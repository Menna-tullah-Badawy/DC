package com.example.myapplicationdc.Activity.Profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModelProvider
import com.example.myapplicationdc.Activity.NavigationButtons.MainActivity
import com.example.myapplicationdc.Domain.PatientModel
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.PatientViewModel
import com.example.myapplicationdc.databinding.ActivityPatientInputBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class PatientInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientInputBinding
    private var imageUri: Uri? = null
    private lateinit var database: DatabaseReference
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var patientViewModel: PatientViewModel

    private val TAG = "PatientInputActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        patientViewModel = ViewModelProvider(this).get(PatientViewModel::class.java)

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("Patients")

        // Set up gender spinner
        val genders = arrayOf("Male", "Female")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, genders)
        binding.spinnerGender.adapter = adapter

        // Set up image picker
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val data = result.data
                    imageUri = data?.data
                    imageUri?.let {
                        binding.imageViewPrescription.setImageURI(it)
                        binding.imageViewPrescription.visibility = View.VISIBLE
                    } ?: run {
                        Log.e(TAG, "Image URI is null.")
                    }
                }
                else -> {
                    Log.e(TAG, "Image picking failed: ${result.resultCode}")
                }
            }
        }

        // Image upload button listener
        binding.btnUploadImage.setOnClickListener {
            openImagePicker()
        }

        // Save patient button listener
        binding.btnSavePatient.setOnClickListener {
            if (validateInputs()) {
                uploadImageToFirebaseStorage()
            }
        }
    }

    // Open the image picker
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun validateInputs(): Boolean {
        val pname = binding.editPatientName.text.toString().trim()
        val ageText = binding.editPatientAge.text.toString().trim()
        val gender = binding.spinnerGender.selectedItem.toString()
        val pationt_address = binding.editPationtAddress.text.toString().trim()
        val pationt_Mobile = binding.editPationtMobile.text.toString().trim()
        val medicalHistory = binding.editMedicalHistory.text.toString().trim()

        return when {
            pname.isEmpty() || ageText.isEmpty() || medicalHistory.isEmpty() || gender.isEmpty() || pationt_address.isEmpty() || pationt_Mobile.isEmpty() -> {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                false
            }
            !ageText.isDigitsOnly() -> {
                Toast.makeText(this, "Please enter a valid age.", Toast.LENGTH_SHORT).show()
                false
            }
            imageUri == null -> {
                Toast.makeText(this, "Please upload a prescription image.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun uploadImageToFirebaseStorage() {
        imageUri?.let { uri ->
            val storageReference = FirebaseStorage.getInstance().getReference("Patients/${System.currentTimeMillis()}")
            storageReference.putFile(uri)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                        savePatientData(downloadUri.toString())
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Failed to get image URL: ${e.message}")
                        Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Image upload failed: ${e.message}")
                    Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: Log.e(TAG, "Image URI is null when trying to upload.")
    }

    private fun savePatientData(imageUrl: String) {
        val pname = binding.editPatientName.text.toString().trim()
        val age = binding.editPatientAge.text.toString().trim().toIntOrNull() ?: 0
        val pationt_address = binding.editPationtAddress.text.toString().trim()
        val pationt_Mobile = binding.editPationtMobile.text.toString().trim().toIntOrNull() ?: 0
        val gender = binding.spinnerGender.selectedItem.toString()
        val medicalHistory = binding.editMedicalHistory.text.toString().trim()

        val patient = PatientModel(
            pname = pname,
            age = age,
            gender = gender,
            pationt_address = pationt_address,
            pationt_Mobile = pationt_Mobile,
            medicalHistory = medicalHistory,
            prescriptionPictures = imageUrl
        )

        // Set patient data in ViewModel
        patientViewModel.setPatientData(patient)

        val patientId = database.push().key ?: return
        database.child(patientId).setValue(patient)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Patient data saved successfully", Toast.LENGTH_SHORT).show()
                    // Redirect to MainActivity and pass the patient ID
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("PATIENT_ID", patientId)
                    startActivity(intent)
                    finish()
                } else {
                    task.exception?.let {
                        Toast.makeText(this, "Failed to save patient data: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}

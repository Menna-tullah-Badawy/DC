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
import com.example.myapplicationdc.Activity.NavigationButtons.MainActivity
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.R
import com.example.myapplicationdc.databinding.ActivityDoctorInputBinding

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DoctorInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorInputBinding
    private var imageUri: Uri? = null
    private lateinit var database: DatabaseReference
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    private val TAG = "DoctorInputActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("Doctors")

        // Setup specialization spinner
        val specializations = arrayOf("Cardiology", "Dentistry", "Neurology", "Orthopedics", "Radiology")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, specializations)
        binding.spinnerSpecialization.adapter = adapter

        // Initialize the activity result launcher for image picking
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val data = result.data
                    imageUri = data?.data
                    imageUri?.let {
                        binding.imageViewDoctor.setImageURI(it)
                        binding.imageViewDoctor.visibility = View.VISIBLE
                    } ?: run {
                        Log.e(TAG, "Image URI is null.")
                    }
                }
                else -> {
                    Log.e(TAG, "Image picking failed: ${result.resultCode}")
                }
            }
        }

        // Handle upload button click to open image picker
        binding.btnUploadDoctorImage.setOnClickListener {
            openImagePicker()
        }

        // Handle save button click
        binding.btnSaveDoctor.setOnClickListener {
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
        pickImageLauncher.launch(intent) // Use the launcher to start the image picker
    }

    // Validate input fields before submission
    private fun validateInputs(): Boolean {
        val name = binding.editDoctorName.text.toString().trim()
        val address = binding.editDoctorAddress.text.toString().trim()
        val experienceText = binding.editDoctorExperience.text.toString().trim()
        val biographysite = binding.editDoctorBiographysite.text.toString().trim()
        val location = binding.editDoctorLocation.text.toString().trim()
        val mobile = binding.editDoctorMobile.text.toString().trim()
        val site = binding.editDoctorSite.text.toString().trim()
        val specialization = binding.spinnerSpecialization.selectedItem.toString()

        return when {
            name.isEmpty() || address.isEmpty() || experienceText.isEmpty() ||biographysite.isEmpty()||
                    location.isEmpty() || mobile.isEmpty() || site.isEmpty() || specialization.isEmpty() -> {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                false
            }
            !experienceText.isDigitsOnly() -> {
                Toast.makeText(this, "Please enter a valid number for experience.", Toast.LENGTH_SHORT).show()
                false
            }
            imageUri == null -> {
                Toast.makeText(this, "Please upload an image.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    // Upload the selected image to Firebase Storage
    private fun uploadImageToFirebaseStorage() {
        imageUri?.let { uri ->
            val storageReference = FirebaseStorage.getInstance().getReference("Doctors/${System.currentTimeMillis()}")
            storageReference.putFile(uri)
                .addOnSuccessListener {
                    // Get the download URL of the uploaded image
                    storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveDoctorData(downloadUri.toString()) // Pass the image URL to saveDoctorData
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

    // Save doctor data to Firebase Database
    private fun saveDoctorData(imageUrl: String) {
        Log.d("Firebase", "Start data saved")
        val name = binding.editDoctorName.text.toString().trim()
        val address = binding.editDoctorAddress.text.toString().trim()
        val experienceText = binding.editDoctorExperience.text.toString().trim().toIntOrNull() ?: 0
        val biographysite = binding.editDoctorBiographysite.text.toString().trim()
        val location = binding.editDoctorLocation.text.toString().trim()
        val mobile = binding.editDoctorMobile.text.toString().trim()
        val site = binding.editDoctorSite.text.toString().trim()
        val specialization = binding.spinnerSpecialization.selectedItem.toString()

        // Prepare doctor data
        val doctor = DoctorModel(
            Name = name,
            Address = address,
            Experience = experienceText,
            Biography = biographysite,
            Location = location,
            Mobile = mobile,
            Site = site,
            Special = specialization,
            Picture = imageUrl // Use the uploaded image URL
        )

        // Generate a unique ID for the doctor and save to Firebase
        val doctorId = database.push().key ?: return
        database.child(doctorId).setValue(doctor)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "Test data saved")
                    Toast.makeText(this, "Doctor data saved successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    task.exception?.let {
                        Log.e("Firebase", "Failed to save test data: ${task.exception?.message}")
                        Log.e(TAG, "Failed to save doctor data: ${it.message}")
                        Toast.makeText(this, "Failed to save doctor data: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}

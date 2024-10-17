package com.example.myapplicationdc.Activity.Profile

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplicationdc.R
import com.example.myapplicationdc.databinding.ActivityPatientProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.logging.Logger
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.bumptech.glide.Glide
import com.example.myapplicationdc.Domain.PatientModel

class PatientProfileActivity : AppCompatActivity() {
        private lateinit var binding: ActivityPatientProfileBinding
        private lateinit var database: DatabaseReference

        private var imagePrescriptionUri: Uri? = null

        private lateinit var pickImagePrescriptionLauncher: ActivityResultLauncher<Intent>
        private lateinit var auth: FirebaseAuth
        private var userEmail: String? = null

        private val genderOptions = arrayOf("Male", "Female")

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityPatientProfileBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Initialize Firebase Auth
            auth = FirebaseAuth.getInstance()

            // Only initialize the database once, and dynamically update it based on context
            database = FirebaseDatabase.getInstance().reference



            pickImagePrescriptionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    imagePrescriptionUri = result.data?.data
                    imagePrescriptionUri?.let {
                        binding.imageViewPrescription.setImageURI(it)
                        binding.imageViewPrescription.visibility = View.VISIBLE
                    } ?: run {
                        Logger.getLogger(PatientProfileActivity::class.java.name).warning("Prescription image URI is null")
                    }
                }
            }

            val currentUser = auth.currentUser

            if (currentUser != null) {
                userEmail = currentUser.email?.replace(".", "_")
                loadData()
            } else {
                Log.e("AllProfilesActivity", "No user is signed in")
                return
            }

            setupGenderSpinner()

            binding.btnUploadImage.setOnClickListener {
                pickImagePrescription()
            }
            binding.btnSavePatient.setOnClickListener {
                saveData()
                savePatientData()
                uploadPrescriptionImageToFirebaseStorage()
                saveData()

                loadData()

            }
        }

        private fun setupGenderSpinner() {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerGender.adapter = adapter
        }



        private fun saveData() {
            val sharedPreferences = getEncryptedSharedPreferences()
            val editor = sharedPreferences.edit()

            deleteProfileData()

            editor.putString("${userEmail}_name", binding.editPatientName.text.toString())
            editor.putString("${userEmail}_age", binding.editPatientAge.text.toString())
            editor.putString("${userEmail}_gender", binding.spinnerGender.selectedItem.toString())
            editor.putString("${userEmail}_address", binding.editPationtAddress.text.toString())
            editor.putString("${userEmail}_mobile", binding.editPationtMobile.text.toString())
            editor.putString("${userEmail}_medicalHistory", binding.editMedicalHistory.text.toString())
            editor.putString("${userEmail}_prescriptionPictures", imagePrescriptionUri?.toString())

            editor.apply()
        }

        private fun loadData() {
            val sharedPreferences = getEncryptedSharedPreferences()

            binding.editPatientName.setText(sharedPreferences.getString("${userEmail}_name", ""))
            binding.editPatientAge.setText(sharedPreferences.getString("${userEmail}_age", ""))
            binding.spinnerGender.setSelection(getGenderPosition(sharedPreferences.getString("${userEmail}_gender", "")))
            binding.editPationtAddress.setText(sharedPreferences.getString("${userEmail}_address", ""))
            binding.editPationtMobile.setText(sharedPreferences.getString("${userEmail}_mobile", ""))
            binding.editMedicalHistory.setText(sharedPreferences.getString("${userEmail}_medicalHistory", ""))


            val prescriptionImageUrl = sharedPreferences.getString("${userEmail}_prescriptionPictures", null)
            if (prescriptionImageUrl != null) {
                Glide.with(this)
                    .load(prescriptionImageUrl)
                    .placeholder(R.drawable.button_bg)
                    .into(binding.imageViewPrescription)
                binding.imageViewPrescription.visibility = View.VISIBLE
            } else {
                binding.imageViewPrescription.visibility = View.GONE
            }
        }



        private fun getGenderPosition(gender: String?): Int {
            return genderOptions.indexOf(gender)
        }



        private fun getEncryptedSharedPreferences(): SharedPreferences {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            return EncryptedSharedPreferences.create(
                "MyPrefs",
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        private fun deleteProfileData() {
            val sharedPreferences = getEncryptedSharedPreferences()
            val editor = sharedPreferences.edit()

            editor.remove("${userEmail}_name")
            editor.remove("${userEmail}_age")
            editor.remove("${userEmail}_gender")
            editor.remove("${userEmail}_address")
            editor.remove("${userEmail}_mobile")
            editor.remove("${userEmail}_medicalHistory")
            editor.remove("${userEmail}_prescriptionPictures")
            editor.apply()
        }


        private fun pickImagePrescription() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImagePrescriptionLauncher.launch(intent)
        }


        private fun uploadPrescriptionImageToFirebaseStorage() {
            imagePrescriptionUri?.let { uri ->
                val storageRef = FirebaseStorage.getInstance().reference.child("prescription_pictures/${System.currentTimeMillis()}.jpg")
                val uploadTask = storageRef.putFile(uri)

                uploadTask.addOnSuccessListener {
                    saveImageUri(uri, "prescriptionPictures")
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val sharedPreferences = getEncryptedSharedPreferences()
                        val editor = sharedPreferences.edit()
                        editor.putString("${userEmail}_prescriptionPictures", downloadUrl.toString())
                        editor.apply()

                        Toast.makeText(this, "Prescription image uploaded successfully.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to upload prescription image: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }


        private fun saveImageUri(uri: Uri?, key: String) {
            val sharedPreferences = getEncryptedSharedPreferences()
            val editor = sharedPreferences.edit()
            editor.putString("${userEmail}_$key", uri.toString())
            editor.apply()
        }





        private fun savePatientData() {
            val Patient = PatientModel(
                name = binding.editPatientName.text.toString(),
                age = binding.editPatientAge.text.toString().toIntOrNull() ?: 0,
                gender = binding.spinnerGender.selectedItem.toString(),
                address = binding.editPationtAddress.text.toString(),
                mobile = binding.editPationtMobile.text.toString().toIntOrNull() ?: 0,
                medicalHistory = binding.editMedicalHistory.text.toString(),
                prescriptionPictures = imagePrescriptionUri.toString()
            )

            val userId = auth.currentUser?.uid
            userId?.let {
                database.child("Patients").child(it).setValue(Patient)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Patient data saved successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("AllProfilesActivity", "Error saving Patient data: ${e.message}")
                    }
            } ?: run {
                Log.e("AllProfilesActivity", "User is not authenticated")
            }
        }
    }


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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.bumptech.glide.Glide
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.R
import com.example.myapplicationdc.databinding.ActivityDoctorProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.logging.Logger

class DoctorProfileActivity : AppCompatActivity() {

        private lateinit var binding: ActivityDoctorProfileBinding
        private lateinit var database: DatabaseReference

        private var imageUri: Uri? = null

        private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
        private lateinit var auth: FirebaseAuth
        private var userEmail: String? = null

        private val specializationOptions = arrayOf("Cardiology", "Dentistry", "Neurology", "Orthopedics", "Radiology")

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityDoctorProfileBinding.inflate(layoutInflater)
            setContentView(binding.root)


            auth = FirebaseAuth.getInstance()


            database = FirebaseDatabase.getInstance().reference

            pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    imageUri = result.data?.data
                    imageUri?.let {
                        binding.imageViewDoctor.setImageURI(it)
                        binding.imageViewDoctor.visibility = View.VISIBLE
                    } ?: run {
                        Logger.getLogger(DoctorProfileActivity::class.java.name).warning("Image URI is null")
                    }
                }
            }



            val currentUser = auth.currentUser

            if (currentUser != null) {
                userEmail = currentUser.email?.replace(".", "_")
                loadData()
            } else {
                Log.e("DoctorProfile", "No user is signed in")
                return
            }

            setupSpecializationSpinner()
            binding.btnUploadDoctorImage.setOnClickListener {
                pickImage()
            }

            binding.btnSaveDoctor.setOnClickListener {
                saveData()
                saveDoctorData()
                uploadImageToFirebaseStorage()
                saveData()

                loadData()

            }
        }



        private fun setupSpecializationSpinner() {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, specializationOptions)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerSpecialization.adapter = adapter
        }

        private fun saveData() {
            val sharedPreferences = getEncryptedSharedPreferences()
            val editor = sharedPreferences.edit()

            deleteProfileData()

            editor.putString("${userEmail}_name", binding.editDoctorName.text.toString())
            editor.putString("${userEmail}_special", binding.spinnerSpecialization.selectedItem.toString())
            editor.putString("${userEmail}_address", binding.editDoctorAddress.text.toString())
            editor.putString("${userEmail}_mobile", binding.editDoctorMobile.text.toString())
            editor.putString("${userEmail}_experience", binding.editDoctorExperience.text.toString())
            editor.putString("${userEmail}_location", binding.editDoctorLocation.text.toString())
            editor.putString("${userEmail}_site", binding.editDoctorSite.text.toString())
            editor.putString("${userEmail}_biography", binding.editDoctorBiographysite.text.toString())
            editor.putString("${userEmail}_picture", imageUri?.toString())

            editor.apply()
        }

        private fun loadData() {
            val sharedPreferences = getEncryptedSharedPreferences()

            binding.editDoctorName.setText(sharedPreferences.getString("${userEmail}_name", ""))
            binding.spinnerSpecialization.setSelection(getSpecializationPosition(sharedPreferences.getString("${userEmail}_special", "")))
            binding.editDoctorAddress.setText(sharedPreferences.getString("${userEmail}_address", ""))
            binding.editDoctorMobile.setText(sharedPreferences.getString("${userEmail}_mobile", ""))
            binding.editDoctorExperience.setText(sharedPreferences.getString("${userEmail}_experience", ""))
            binding.editDoctorLocation.setText(sharedPreferences.getString("${userEmail}_location", ""))
            binding.editDoctorSite.setText(sharedPreferences.getString("${userEmail}_site", ""))
            binding.editDoctorBiographysite.setText(sharedPreferences.getString("${userEmail}_biography", ""))

            val imageUrl = sharedPreferences.getString("${userEmail}_imageUrl", null)
            if (imageUrl != null) {
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.button_bg)
                    .into(binding.imageViewDoctor)
                binding.imageViewDoctor.visibility = View.VISIBLE
            } else {
                binding.imageViewDoctor.visibility = View.GONE
            }


        }




        private fun getSpecializationPosition(specialization: String?): Int {
            return specializationOptions.indexOf(specialization)
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
            editor.remove("${userEmail}_special")
            editor.remove("${userEmail}_address")
            editor.remove("${userEmail}_mobile")
            editor.remove("${userEmail}_experience")
            editor.remove("${userEmail}_location")
            editor.remove("${userEmail}_site")
            editor.remove("${userEmail}_biography")
            editor.remove("${userEmail}_picture")
            editor.apply()
        }
        private fun pickImage() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }


        private fun uploadImageToFirebaseStorage() {
            imageUri?.let { uri ->
                val storageRef = FirebaseStorage.getInstance().reference.child("doctor_profile_pictures/${System.currentTimeMillis()}.jpg")
                val uploadTask = storageRef.putFile(uri)

                uploadTask.addOnSuccessListener {
                    saveImageUri(uri, "picture")
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val sharedPreferences = getEncryptedSharedPreferences()
                        val editor = sharedPreferences.edit()
                        editor.putString("${userEmail}_imageUrl", downloadUrl.toString())
                        editor.apply()

                        Toast.makeText(this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }



        private fun saveImageUri(uri: Uri?, key: String) {
            val sharedPreferences = getEncryptedSharedPreferences()
            val editor = sharedPreferences.edit()
            editor.putString("${userEmail}_$key", uri.toString())
            editor.apply()
        }

        private fun saveDoctorData() {
            val doctor = DoctorModel(
                name = binding.editDoctorName.text.toString(),
                address = binding.editDoctorAddress.text.toString(),
                biography = binding.editDoctorBiographysite.text.toString(),
                mobile = binding.editDoctorMobile.text.toString(),
                special = binding.spinnerSpecialization.selectedItem.toString(),
                experience = binding.editDoctorExperience.text.toString().toIntOrNull() ?: 0,
                location = binding.editDoctorLocation.text.toString(),
                site = binding.editDoctorSite.text.toString(),
                picture = imageUri.toString()
            )

            val userId = auth.currentUser?.uid
            userId?.let {
                database.child("Doctors").child(it).setValue(doctor)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Doctor data saved successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("AllProfilesActivity", "Error saving doctor data: ${e.message}")
                    }
            } ?: run {
                Log.e("AllProfilesActivity", "User is not authenticated")
            }
        }




    }

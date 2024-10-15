package com.example.myapplicationdc.Activity.Profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.util.Log
import com.example.myapplicationdc.R
import com.example.myapplicationdc.databinding.ActivityAllProfilesBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.io.File
import java.io.FileOutputStream

class AllProfilesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllProfilesBinding
    private var imageUri: Uri? = null
    private var prescriptionImageUri: Uri? = null

    private val genderOptions = arrayOf("Male", "Female", "Other")
    private val specializationOptions = arrayOf("Cardiology", "Dentistry", "Neurology", "Orthopedics", "Radiology")

    private lateinit var auth: FirebaseAuth
    private var userEmail: String? = null

    private val getImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val savedImagePath = saveImageToInternalStorage(it)
            binding.imageView.setImageURI(Uri.parse(savedImagePath))
            binding.imageView.visibility = View.VISIBLE
            imageUri = Uri.parse(savedImagePath)
        }
    }

    private val getUploadPrescriptionPictures = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val savedPrescriptionPath = saveImageToInternalStorage(it, "prescription_image.jpg")
            binding.imageViewprescriptionPictures.setImageURI(Uri.parse(savedPrescriptionPath))
            binding.imageViewprescriptionPictures.visibility = View.VISIBLE
            prescriptionImageUri = Uri.parse(savedPrescriptionPath)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllProfilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Get current user
        val currentUser = auth.currentUser

        // Check if user is logged in
        if (currentUser != null) {
            userEmail = currentUser.email?.replace(".", "_") // Use email as a unique identifier
            loadData() // Load the correct user data based on the email
        } else {
            // No user is signed in, redirect to login or show a message
            Log.e("AllProfilesActivity", "No user is signed in")
            return // or startActivity for login
        }

        setupGenderSpinner()
        setupSpecializationSpinner()

        binding.btnSave.setOnClickListener {
            saveData()
        }

        binding.btnUploadImage.setOnClickListener {
            getImageLauncher.launch("image/*")
        }

        binding.btnUploadprescriptionPictures.setOnClickListener {
            getUploadPrescriptionPictures.launch("image/*")
        }
    }

    private fun setupGenderSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
    }

    private fun setupSpecializationSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, specializationOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSpecialization.adapter = adapter
    }

    // Save user data based on the userEmail
    private fun saveData() {
        val sharedPreferences = getEncryptedSharedPreferences()
        val editor = sharedPreferences.edit()
        deleteProfileData()
        Log.e("SaveData", "${userEmail}_name: ${binding.editName.text.toString()}")
        editor.putString("${userEmail}_name", binding.editName.text.toString())
        editor.putString("${userEmail}_age", binding.editAge.text.toString())
        editor.putString("${userEmail}_special", binding.spinnerSpecialization.selectedItem.toString())
        editor.putString("${userEmail}_gender", binding.spinnerGender.selectedItem.toString())
        editor.putString("${userEmail}_address", binding.editAddress.text.toString())
        editor.putString("${userEmail}_mobile", binding.editMobile.text.toString())
        editor.putString("${userEmail}_medicalHistory", binding.editMedicalHistory.text.toString())
        editor.putString("${userEmail}_experience", binding.editExperience.text.toString())
        editor.putString("${userEmail}_location", binding.editDoctorLocation.text.toString())
        editor.putString("${userEmail}_website", binding.editDoctorSite.text.toString())
        editor.putString("${userEmail}_biographysite", binding.editDoctorBiographysite.text.toString())
        editor.putString("${userEmail}_pictureUri", imageUri?.toString())
        editor.putString("${userEmail}_prescriptionPictureUri", prescriptionImageUri?.toString())

        editor.apply() // Save the changes
    }

    // Load user data based on the userEmail
    private fun loadData() {
        val sharedPreferences = getEncryptedSharedPreferences()

        binding.editName.setText(sharedPreferences.getString("${userEmail}_name", ""))
        binding.editAge.setText(sharedPreferences.getString("${userEmail}_age", ""))
        binding.spinnerGender.setSelection(getGenderPosition(sharedPreferences.getString("${userEmail}_gender", "")))
        binding.spinnerSpecialization.setSelection(getSpecializationPosition(sharedPreferences.getString("${userEmail}_special", "")))
        binding.editAddress.setText(sharedPreferences.getString("${userEmail}_address", ""))
        binding.editMobile.setText(sharedPreferences.getString("${userEmail}_mobile", ""))
        binding.editMedicalHistory.setText(sharedPreferences.getString("${userEmail}_medicalHistory", ""))
        binding.editExperience.setText(sharedPreferences.getString("${userEmail}_experience", ""))
        binding.editDoctorLocation.setText(sharedPreferences.getString("${userEmail}_location", ""))
        binding.editDoctorSite.setText(sharedPreferences.getString("${userEmail}_website", ""))
        binding.editDoctorBiographysite.setText(sharedPreferences.getString("${userEmail}_biographysite", ""))

        val pictureUriString = sharedPreferences.getString("${userEmail}_pictureUri", null)
        val prescriptionUriString = sharedPreferences.getString("${userEmail}_prescriptionPictureUri", null)

        if (pictureUriString != null) {
            imageUri = Uri.parse(pictureUriString)
            binding.imageView.setImageURI(imageUri)
            binding.imageView.visibility = View.VISIBLE
        } else {
            binding.imageView.visibility = View.GONE
        }

        if (prescriptionUriString != null) {
            prescriptionImageUri = Uri.parse(prescriptionUriString)
            binding.imageViewprescriptionPictures.setImageURI(prescriptionImageUri)
            binding.imageViewprescriptionPictures.visibility = View.VISIBLE
        } else {
            binding.imageViewprescriptionPictures.visibility = View.GONE
        }
    }

    private fun getGenderPosition(gender: String?): Int {
        return genderOptions.indexOf(gender)
    }

    private fun getSpecializationPosition(specialization: String?): Int {
        return specializationOptions.indexOf(specialization)
    }

    private fun saveImageToInternalStorage(uri: Uri, fileName: String = "profile_image.jpg"): String {
        val inputStream = contentResolver.openInputStream(uri)
        val imageFile = File(filesDir, fileName)
        val outputStream = FileOutputStream(imageFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input?.copyTo(output)
            }
        }
        return imageFile.absolutePath
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

        // Remove the user data associated with the current email
        editor.remove("${userEmail}_name")
        editor.remove("${userEmail}_age")
        editor.remove("${userEmail}_special")
        editor.remove("${userEmail}_gender")
        editor.remove("${userEmail}_address")
        editor.remove("${userEmail}_mobile")
        editor.remove("${userEmail}_medicalHistory")
        editor.remove("${userEmail}_experience")
        editor.remove("${userEmail}_location")
        editor.remove("${userEmail}_website")
        editor.remove("${userEmail}_biographysite")
        editor.remove("${userEmail}_pictureUri")
        editor.remove("${userEmail}_prescriptionPictureUri")

        editor.apply() // Apply the changes to remove the data
    }
}

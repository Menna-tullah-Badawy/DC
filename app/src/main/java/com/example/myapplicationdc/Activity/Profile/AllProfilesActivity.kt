//package com.example.myapplicationdc.Activity.Profile
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.view.View
//import android.widget.ArrayAdapter
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import android.content.SharedPreferences
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.result.ActivityResultLauncher
//import com.example.myapplicationdc.R
//import com.example.myapplicationdc.databinding.ActivityAllProfilesBinding
//import com.google.firebase.auth.FirebaseAuth
//import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKeys
//import com.bumptech.glide.Glide
//import com.example.myapplicationdc.Domain.DoctorModel
//import com.example.myapplicationdc.Domain.PatientModel
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
//import java.util.logging.Logger
//
//class AllProfilesActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityAllProfilesBinding
//    private lateinit var database: DatabaseReference
//
//    private var imageUri: Uri? = null
//    private var imagePrescriptionUri: Uri? = null
//
//    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
//    private lateinit var pickImagePrescriptionLauncher: ActivityResultLauncher<Intent>
//    private lateinit var auth: FirebaseAuth
//    private var userEmail: String? = null
//
//    private val genderOptions = arrayOf("Male", "Female")
//    private val specializationOptions = arrayOf("Cardiology", "Dentistry", "Neurology", "Orthopedics", "Radiology")
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityAllProfilesBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Initialize Firebase Auth
//        auth = FirebaseAuth.getInstance()
//
//        // Only initialize the database once, and dynamically update it based on context
//        database = FirebaseDatabase.getInstance().reference
//
//        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                imageUri = result.data?.data
//                imageUri?.let {
//                    binding.imageView.setImageURI(it)
//                    binding.imageView.visibility = View.VISIBLE
//                } ?: run {
//                    Logger.getLogger(AllProfilesActivity::class.java.name).warning("Image URI is null")
//                }
//            }
//        }
//
//        pickImagePrescriptionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                imagePrescriptionUri = result.data?.data
//                imagePrescriptionUri?.let {
//                    binding.imageViewprescriptionPictures.setImageURI(it)
//                    binding.imageViewprescriptionPictures.visibility = View.VISIBLE
//                } ?: run {
//                    Logger.getLogger(AllProfilesActivity::class.java.name).warning("Prescription image URI is null")
//                }
//            }
//        }
//
//        val currentUser = auth.currentUser
//
//        if (currentUser != null) {
//            userEmail = currentUser.email?.replace(".", "_")
//            loadData()
//        } else {
//            Log.e("AllProfilesActivity", "No user is signed in")
//            return
//        }
//
//        setupGenderSpinner()
//        setupSpecializationSpinner()
//binding.btnUploadImage.setOnClickListener {
//    pickImage()
//}
//        binding.btnUploadprescriptionPictures.setOnClickListener {
//            pickImagePrescription()
//        }
//        binding.btnSave.setOnClickListener {
//            saveData()
//            savePatientData()
//            saveDoctorData()
//            uploadImageToFirebaseStorage()
//            uploadPrescriptionImageToFirebaseStorage()
//            saveData()
//
//            loadData()
//
//        }
//    }
//
//    private fun setupGenderSpinner() {
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerGender.adapter = adapter
//    }
//
//    private fun setupSpecializationSpinner() {
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, specializationOptions)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerSpecialization.adapter = adapter
//    }
//
//    private fun saveData() {
//        val sharedPreferences = getEncryptedSharedPreferences()
//        val editor = sharedPreferences.edit()
//
//        deleteProfileData()
//
//        editor.putString("${userEmail}_name", binding.editName.text.toString())
//        editor.putString("${userEmail}_age", binding.editAge.text.toString())
//        editor.putString("${userEmail}_special", binding.spinnerSpecialization.selectedItem.toString())
//        editor.putString("${userEmail}_gender", binding.spinnerGender.selectedItem.toString())
//        editor.putString("${userEmail}_address", binding.editAddress.text.toString())
//        editor.putString("${userEmail}_mobile", binding.editMobile.text.toString())
//        editor.putString("${userEmail}_medicalHistory", binding.editMedicalHistory.text.toString())
//        editor.putString("${userEmail}_experience", binding.editExperience.text.toString())
//        editor.putString("${userEmail}_location", binding.editDoctorLocation.text.toString())
//        editor.putString("${userEmail}_site", binding.editDoctorSite.text.toString())
//        editor.putString("${userEmail}_biography", binding.editAllDoctorBiographysite.text.toString())
//        editor.putString("${userEmail}_picture", imageUri?.toString())
//        editor.putString("${userEmail}_prescriptionPictures", imagePrescriptionUri?.toString())
//
//        editor.apply()
//    }
//
//    private fun loadData() {
//        val sharedPreferences = getEncryptedSharedPreferences()
//
//        binding.editName.setText(sharedPreferences.getString("${userEmail}_name", ""))
//        binding.editAge.setText(sharedPreferences.getString("${userEmail}_age", ""))
//        binding.spinnerGender.setSelection(getGenderPosition(sharedPreferences.getString("${userEmail}_gender", "")))
//        binding.spinnerSpecialization.setSelection(getSpecializationPosition(sharedPreferences.getString("${userEmail}_special", "")))
//        binding.editAddress.setText(sharedPreferences.getString("${userEmail}_address", ""))
//        binding.editMobile.setText(sharedPreferences.getString("${userEmail}_mobile", ""))
//        binding.editMedicalHistory.setText(sharedPreferences.getString("${userEmail}_medicalHistory", ""))
//        binding.editExperience.setText(sharedPreferences.getString("${userEmail}_experience", ""))
//        binding.editDoctorLocation.setText(sharedPreferences.getString("${userEmail}_location", ""))
//        binding.editDoctorSite.setText(sharedPreferences.getString("${userEmail}_site", ""))
//        binding.editAllDoctorBiographysite.setText(sharedPreferences.getString("${userEmail}_biography", ""))
//
//        val imageUrl = sharedPreferences.getString("${userEmail}_imageUrl", null)
//        if (imageUrl != null) {
//            Glide.with(this)
//                .load(imageUrl)
//                .placeholder(R.drawable.button_bg)
//                .into(binding.imageView)
//            binding.imageView.visibility = View.VISIBLE
//        } else {
//            binding.imageView.visibility = View.GONE
//        }
//
//        val prescriptionImageUrl = sharedPreferences.getString("${userEmail}_prescriptionPictures", null)
//        if (prescriptionImageUrl != null) {
//            Glide.with(this)
//                .load(prescriptionImageUrl)
//                .placeholder(R.drawable.button_bg)
//                .into(binding.imageViewprescriptionPictures)
//            binding.imageViewprescriptionPictures.visibility = View.VISIBLE
//        } else {
//            binding.imageViewprescriptionPictures.visibility = View.GONE
//        }
//    }
//
//
//
//    private fun getGenderPosition(gender: String?): Int {
//        return genderOptions.indexOf(gender)
//    }
//
//    private fun getSpecializationPosition(specialization: String?): Int {
//        return specializationOptions.indexOf(specialization)
//    }
//
//    private fun getEncryptedSharedPreferences(): SharedPreferences {
//        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//
//        return EncryptedSharedPreferences.create(
//            "MyPrefs",
//            masterKeyAlias,
//            this,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
//    }
//
//    private fun deleteProfileData() {
//        val sharedPreferences = getEncryptedSharedPreferences()
//        val editor = sharedPreferences.edit()
//
//        editor.remove("${userEmail}_name")
//        editor.remove("${userEmail}_age")
//        editor.remove("${userEmail}_special")
//        editor.remove("${userEmail}_gender")
//        editor.remove("${userEmail}_address")
//        editor.remove("${userEmail}_mobile")
//        editor.remove("${userEmail}_medicalHistory")
//        editor.remove("${userEmail}_experience")
//        editor.remove("${userEmail}_location")
//        editor.remove("${userEmail}_site")
//        editor.remove("${userEmail}_biography")
//        editor.remove("${userEmail}_picture")
//        editor.remove("${userEmail}_prescriptionPictures")
//        editor.apply()
//    }
//    private fun pickImage() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        pickImageLauncher.launch(intent)
//    }
//
//    private fun pickImagePrescription() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        pickImagePrescriptionLauncher.launch(intent)
//    }
//    private fun uploadImageToFirebaseStorage() {
//        imageUri?.let { uri ->
//            val storageRef = FirebaseStorage.getInstance().reference.child("doctor_profile_pictures/${System.currentTimeMillis()}.jpg")
//            val uploadTask = storageRef.putFile(uri)
//
//            uploadTask.addOnSuccessListener {
//                saveImageUri(uri, "picture")
//                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
//                    val sharedPreferences = getEncryptedSharedPreferences()
//                    val editor = sharedPreferences.edit()
//                    editor.putString("${userEmail}_imageUrl", downloadUrl.toString())
//                    editor.apply()
//
//                    Toast.makeText(this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show()
//                }
//            }.addOnFailureListener {
//                Toast.makeText(this, "Failed to upload image: ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun uploadPrescriptionImageToFirebaseStorage() {
//        imagePrescriptionUri?.let { uri ->
//            val storageRef = FirebaseStorage.getInstance().reference.child("prescription_pictures/${System.currentTimeMillis()}.jpg")
//            val uploadTask = storageRef.putFile(uri)
//
//            uploadTask.addOnSuccessListener {
//                saveImageUri(uri, "prescriptionPictures")
//                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
//                    val sharedPreferences = getEncryptedSharedPreferences()
//                    val editor = sharedPreferences.edit()
//                    editor.putString("${userEmail}_prescriptionPictures", downloadUrl.toString())
//                    editor.apply()
//
//                    Toast.makeText(this, "Prescription image uploaded successfully.", Toast.LENGTH_SHORT).show()
//                }
//            }.addOnFailureListener {
//                Toast.makeText(this, "Failed to upload prescription image: ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//    private fun saveImageUri(uri: Uri?, key: String) {
//        val sharedPreferences = getEncryptedSharedPreferences()
//        val editor = sharedPreferences.edit()
//        editor.putString("${userEmail}_$key", uri.toString())
//        editor.apply()
//    }
//
//    private fun saveDoctorData() {
//        val doctor = DoctorModel(
//            name = binding.editName.text.toString(),
//            address = binding.editAddress.text.toString(),
//            biography = binding.editAllDoctorBiographysite.text.toString(),
//            mobile = binding.editMobile.text.toString(),
//            special = binding.spinnerSpecialization.selectedItem.toString(),
//            experience = binding.editExperience.text.toString().toIntOrNull() ?: 0,
//            location = binding.editDoctorLocation.text.toString(),
//            site = binding.editDoctorSite.text.toString(),
//            picture = imageUri.toString()
//        )
//
//        val userId = auth.currentUser?.uid
//        userId?.let {
//            database.child("Doctors").child(it).setValue(doctor)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "Doctor data saved successfully.", Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
//                    Log.e("AllProfilesActivity", "Error saving doctor data: ${e.message}")
//                }
//        } ?: run {
//            Log.e("AllProfilesActivity", "User is not authenticated")
//        }
//    }
//
//    private fun savePatientData() {
//        val Patient = PatientModel(
//            name = binding.editName.text.toString(),
//            age = binding.editAge.text.toString().toIntOrNull() ?: 0,
//            gender = binding.spinnerGender.selectedItem.toString(),
//            address = binding.editAddress.text.toString(),
//            mobile = binding.editMobile.text.toString().toIntOrNull() ?: 0,
//            medicalHistory = binding.editMedicalHistory.text.toString(),
//            prescriptionPictures = imagePrescriptionUri.toString()
//        )
//
//        val userId = auth.currentUser?.uid
//        userId?.let {
//            database.child("Patients").child(it).setValue(Patient)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "Patient data saved successfully.", Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
//                    Log.e("AllProfilesActivity", "Error saving Patient data: ${e.message}")
//                }
//        } ?: run {
//            Log.e("AllProfilesActivity", "User is not authenticated")
//        }
//    }
//}

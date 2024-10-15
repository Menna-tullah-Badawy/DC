//package com.example.myapplicationdc.Activity.Profile
//
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import com.bumptech.glide.Glide
//import com.example.myapplicationdc.Domain.PatientModel
//import com.example.myapplicationdc.R
//import com.example.myapplicationdc.ViewModel.PatientViewModel
//import com.example.myapplicationdc.databinding.ActivityPatientProfileBinding
//import com.google.firebase.database.*
//class PatientProfileActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityPatientProfileBinding
//    private lateinit var database: DatabaseReference
//    private lateinit var patientViewModel: PatientViewModel
//    private var patientId: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPatientProfileBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Initialize ViewModel
//        patientViewModel = ViewModelProvider(this).get(PatientViewModel::class.java)
//
//        // Observe patient data
//        patientViewModel.patientData.observe(this, Observer { patient ->
//            patient?.let {
//                displayPatientData(it)
//            }
//        })
//
//        database = FirebaseDatabase.getInstance().getReference("Patients")
//
//        patientId = intent.getStringExtra("PATIENT_ID")
//
//        patientId?.let {
//            readPatientData()
//        } ?: run {
//            Toast.makeText(this, "No patient ID provided.", Toast.LENGTH_SHORT).show()
//            finish()
//        }
//    }
//
//    private fun readPatientData() {
//        database.child(patientId!!).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    val patient = snapshot.getValue(PatientModel::class.java)
//                    patient?.let {
//                        patientViewModel.setPatientData(it)
//                    }
//                } else {
//                    Toast.makeText(this@PatientProfileActivity, "Patient data not found.", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@PatientProfileActivity, "Failed to load patient data: ${error.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun displayPatientData(patient: PatientModel) {
//        binding.pname.text = patient.pname
//        binding.age.text = patient.age.toString()
//        binding.gender.text = patient.gender
//        binding.textPationtAddress.text = patient.pationt_address
//        binding.textPationtMobile.text = patient.pationt_Mobile.toString()
//        binding.medicalHistory.text = patient.medicalHistory
//
//        Glide.with(this).load(patient.prescriptionPictures).into(binding.viewprescriptionPictures)
//    }
//}

package com.example.myapplicationdc.Activity.Profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplicationdc.databinding.ActivityPatientProfileBinding
import com.example.myapplicationdc.Domain.PatientModel
import com.google.firebase.database.*

class PatientProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientProfileBinding
    private lateinit var database: DatabaseReference
    private lateinit var patientId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPatientProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patientId = intent.getStringExtra("PATIENT_ID") ?: return

        database = FirebaseDatabase.getInstance().getReference("Patients")

        readPatientData()
    }

    private fun readPatientData() {
        database.child(patientId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val patient = snapshot.getValue(PatientModel::class.java)
                    patient?.let {
                        displayPatientData(it)
                    } ?: run {
                        Toast.makeText(this@PatientProfileActivity, "Failed to retrieve patient data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@PatientProfileActivity, "Patient is not present", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PatientProfileActivity, "Failed to read data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayPatientData(patient: PatientModel) {
        binding.pname.text = patient.pname
        binding.age.text = patient.age.toString()
        binding.gender.text = patient.gender
        binding.textPationtAddress.text = patient.pationt_address
        binding.textPationtMobile.text = patient.pationt_Mobile.toString()
        binding.medicalHistory.text = patient.medicalHistory


        val imageUrl = patient.prescriptionPictures
        if (imageUrl.isNotEmpty()) {
            loadImageFromStorage(imageUrl)
        }
    }

    private fun loadImageFromStorage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .into(binding.viewprescriptionPictures)
    }
}

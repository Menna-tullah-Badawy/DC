package com.example.myapplicationdc.Activity.Profile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.R
//import com.example.myapplicationdc.ViewModel.DoctorViewModel
import com.example.myapplicationdc.databinding.ActivityDoctorProfileBinding
import com.google.firebase.database.*

class DoctorProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorProfileBinding
    private lateinit var database: DatabaseReference
//    private lateinit var doctorViewModel: DoctorViewModel
    private var doctorId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        doctorViewModel = ViewModelProvider(this).get(DoctorViewModel::class.java)

        database = FirebaseDatabase.getInstance().getReference("Doctors")
        doctorId = intent.getStringExtra("DOCTOR_ID")
        Log.d("DoctorProfileActivity", "Doctor ID: $doctorId")

        if (doctorId != null) {
            fetchDoctorData()
        } else {
            showToast("Doctor ID is missing")
            finish()
        }
    }

    private fun fetchDoctorData() {
        Log.d("Firebase", "Start fetching doctor data")
        doctorId?.let { id ->
            database.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val doctorName = snapshot.child("name").getValue(String::class.java)
                        val doctorAddress = snapshot.child("address").getValue(String::class.java)
                        val doctorSpecialization = snapshot.child("special").getValue(String::class.java)
                        val doctorExperience = snapshot.child("experience").getValue(Int::class.java)
                        val doctorBiography = snapshot.child("biography").getValue(String::class.java)
                        val doctorMobile = snapshot.child("mobile").getValue(String::class.java)
                        val doctorPicture = snapshot.child("picture").getValue(String::class.java)

                        if (doctorName != null) {
                            updateUI(doctorName, doctorAddress, doctorSpecialization, doctorExperience, doctorBiography, doctorMobile, doctorPicture)
                        } else {
                            showToast("Doctor not found")
                        }
                    } else {
                        showToast("Doctor not found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching data: ${error.message}")
                    showToast("Error fetching data: ${error.message}")
                }
            })
        }
    }

    private fun updateUI(name: String, address: String?, specialization: String?, experience: Int?, biography: String?, mobile: String?, pictureUrl: String?) {
        binding.textDoctorName.text = name
        binding.textDoctorAddress.text = address
        binding.textDoctorSpecialization.text = specialization
        binding.textDoctorExperience.text = experience?.toString() ?: "N/A"
        binding.textDoctorBiography.text = biography
        binding.textDoctorMobile.text = mobile

        pictureUrl?.let {
            Glide.with(this).load(it).into(binding.doctorProfilePicture)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

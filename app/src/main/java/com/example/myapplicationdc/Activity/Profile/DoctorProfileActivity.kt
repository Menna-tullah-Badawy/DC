//package com.example.myapplicationdc.Activity.Profile
//
//import android.content.Context
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.example.myapplicationdc.R
//
//class DoctorProfileActivity : AppCompatActivity() {
//
//    private lateinit var sharedPreferences: SharedPreferences
//
//    private lateinit var textViewName: TextView
//    private lateinit var textViewEmail: TextView
//    private lateinit var textViewAddress: TextView
//    private lateinit var textViewMobile: TextView
//    private lateinit var textViewSpecialization: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_doctor_profile)
//
//        // Initialize SharedPreferences
//        sharedPreferences = getSharedPreferences("userProfile", Context.MODE_PRIVATE)
//
//        // Initialize TextViews
//        textViewName = findViewById(R.id.textViewName)
//        textViewEmail = findViewById(R.id.textViewEmail)
//        textViewAddress = findViewById(R.id.textViewAddress)
//        textViewMobile = findViewById(R.id.textViewMobile)
//        textViewSpecialization = findViewById(R.id.textViewSpecialization)
//
//        // Load doctor data
//        loadDoctorData()
//    }
//
//    private fun loadDoctorData() {
//        // Retrieve data from SharedPreferences
//        val name = sharedPreferences.getString("name", "N/A")
//        val email = sharedPreferences.getString("email", "N/A")
//        val address = sharedPreferences.getString("address", "N/A")
//        val mobile = sharedPreferences.getString("mobile", "N/A")
//        val specialization = sharedPreferences.getString("specialization", "N/A")
//
//        // Set data to TextViews
//        textViewName.text = name
//        textViewEmail.text = email
//        textViewAddress.text = address
//        textViewMobile.text = mobile
//        textViewSpecialization.text = specialization
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // Refresh data if the activity is resumed
//        loadDoctorData()
//    }
//}

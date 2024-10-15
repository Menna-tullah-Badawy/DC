//package com.example.myapplicationdc.Activity.Profile
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.myapplicationdc.Activity.NavigationButtons.MainActivity
//import com.example.myapplicationdc.R
//import com.example.myapplicationdc.databinding.ActivityChooseYourDirectionsBinding
//
//class ChooseYourDirectionsActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityChooseYourDirectionsBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        binding = ActivityChooseYourDirectionsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.btnDoctor.setOnClickListener {
//
////            val intent = Intent(this, DoctorInputActivity::class.java)
////            startActivity(intent)
//        }
//
//        binding.btnPatient.setOnClickListener {
//            val intent = Intent(this, PatientInputActivity::class.java)
//            startActivity(intent)
//        }
//
//        binding.btnGoToHomePage.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        binding.btnGoToProfilePage.setOnClickListener {
////            val intent = Intent(this, DoctorProfileActivity::class.java)
////            startActivity(intent)
//        }
//        }
//
//    }

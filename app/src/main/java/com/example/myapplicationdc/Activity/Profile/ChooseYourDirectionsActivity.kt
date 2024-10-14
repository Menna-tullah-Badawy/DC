package com.example.myapplicationdc.Activity.Profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplicationdc.Activity.NavigationButtons.MainActivity
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.AuthViewModel
import com.example.myapplicationdc.databinding.ActivityChooseYourDirectionsBinding

class ChooseYourDirectionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseYourDirectionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
         val authViewModel: AuthViewModel by viewModels()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityChooseYourDirectionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnDoctor.setOnClickListener {
            authViewModel.setUserLoggedIn()
            val intent = Intent(this, DoctorInputActivity::class.java)
            startActivity(intent)
        }

        binding.btnPatient.setOnClickListener {
            authViewModel.setUserLoggedIn()
            val intent = Intent(this, PatientInputActivity::class.java)
            startActivity(intent)
        }




    }
}

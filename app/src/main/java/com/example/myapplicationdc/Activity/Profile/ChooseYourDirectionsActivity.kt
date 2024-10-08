package com.example.myapplicationdc.Activity.Profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplicationdc.R
import com.example.myapplicationdc.databinding.ActivityChooseYourDirectionsBinding

class ChooseYourDirectionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseYourDirectionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Using ViewBinding
        binding = ActivityChooseYourDirectionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adjust for system bars (like the status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set click listeners for the Doctor and Patient buttons
        binding.btnDoctor.setOnClickListener {
            // Navigate to DoctorInputActivity
            val intent = Intent(this, DoctorInputActivity::class.java)
            startActivity(intent)
        }

        binding.btnPatient.setOnClickListener {
            // Navigate to PatientInputActivity
            val intent = Intent(this, PatientInputActivity::class.java)
            startActivity(intent)
        }
    }
}

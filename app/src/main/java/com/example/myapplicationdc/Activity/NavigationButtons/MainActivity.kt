package com.example.myapplicationdc.Activity.NavigationButtons

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationdc.Activity.Profile.DoctorProfileActivity
import com.example.myapplicationdc.Activity.Profile.PatientProfileActivity
import com.example.myapplicationdc.Adapter.CategoryAdapter
import com.example.myapplicationdc.Adapters.TopDoctorAdapter
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.MainViewModel
import com.example.myapplicationdc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize categories and top doctors
        initCategory()
        initTopDoctor()




        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already in MainActivity, no need to restart
                    true
                }
                R.id.navigation_fav_bold -> {
                    startActivity(Intent(this, FavouriteActivity::class.java))
                    true
                }
                R.id.navigation_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.navigation_account -> {

                    val doctorId = intent.getStringExtra("DOCTOR_ID")
                    if (doctorId != null) {
                        Log.d("Firebase", "DOCTOR_ID found: $doctorId")
                        val intent = Intent(this, DoctorProfileActivity::class.java).apply {
                            putExtra("DOCTOR_ID", doctorId)
                        }
                        startActivity(intent)
                    } else {
                        Log.d("Firebase", "DOCTOR_ID not found.")

                        // If no Doctor ID, check for Patient ID
                        val patientId = intent.getStringExtra("PATIENT_ID")
                        if (patientId != null) {
                            Log.d("Firebase", "PATIENT_ID found: $patientId")

                            val intent = Intent(this, PatientProfileActivity::class.java).apply {
                                putExtra("PATIENT_ID", patientId)
                            }
                            startActivity(intent)
                        } else {
                            Log.d("Firebase", "No DOCTOR ID or PATIENT ID found.")

                            Toast.makeText(this, "No DOCTOR ID or PATIENT ID found.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }
    private fun initTopDoctor() {
        binding.progressBarTopDoctors.visibility = View.VISIBLE

        viewModel.doctor.observe(this) { doctors ->
            // Set up RecyclerView for top doctors
            binding.recyclerViewTopDoctors.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.recyclerViewTopDoctors.adapter = TopDoctorAdapter(doctors)

            // Hide progress bar after data is loaded
            binding.progressBarTopDoctors.visibility = View.GONE
        }

        viewModel.loadDoctors()
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE

        viewModel.category.observe(this) { categories ->
            // Set up RecyclerView for categories
            binding.viewCategory.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.viewCategory.adapter = CategoryAdapter(categories)

            // Hide progress bar after data is loaded
            binding.progressBarCategory.visibility = View.GONE
        }

        viewModel.loadCategory()
    }
}

package com.example.myapplicationdc.Activity.NavigationButtons

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationdc.Activity.Authentication.SignInActivity
import com.example.myapplicationdc.Activity.Profile.ChooseYourDirectionsActivity
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

        // Initialize the RecyclerViews
        initCategory()
        initTopDoctor()
        binding.imageView5Main.setOnClickListener {

            val intent = Intent(this, ChooseYourDirectionsActivity::class.java)
            startActivity(intent)
            finish()
        }
        // Handle bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true // Stay on the current screen
                R.id.navigation_fav_bold -> {
                    startActivity(Intent(this, FavouriteActivity::class.java))
                    true
                }
                R.id.navigation_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.navigation_account -> {
                    startActivity(Intent(this, PatientProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun initTopDoctor() {
        // Show progress bar while loading data
        binding.progressBarTopDoctors.visibility = View.VISIBLE

        viewModel.doctor.observe(this) { doctors ->
            if (!doctors.isNullOrEmpty()) {
                binding.recyclerViewTopDoctors.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                binding.recyclerViewTopDoctors.adapter = TopDoctorAdapter(doctors)
            }
            // Hide progress bar after data is loaded
            binding.progressBarTopDoctors.visibility = View.GONE
        }

        viewModel.loadDoctors()
    }

    private fun initCategory() {
        // Show progress bar while loading data
        binding.progressBarCategory.visibility = View.VISIBLE

        viewModel.category.observe(this) { categories ->
            if (!categories.isNullOrEmpty()) {
                binding.viewCategory.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                binding.viewCategory.adapter = CategoryAdapter(categories)
            }
            // Hide progress bar after data is loaded
            binding.progressBarCategory.visibility = View.GONE
        }

        viewModel.loadCategory()
    }
}

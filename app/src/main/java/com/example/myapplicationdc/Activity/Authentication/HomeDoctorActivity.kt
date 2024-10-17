package com.example.myapplicationdc.Activity.Authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationdc.Activity.NavigationButtons.DashboardActivity
import com.example.myapplicationdc.Activity.NavigationButtons.FavouriteActivity
import com.example.myapplicationdc.Activity.Profile.AllProfilesActivity
import com.example.myapplicationdc.Activity.Profile.ChooseYourDirectionsActivity
import com.example.myapplicationdc.Adapter.CategoryAdapter
import com.example.myapplicationdc.Adapters.TopDoctorAdapter
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.MainViewModel
import com.example.myapplicationdc.databinding.ActivityHomeDoctorBinding
import com.google.firebase.auth.FirebaseAuth

class HomeDoctorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeDoctorBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the RecyclerViews
        initCategory()
        initTopDoctor()
        binding.imageView5DoctorMain.setOnClickListener {

            val intent = Intent(this, ChooseYourDirectionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Handle bottom navigation view
        binding.bottomNavigationViewDoctorMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dhome -> true
                R.id.navigation_dfav_bold -> {
                    startActivity(Intent(this, FavouriteActivity::class.java))
                    true
                }
                R.id.navigation_ddashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.navigation_daccount -> {
                    startActivity(Intent(this, AllProfilesActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun initTopDoctor() {
        binding.progressBarTopDoctorsDoctorMain.visibility = View.VISIBLE

        viewModel.doctor.observe(this) { doctors ->
            if (!doctors.isNullOrEmpty()) {
                binding.recyclerViewTopDoctorsDoctorMain.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                binding.recyclerViewTopDoctorsDoctorMain.adapter = TopDoctorAdapter(doctors)
            }
            // Hide progress bar after data is loaded
            binding.progressBarTopDoctorsDoctorMain.visibility = View.GONE
        }

        viewModel.loadDoctors()
    }

    private fun initCategory() {
        // Show progress bar while loading data
        binding.progressBarCategoryDoctorMain.visibility = View.VISIBLE

        viewModel.category.observe(this) { categories ->
            if (!categories.isNullOrEmpty()) {
                binding.viewCategoryDoctorMain.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                binding.viewCategoryDoctorMain.adapter = CategoryAdapter(categories)
            }
            // Hide progress bar after data is loaded
            binding.progressBarCategoryDoctorMain.visibility = View.GONE
        }

        viewModel.loadCategory()
    }


}

package com.example.myapplicationdc.Activity.Directions.PatientDirections

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationdc.Activity.Directions.DoctorDirections.TopDoctorActivity
import com.example.myapplicationdc.Activity.Directions.NavigationButtons.ChooseYourDirectionsActivity
import com.example.myapplicationdc.Activity.Directions.NavigationButtons.FavouriteActivity
import com.example.myapplicationdc.Activity.HistoryActivity
import com.example.myapplicationdc.Activity.Profile.PatientProfileActivity
import com.example.myapplicationdc.Adapter.CategoryAdapter
import com.example.myapplicationdc.Adapters.TopDoctorAdapter
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.MainViewModel
import com.example.myapplicationdc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var patientId: Int?=0
    private var patientName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        patientId = intent.getIntExtra("patientId",5)
        patientName = intent.getStringExtra("PATIENT_NAME")
        binding.textView2.text = "Hi $patientName"

        // Check if patientId is available
        patientId?.let {
            // Use patientId if needed (e.g., fetch patient-specific data)
            println("Logged in patient ID: $it")
        }
        // Initialize the RecyclerViews
        initCategory()
        initTopDoctor()

        binding.imageView5Main.setOnClickListener {

            val intent = Intent(this, ChooseYourDirectionsActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_fav_bold -> {
                    startActivity(Intent(this, FavouriteActivity::class.java))
                    true
                }
                R.id.navigation_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.navigation_account -> {
                    startActivity(Intent(this, PatientProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }


//    binding.doctorlistText.setOnClickListener {
//        val intent = Intent(this@MainActivity, TopDoctorActivity::class.java)
//        intent.putExtra("patientId", patientId)
//        startActivity(intent)
//    }
//
//    // Navigate to HistoryActivity
//    binding.history.setOnClickListener {
//        val intent = Intent(this@MainActivity, HistoryActivity::class.java)
//        intent.putExtra("patientId", patientId)
//        startActivity(intent)
//    }
//
//    // Navigate to Home (restarting MainActivity)
//    binding.home.setOnClickListener {
//        startActivity(Intent(this@MainActivity, MainActivity::class.java))
//    }
}

private fun initTopDoctor() {
    binding.progressBarTopDoctors.visibility = View.VISIBLE

    viewModel.doctor.observe(this@MainActivity, Observer { doctors ->
        // Set up RecyclerView layout and adapter
        binding.recyclerViewTopDoctors.layoutManager = LinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recyclerViewTopDoctors.adapter = TopDoctorAdapter(doctors,
            (patientId)
        )

        // Hide progress bar after data is loaded
        binding.progressBarTopDoctors.visibility = View.GONE
    })

    viewModel.loadDoctors()
}

private fun initCategory() {
    binding.progressBarCategory.visibility = View.VISIBLE

    viewModel.category.observe(this@MainActivity, Observer { categories ->
        // Set up RecyclerView layout and adapter
        binding.viewCategory.layoutManager = LinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.viewCategory.adapter = CategoryAdapter(categories)

        // Hide progress bar after data is loaded
        binding.progressBarCategory.visibility = View.GONE
    })

    viewModel.loadCategory()
}
}

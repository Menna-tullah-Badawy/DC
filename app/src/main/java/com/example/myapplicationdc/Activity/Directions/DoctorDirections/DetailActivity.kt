package com.example.myapplicationdc.Activity.Directions.DoctorDirections

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.myapplicationdc.Activity.BaseActivity
import com.example.myapplicationdc.Activity.Directions.PatientDirections.AppointmentActivity
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.MainViewModel
import com.example.myapplicationdc.databinding.ActivityDetailBinding

class DetailActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: DoctorModel
    var patientId:Int=0
    private val mainViewModel: MainViewModel by viewModels()

    // Track if the doctor is in favorites
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getBundle()
        patientId=intent.getIntExtra("patientId",6)

        // Initialize favorite button icon based on the favorite state
        updateFavoriteButtonIcon()

        // Favorite button click listener

    }

    private fun getBundle() {
        item = intent.getParcelableExtra<DoctorModel>("object")!!

        binding.apply {
            titleTxt.text = item.name
            specialTxt.text = item.special
            patiensTxt.text = item.patients
            bioTxt.text = item.biography
            addressTxt.text = item.address
            experienceTxt.text = "${item.experience} Years"
            ratingTxt.text = "${item.rating}"

            // Back button functionality
            backBtn.setOnClickListener {
                finish()
            }

            // Website button functionality
            websiteBtn.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(item.site)
                startActivity(i)
            }

            // Message button functionality
            messageBtn.setOnClickListener {
                val uri = Uri.parse("smsto:${item.mobile}")
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                intent.putExtra("sms_body", "the SMS text")
                startActivity(intent)
            }
            // Call button functionality
            callBtn.setOnClickListener {
                val uri = "tel:${item.mobile.trim()}"
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(uri))
                startActivity(intent)
            }

            // Direction button functionality
            directionBtn.setOnClickListener {
                val locationUrl = if (item.location.trim().isNotEmpty()) {
                    item.location.trim()
                } else {
                    "https://www.google.com/maps/search/?api=1&query=${item.address.trim()}"
                }
                Log.d("DetailActivity", "Location URL: $locationUrl")
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl))
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl)))
                }
            }

            // Share button functionality
            shareBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, item.name)
                intent.putExtra(Intent.EXTRA_TEXT, "${item.name} ${item.address} ${item.mobile}")
                startActivity(Intent.createChooser(intent, "Choose one"))
            }

            // Load the doctor's picture using Glide
            Glide.with(this@DetailActivity)
                .load(item.picture)
                .into(img)

            // Make Appointment button functionality
            makeBtn.setOnClickListener {
                val intent = Intent(this@DetailActivity, AppointmentActivity::class.java)
                intent.putExtra("doctor_id", item.id)
                intent.putExtra("doctor_name", item.name)
                intent.putExtra("doctor_image", item.picture)
                intent.putExtra("Location", item.location)
                intent.putExtra("fees", item.fees)
                intent.putExtra("patientId",patientId)
                startActivity(intent)
            }
        }
    }

    // Replace this method with your logic to retrieve the current patient's ID
    private fun getCurrentPatientId(): Int {
        // Logic to retrieve the current patient's ID
        return 0 // Example patient ID; change as needed
    }

    // Function to update the favorite button icon based on the favorite state
    private fun updateFavoriteButtonIcon() {
        binding.favBtn.setImageResource(if (isFavorite) {
            // Change to a filled heart icon or your chosen favorite icon
            R.drawable.ic_fav
        } else {
            // Change to an empty heart icon or your chosen not favorite icon
            R.drawable.ic_unfav
        })
    }
}

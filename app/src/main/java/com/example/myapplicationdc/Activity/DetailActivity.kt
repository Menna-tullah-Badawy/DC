package com.example.myapplicationdc.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.databinding.ActivityDetailBinding


class DetailActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: DoctorModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getBundle()

    }

    private fun getBundle()
    {
        item = intent.getParcelableExtra<DoctorModel>("object")!!

        binding.apply {
            titleTxt.text = item.Name
            specialTxt.text = item.Special
            patiensTxt.text = item.Patients
            bioTxt.text = item.Biography
            addressTxt.text = item.Address
            experienceTxt.text = item.Experience.toString() + " Years"
            ratingTxt.text = "${item.Rating}"
        }

       binding.backBtn.setOnClickListener {
            finish()
        }
        binding.websiteBtn.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(item.Site))
            startActivity(i)
        }
        binding.messageBtn.setOnClickListener {
            val uri = Uri.parse("smsto:${item.Mobile}")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra("sms_body", "the SMS text")
            startActivity(intent)
        }
        binding.callBtn.setOnClickListener {
            val uri = "tel:" + item.Mobile.trim()
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(uri))
            startActivity(intent)
        }
        binding.directionBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.Location))
            startActivity(intent)
        }
        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, item.Name)
            intent.putExtra(Intent.EXTRA_TEXT, "${item.Name} ${item.Address} ${item.Mobile}")
            startActivity(Intent.createChooser(intent, "Choose one"))
        }



        Glide.with(this)
            .load(item.Picture)
            .into(binding.img)

    }
}
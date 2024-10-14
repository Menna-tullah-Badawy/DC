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
            titleTxt.text = item.name
            specialTxt.text = item.special
            patiensTxt.text = item.patients
            bioTxt.text = item.biography
            addressTxt.text = item.address
            experienceTxt.text = item.experience.toString() + " Years"
            ratingTxt.text = "${item.rating}"
        }

       binding.backBtn.setOnClickListener {
            finish()
        }
        binding.websiteBtn.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(item.site))
            startActivity(i)
        }
        binding.messageBtn.setOnClickListener {
            val uri = Uri.parse("smsto:${item.mobile}")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra("sms_body", "the SMS text")
            startActivity(intent)
        }
        binding.callBtn.setOnClickListener {
            val uri = "tel:" + item.mobile?.trim()
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(uri))
            startActivity(intent)
        }
        binding.directionBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.location))
            startActivity(intent)
        }
        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, item.name)
            intent.putExtra(Intent.EXTRA_TEXT, "${item.name} ${item.address} ${item.mobile}")
            startActivity(Intent.createChooser(intent, "Choose one"))
        }



        Glide.with(this)
            .load(item.picture)
            .into(binding.img)

    }
}
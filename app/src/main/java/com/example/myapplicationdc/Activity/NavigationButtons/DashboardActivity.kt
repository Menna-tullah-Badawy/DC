package com.example.myapplicationdc.Activity.NavigationButtons

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import com.example.myapplicationdc.Activity.Authentication.SignInActivity
import com.example.myapplicationdc.Activity.BaseActivity
import com.example.myapplicationdc.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : BaseActivity() {
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
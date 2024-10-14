package com.example.myapplicationdc.Activity



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationdc.Activity.Authentication.SignInActivity
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.AuthViewModel
import com.example.myapplicationdc.Activity.Profile.ChooseYourDirectionsActivity
import com.example.myapplicationdc.Activity.NavigationButtons.MainActivity
class IntroActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // Check the user's login status
        authViewModel.checkUserLoginStatus()

        authViewModel.isUserLoggedIn.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Close IntroActivity
            } else {
                val getStartedButton = findViewById<Button>(R.id.getStartedButton)
                getStartedButton.setOnClickListener {
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}

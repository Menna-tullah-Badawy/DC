package com.example.myapplicationdc.Activity.Authentication

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.myapplicationdc.Activity.BaseActivity
import com.example.myapplicationdc.Activity.NavigationButtons.MainActivity
import com.example.myapplicationdc.Activity.Profile.ChooseYourDirectionsActivity
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.AuthViewModel
import com.example.myapplicationdc.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignInActivity : BaseActivity() {
    private var _binding: ActivitySignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var pb: Dialog
    private lateinit var database: DatabaseReference
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.btnSignIn.setOnClickListener {
            userLogin()
        }

        binding.btnSignInWithGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun userLogin() {
        val email = binding.etSinInEmail.text.toString()
        val password = binding.etSinInPassword.text.toString()

        if (validateForm(email, password)) {
            showProgressBar()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressBar()
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val userName = auth.currentUser?.displayName ?: "Unknown"
                        val userEmail = email

                        val user = User(id = userId, name = userName, email = userEmail)

                        // Save user data in Realtime Database
                        userId?.let {
                            database.child("users").child(it).setValue(user).addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    startActivity(Intent(this, ChooseYourDirectionsActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
    }


    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account = task.result
            account?.let {
                updateUI(it, it.email!!)
            }
        } else {
            Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount, email: String) {
        showProgressBar()

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            hideProgressBar()
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                val userName = account.displayName ?: "Unknown"
                val userEmail = email

                val user = User(id = userId, name = userName, email = userEmail)

                userId?.let {
                    database.child("users").child(it).setValue(user).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            observeLoginStatus()
                        } else {
                            Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeLoginStatus() {
        authViewModel.checkUserLoginStatus()
        authViewModel.isUserLoggedIn.observe(this) { isLoggedIn ->
            val intent = if (isLoggedIn) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, ChooseYourDirectionsActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = "Please enter a valid email"
                false
            }
            TextUtils.isEmpty(password) -> {
                binding.tilPassword.error = "Enter password"
                false
            }
            else -> {
                binding.tilEmail.error = null
                binding.tilPassword.error = null
                true
            }
        }
    }
}

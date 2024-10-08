package com.example.myapplicationdc.Activity.Authentication

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplicationdc.Activity.BaseActivity
import com.example.myapplicationdc.Activity.Profile.ChooseYourDirectionsActivity

import com.example.myapplicationdc.R
import com.example.myapplicationdc.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : BaseActivity() {
    private var _binding: ActivitySignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var pb: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

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
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener {
            userLogin()
        }

        binding.btnSignInWithGoogle.setOnClickListener {
            Log.d("SignInActivity", "Google Sign-In button clicked")
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
                        Log.d("SignInActivity", "Email Sign-In successful")
                        val intent = Intent(this, ChooseYourDirectionsActivity::class.java)
                        startActivity(intent)
                        finish() // Close SignInActivity
                    } else {
                        Log.e("SignInActivity", "Email Sign-In failed: ${task.exception}")
                        Toast.makeText(this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun signInWithGoogle() {
        Log.d("SignInActivity", "Starting Google Sign-In")
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            Log.d("SignInActivity", "Google Sign-In task successful")
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                Log.d("SignInActivity", "Google account retrieved: ${account.email}")

                // Check if email is available, else prompt user to select
                if (account.email.isNullOrEmpty()) {
                    promptForEmail(account)
                } else {
                    askToUseEmail(account.email!!, account)
                }
            }
        } else {
            Log.e("SignInActivity", "Google Sign-In failed: ${task.exception}")
            Toast.makeText(this, "Sign In Failed, try again.", Toast.LENGTH_SHORT).show()
        }
    }

    // Prompt user to select email from available accounts
    private fun promptForEmail(account: GoogleSignInAccount) {
        val accounts = getEmailAccounts()
        if (accounts.isNotEmpty()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Email")

            // Create an ArrayAdapter for the email accounts
            builder.setItems(accounts.toTypedArray()) { dialog, which ->
                val selectedEmail = accounts[which]
                askToUseEmail(selectedEmail, account)
            }

            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            builder.show()
        } else {
            // If no accounts found, fallback to manual input
            manualEmailInput(account)
        }
    }

    // Ask the user if they want to use the selected email
    private fun askToUseEmail(selectedEmail: String, account: GoogleSignInAccount) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Use Selected Email?")
        builder.setMessage("Do you want to use this email: $selectedEmail?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            updateUI(account, selectedEmail)
        }

        builder.setNegativeButton("No") { dialog, _ ->
            manualEmailInput(account) // Fall back to manual input if user chooses "No"
        }

        builder.show()
    }

    // Method to get email accounts from the device
    private fun getEmailAccounts(): List<String> {
        val accountManager = AccountManager.get(this)
        val accounts = accountManager.getAccountsByType("com.google")
        return accounts.map { it.name } // Return a list of email addresses
    }

    // Fallback method to manually input email
    private fun manualEmailInput(account: GoogleSignInAccount) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Email")

        // Set up the input
        val input = EditText(this)
        input.hint = "Enter your email"
        input.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK") { dialog, _ ->
            val email = input.text.toString()
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Update the UI with the entered email
                updateUI(account, email)
            } else {
                Toast.makeText(this, "Please enter a valid email address (e.g. example@gmail.com)", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    // Update UI after Google Sign-In
    private fun updateUI(account: GoogleSignInAccount, email: String) {
        Log.d("SignInActivity", "Updating UI with Google account: ${account.displayName}, Email: $email")
        showProgressBar()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            hideProgressBar()
            if (it.isSuccessful) {
                Log.d("SignInActivity", "Firebase Auth with Google successful")
                val id = FireStoreClass().getCurrentUserId()
                val name = account.displayName.toString()
                val userInfo = User(id, name, email)
                FireStoreClass().registerUser(userInfo)
                val intent = Intent(this, ChooseYourDirectionsActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.e("SignInActivity", "Firebase Auth with Google failed: ${it.exception}")
                Toast.makeText(this, "Sign In Failed, try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = "Please enter a valid email address (e.g. example@gmail.com)"
                false
            }
            TextUtils.isEmpty(password) -> {
                binding.tilPassword.error = "Enter password"
                binding.tilEmail.error = null
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

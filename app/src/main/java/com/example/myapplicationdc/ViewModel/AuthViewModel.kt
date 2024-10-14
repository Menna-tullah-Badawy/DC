package com.example.myapplicationdc.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean> get() = _isUserLoggedIn



    fun checkUserLoginStatus() {
        val userId = auth.currentUser?.uid
        _isUserLoggedIn.value = userId != null
    }


    fun setUserLoggedIn() {
        _isUserLoggedIn.value = true
    }


}
//
//package com.example.myapplicationdc.ViewModel
//
//import android.app.Application
//import android.content.Context
//import android.util.Log
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//class AuthViewModel(application: Application) : AndroidViewModel(application) {
//    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
//    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//
//    // SharedPreferences for storing direction choice
//    private val sharedPreferences =
//        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
//
//    // LiveData for login status and direction choice
//    private val _isUserLoggedIn = MutableLiveData<Boolean>()
//    val isUserLoggedIn: LiveData<Boolean> get() = _isUserLoggedIn
//
//    private val _hasChosenDirection = MutableLiveData<Boolean>()
//    val hasChosenDirection: LiveData<Boolean> get() = _hasChosenDirection
//
//    private val _isUserNew = MutableLiveData<Boolean>()
//    val isUserNew: LiveData<Boolean> get() = _isUserNew
//
//    fun getCurrentUserId(): String? {
//        return auth.currentUser?.uid
//    }
//
//    fun setDirectionChosen() {
//        _hasChosenDirection.value = true
//        sharedPreferences.edit().putBoolean("hasChosenDirection", true).apply()
//        Log.d("AuthViewModel", "Direction chosen: ${_hasChosenDirection.value}")
//    }
//
//    fun checkIfUserIsNew(userId: String, database: DatabaseReference) {
//        database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
//            _isUserNew.value = !snapshot.exists()
//        }
//    }
//
//    fun getCurrentUserDirection(onResult: (String) -> Unit) {
//        val userId = auth.currentUser?.uid
//        if (userId != null) {
//            database.child(userId).child("direction").get().addOnSuccessListener {
//                val direction = it.getValue(String::class.java) ?: ""
//                onResult(direction)
//            }.addOnFailureListener {
//                onResult("")
//            }
//        }
//    }
//
//    // Fix: Adding the saveDirectionInFirebase function
//    fun saveDirectionInFirebase(userId: String, direction: String) {
//        val userMap = mapOf(
//            "direction" to direction
//        )
//        database.child(userId).updateChildren(userMap)
//            .addOnSuccessListener {
//                Log.d("AuthViewModel", "Direction saved successfully in Firebase.")
//            }
//            .addOnFailureListener { e ->
//                Log.e("AuthViewModel", "Failed to save direction in Firebase", e)
//            }
//    }
//}

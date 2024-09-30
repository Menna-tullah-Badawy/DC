package com.example.myapplicationdc.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplicationdc.Domain.CategoryModel
import com.example.myapplicationdc.Domain.DoctorModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel(): ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _category = MutableLiveData<MutableList<CategoryModel>>()
    private val _doctor = MutableLiveData<MutableList<DoctorModel>>()

    val category: LiveData<MutableList<CategoryModel>> = _category
    val doctor: LiveData<MutableList<DoctorModel>> = _doctor

    fun loadCategory() {
        val Ref = firebaseDatabase.getReference("Category")
        Ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(CategoryModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _category.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    fun loadDoctors(){
        val Ref=firebaseDatabase.getReference("Doctors")
        Ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                   val lists= mutableListOf<DoctorModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(DoctorModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _doctor.value = lists

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


        }



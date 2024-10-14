package com.example.myapplicationdc.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplicationdc.Domain.PatientModel

class PatientViewModel : ViewModel() {
    private val _patientData = MutableLiveData<PatientModel>()
    val patientData: LiveData<PatientModel> get() = _patientData

    fun setPatientData(patient: PatientModel) {
        _patientData.value = patient
    }
}

package com.example.myapplicationdc.Activity.ui.notifications

import android.os.Bundle
import android.util.Log
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplicationdc.Domain.Appointment
import com.example.myapplicationdc.ViewModel.DoctorViewModel
import com.example.myapplicationdc.databinding.ActivityNotificationsBinding
import com.google.firebase.database.*

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout for this activity
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the DoctorViewModel
        val doctorViewModel = ViewModelProvider(this).get(DoctorViewModel::class.java)

        // Observe the doctorId LiveData
        doctorViewModel.doctorId.observe(this) { doctorId ->
            if (doctorId != null) {
                // Initialize Firebase Database reference
                databaseReference = FirebaseDatabase.getInstance().getReference("appointment")
                fetchAppointments(doctorId) // Fetch appointments for the retrieved doctorId
            }
        }
    }

    // Function to fetch appointments where doctorId matches
    private fun fetchAppointments(doctorId: String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d("NotificationsActivity", "No appointments found for doctorId: $doctorId")
                    return
                }

                binding.tableLayout.removeAllViews() // Clear previous data

                for (appointmentSnapshot in dataSnapshot.children) {
                    val appointmentData = appointmentSnapshot.value as? Map<*, *>
                    val appointment = appointmentData?.let {
                        Appointment(
                            appointmentId = it["appointmentId"] as? Int ?: 0,
                            appointmentDate = it["appointmentDate"] as? String ?: "Unknown",
                            doctorId = it["doctorId"] as? Int ?: 0,
                            patientId = it["patientId"] as? Int ?: 0,
                            doctorName = it["doctorName"] as? String ?: "Unknown",
                            doctorImage = it["doctorImage"] as? String ?: "Unknown",
                            location = it["location"] as? String ?: "Unknown"
                        )
                    } ?: continue

                    Log.d("NotificationsActivity", "Fetched appointment: ${appointment.appointmentDate}, ID: ${appointment.appointmentId}")

                    // Check if the doctorId matches
                    if (appointment.doctorId.toString() == doctorId) {
                        fetchPatientDetails(appointment.patientId!!) { patientName, medicalHistory ->
                            addRowToTable(patientName, appointment.appointmentDate!!, medicalHistory)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("NotificationsActivity", "Error fetching appointments: ${databaseError.message}")
            }
        })
    }

    private fun fetchPatientDetails(patientId: Int, callback: (String, String) -> Unit) {
        val patientReference = FirebaseDatabase.getInstance().getReference("Patients")

        patientReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var found = false
                for (patientSnapshot in dataSnapshot.children) {
                    val id = patientSnapshot.child("id").getValue(Int::class.java) ?: 0

                    if (id == patientId) {
                        found = true
                        val patientName = patientSnapshot.child("pname").getValue(String::class.java) ?: "Unknown"
                        val medicalHistory = patientSnapshot.child("medicalHistory").getValue(String::class.java) ?: "No history"

                        Log.d("NotificationsActivity", "Fetched patientName: $patientName, medicalHistory: $medicalHistory")

                        callback(patientName, medicalHistory)
                        return
                    }
                }

                if (!found) {
                    Log.d("NotificationsActivity", "Patient ID $patientId not found")
                    callback("Unknown", "No history")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("NotificationsActivity", "Error fetching patient details: ${databaseError.message}")
            }
        })
    }

    private fun addRowToTable(patientName: String, appointmentDay: String, medicalHistory: String) {
        val tableLayout: TableLayout = binding.tableLayout
        val row = TableRow(this)

        val nameTextView = TextView(this).apply {
            text = patientName
            setPadding(8, 8, 8, 8)
        }
        row.addView(nameTextView)

        val dayTextView = TextView(this).apply {
            text = appointmentDay
            setPadding(8, 8, 8, 8)
        }
        row.addView(dayTextView)

        val historyTextView = TextView(this).apply {
            text = medicalHistory
            setPadding(8, 8, 8, 8)
        }
        row.addView(historyTextView)

        tableLayout.addView(row)
    }
}

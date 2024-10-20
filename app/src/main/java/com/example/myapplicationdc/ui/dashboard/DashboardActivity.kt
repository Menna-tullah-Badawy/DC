package com.example.myapplicationdc.ui.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationdc.Adapter.DoctorAppointmentAdapter
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.DoctorViewModel
import com.example.myapplicationdc.databinding.ActivityDashboardBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var adapter: DoctorAppointmentAdapter
    private val availableDays: MutableList<String> = mutableListOf() // List to hold available days
    private lateinit var doctorViewModel: DoctorViewModel // ViewModel instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up ViewModel
        doctorViewModel = ViewModelProvider(this).get(DoctorViewModel::class.java)

        // Set up RecyclerView
        val recyclerView: RecyclerView = binding.availableDaysrecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DoctorAppointmentAdapter(availableDays) { dayName ->
            showDeleteConfirmationDialog(dayName)
        }
        recyclerView.adapter = adapter

        // Observe doctorId from the ViewModel and fetch available days
        doctorViewModel.doctorId.observe(this) { doctorId ->
            if (!doctorId.isNullOrEmpty()) {
                // Fetch available days from Firebase
                fetchAvailableDays(doctorId)
            } else {
                Log.e("DashboardActivity", "Doctor ID is null or empty")
            }
        }

        // Set up FAB click listener
        val fab: FloatingActionButton = binding.fabAdd
        fab.setOnClickListener {
            showAddDayDialog()
        }
    }

    private fun fetchAvailableDays(doctorId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Doctors").child(doctorId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                availableDays.clear() // Clear the list before adding new data

                // Directly access the availableDays for the specific doctor
                val availableDaysList = dataSnapshot.child("availableDays")
                for (daySnapshot in availableDaysList.children) {
                    val day = daySnapshot.child("day").getValue(String::class.java)?.replace("\"", "") // Remove quotes
                    val status = daySnapshot.child("status").getValue(String::class.java)

                    if (day != null && status != "reserved") {
                        availableDays.add(day) // Add available day to the list
                    }
                }
                adapter.notifyDataSetChanged() // Notify adapter of data change
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DashboardActivity", "Error fetching available days: ${databaseError.message}")
            }
        })
    }

    private fun showAddDayDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_day, null)
        val editTextDay = dialogView.findViewById<EditText>(R.id.editTextDay)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Available Day")
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.buttonAdd).setOnClickListener {
            val dayName = editTextDay.text.toString().trim()
            if (dayName.isNotEmpty()) {
                if (!availableDays.contains(dayName)) {
                    availableDays.add(dayName)
                    adapter.notifyDataSetChanged() // Notify adapter of data change
                    addDayToFirebase(dayName) // Add to Firebase
                    Toast.makeText(this, "$dayName added successfully!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss() // Dismiss dialog after adding
                } else {
                    Toast.makeText(this, "$dayName already exists!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss() // Dismiss dialog if already exists
                }
            } else {
                Toast.makeText(this, "Please enter a day name", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addDayToFirebase(dayName: String) {
        doctorViewModel.doctorId.value?.let { doctorId ->
            val databaseReference = FirebaseDatabase.getInstance().getReference("Doctors").child(doctorId).child("availableDays")

            // Create a new unique key for the day
            val key = databaseReference.push().key ?: return

            // Add the day to the Firebase database
            val dayData = mapOf("day" to dayName, "status" to "available")
            databaseReference.child(key).setValue(dayData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("DashboardActivity", "Day added successfully: $dayName")
                } else {
                    Log.e("DashboardActivity", "Error adding day: ${task.exception?.message}")
                    Toast.makeText(this, "Failed to add day: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Function to show the delete confirmation dialog
    private fun showDeleteConfirmationDialog(dayName: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Day")
            .setMessage("Are you sure you want to delete $dayName?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteDay(dayName)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    // Function to delete the selected day
    private fun deleteDay(dayName: String) {
        doctorViewModel.doctorId.value?.let { doctorId ->
            // Remove from adapter
            availableDays.remove(dayName)
            adapter.notifyDataSetChanged() // Notify adapter of data change

            // Remove from Firebase
            val databaseReference = FirebaseDatabase.getInstance().getReference("Doctors").child(doctorId).child("availableDays")

            // Find the key for the day to delete
            databaseReference.orderByChild("day").equalTo(dayName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (daySnapshot in dataSnapshot.children) {
                        daySnapshot.ref.removeValue() // Remove the day from Firebase
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("DashboardActivity", "Error deleting day: ${databaseError.message}")
                }
            })
        }
    }
}

package com.example.myapplicationdc.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplicationdc.Activity.Directions.DoctorDirections.DetailActivity
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.databinding.ViewholderTopDoctor2Binding

class TopDoctorAdapter2(
    private val items: MutableList<DoctorModel>,
    private val patientId: Int?
) : RecyclerView.Adapter<TopDoctorAdapter2.Viewholder>() {

    private var context: Context? = null

    class Viewholder(val binding: ViewholderTopDoctor2Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        val binding = ViewholderTopDoctor2Binding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val doctor = items[position]
        holder.binding.nameTxt.text = doctor.name
        holder.binding.special.text = doctor.special
        holder.binding.ScoreTxt.text = doctor.rating.toString()
        holder.binding.ratingBar.rating = doctor.rating.toFloat()
        holder.binding.degreeTxt.text = "Professional Doctor"

        // Load doctor image using Glide
        Glide.with(holder.itemView.context)
            .load(doctor.picture)
            .apply(RequestOptions().transform(CenterCrop()))
            .into(holder.binding.img)

        // Navigate to DetailActivity with selected doctor and patientId
        holder.binding.makeBtn.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("object", doctor)
                putExtra("patientId", patientId)
            }
            context?.startActivity(intent)
        }
    }
}

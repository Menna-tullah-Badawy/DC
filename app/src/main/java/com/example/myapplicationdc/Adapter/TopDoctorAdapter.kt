package com.example.myapplicationdc.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplicationdc.Activity.DetailActivity
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.databinding.ViewholderTopDoctorBinding

class TopDoctorAdapter(val items: MutableList<DoctorModel>):RecyclerView.Adapter<TopDoctorAdapter.Viewholder>() {
    private var context: Context?=null

    class Viewholder(val binding: ViewholderTopDoctorBinding ):RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
    context=parent.context
        val binding =
            ViewholderTopDoctorBinding.inflate(LayoutInflater.from(context), parent , false)
        return Viewholder(binding)
    }


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.binding.nameTxt.text = items[position].name
        holder.binding.specialTxt.text = items[position].special
        holder.binding.scoreTxt.text = items[position].rating.toString()
        holder.binding.yearTxt.text = items[position].experience.toString() + " Years"

        Glide.with(holder.itemView.context)
            .load(items[position].picture)
            .apply{ RequestOptions().transform(CenterCrop())}
            .into(holder.binding.img)

        holder.itemView.setOnClickListener{
            val intent =Intent(context,DetailActivity::class.java)
            intent.putExtra("object",items[position])
            context?.startActivity(intent)
        }
    }

}
package com.fogued.herbariumapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(
    private val items: List<ImageItem>,
    private var isGridView: Boolean
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val locationTextView: TextView = view.findViewById(R.id.locationTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(if (isGridView) R.layout.item_grid else R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.imageView.context).load(item.imageUrl).into(holder.imageView)
        holder.nameTextView.text = item.name
        holder.locationTextView.text = item.location
        holder.descriptionTextView.text = item.description
    }

    override fun getItemCount(): Int = items.size

    fun setViewType(isGridView: Boolean) {
        this.isGridView = isGridView
        notifyDataSetChanged()
    }
}
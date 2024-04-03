package com.softyorch.firestorage.ui.xml.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softyorch.firestorage.databinding.ItemGalleryBinding

class GalleryAdapter(
    private val images: MutableList<String> = mutableListOf()
): RecyclerView.Adapter<GalleryViewHolder>() {

    fun updateList(list: List<String>) {
        images.clear()
        images.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding: ItemGalleryBinding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.render(image = images[position])
    }
}

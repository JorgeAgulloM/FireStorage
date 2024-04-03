package com.softyorch.firestorage.ui.xml.list

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softyorch.firestorage.databinding.ItemGalleryBinding

class GalleryViewHolder(private val binding: ItemGalleryBinding): RecyclerView.ViewHolder(binding.root) {
    fun render(image: String) {
        Glide.with(binding.ivGalleryItem.context).load(image).into(binding.ivGalleryItem)
    }
}

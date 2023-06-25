package com.example.namo.ui.bottom.diary.groupDiary.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.namo.databinding.ItemGalleryListBinding

class GroupPlaceGalleryAdapter(  // 그룹 다이어리 장소별 이미지
    private val context: Context,
    initialItems: List<String> = emptyList()

):
    RecyclerView.Adapter<GroupPlaceGalleryAdapter.ViewHolder>(){


    private val items = ArrayList<String>(initialItems)

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(image: List<String>) {
        this.items.clear()
        this.items.addAll(image)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemGalleryListBinding = ItemGalleryListBinding.inflate(
            LayoutInflater.from(viewGroup.context),viewGroup,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val uri= items[position]

        Glide.with(context)
                .load(uri.toUri())
                .into(holder.imageUrl)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemGalleryListBinding): RecyclerView.ViewHolder(binding.root){
        val imageUrl=binding.galleryImgIv

    }
}
package com.mongmong.namo.presentation.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.mongmong.namo.presentation.config.CategoryColor

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("android:imageSource")
    fun setImageSource(imageView: ImageView, resource: LiveData<Int?>?) {
        if (resource != null && resource.value != null) {
            imageView.setImageResource(resource.value!!)
        }
    }

    @JvmStatic
    @BindingAdapter("app:tintColor")
    fun setTintColor(view: View, color: Int) {
        val hexColor = when (color) {
            in 1..CategoryColor.getAllColors().size -> CategoryColor.getAllColors()[color - 1]
            else -> CategoryColor.DEFAULT_PALETTE_COLOR1.hexColor
        }
        view.backgroundTintList = ColorStateList.valueOf(Color.parseColor(hexColor))
    }

}

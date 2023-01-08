package com.example.myapplication.utils

import android.content.Context
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.net.Uri
import android.widget.ImageView
import com.example.myapplication.R
import java.io.IOException
import com.bumptech.glide.Glide


class GlideLoader (val context: Context) {
    fun loadUserPicture (imageURI: Uri, imageView: ImageView) {
        try {
            Glide
                .with(context)
                .load(imageURI)
                .centerCrop()
                .placeholder(R.drawable.face)
                .into(imageView)
        } catch (e:IOException){
            e.printStackTrace()
        }
    }
}
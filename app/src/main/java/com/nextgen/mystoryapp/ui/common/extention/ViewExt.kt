package com.nextgen.mystoryapp.ui.common.extention

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun ImageView.loadImage(url: String?) {
    Glide.with(this.context).load(url).centerCrop().into(this)
}

fun ImageView.loadOriImage(url: String?) {
    Glide.with(this.context).load(url).fitCenter().into(this)
}

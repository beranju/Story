package com.nextgen.mystoryapp.ui.common.extention

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showAlertDialog(message: String) {
    AlertDialog.Builder(this).apply {
        setMessage(message)
        setPositiveButton("OK") { d, _ ->
            d.cancel()
        }
    }.show()
}
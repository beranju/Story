package com.nextgen.mystoryapp.ui.common.extention

import android.util.Patterns

fun String.isEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
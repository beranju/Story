package com.nextgen.mystoryapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.nextgen.mystoryapp.R

class CustomEditText : AppCompatEditText {
    private lateinit var errorIcon: Drawable
    private lateinit var passIcon: Drawable
    private lateinit var emailIcon: Drawable
    private val minLongPass: Int = 6

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        when (inputType) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                hint = context.getString(R.string.hint_password)
                setPassIcon()
            }
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                hint = context.getString(R.string.hint_email)
                setEmailIcon()
            }
        }
    }

    private fun init() {
        errorIcon =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_warning_red) as Drawable
        passIcon = ContextCompat.getDrawable(context, R.drawable.ic_password_custom) as Drawable
        emailIcon = ContextCompat.getDrawable(context, R.drawable.ic_email_custom) as Drawable

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when (inputType) {
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                        if (s?.isNotEmpty() == true) {
                            if (s.length < minLongPass) {
                                hideErrorIcon()
                                error = "Password min 6 karakter"
                            } else {
                                hideErrorIcon()
                                setPassIcon()
                            }
                        }
                    }
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                            hideErrorIcon()
                            setEmailIcon()
                        } else {
                            showErrorIcon()
                            setEmailIcon()
                            error = context.getString(R.string.email_not_valid)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun setPassIcon() {
        setIconDrawables(startOfTheText = passIcon)
    }

    private fun setEmailIcon() {
        setIconDrawables(startOfTheText = emailIcon)
    }

    private fun showErrorIcon() {
        setIconDrawables(endOfTheText = errorIcon)
    }

    private fun hideErrorIcon() {
        setIconDrawables(null, null, null, null)
    }

    private fun setIconDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null,
    ) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            startOfTheText, topOfTheText, endOfTheText, bottomOfTheText
        )
    }
}
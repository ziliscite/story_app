package com.submission.storyapp.presentation.shared

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class PasswordEditText : TextInputEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (s.toString().length < 8) {
                    setError("Invalid password format", null) // disable the "eye" logo
                } else {
                    error = null
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}

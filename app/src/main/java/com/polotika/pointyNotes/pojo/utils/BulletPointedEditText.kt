package com.polotika.pointyNotes.pojo.utils

import android.R
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class BulletPointedEditText(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    TextInputEditText(context, attrs, defStyleAttr) {
    var mContext: Context? = context
    var mTypeface: Typeface? = null

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.editTextStyle
    ) {
    }

    constructor(context: Context) : this(context, null, R.attr.editTextStyle) {}

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        var text = text
        if (lengthAfter > lengthBefore) {
            if (text.toString().length == 1) {
                text = "• $text"
                setText(text)
                setSelection(getText()?.length?:0)
            }
            if (text.toString().endsWith("\n")) {
                text = text.toString().replace("\n", "\n• ")
                text = text.toString().replace("• •", "•")
                setText(text)
                setSelection(getText()?.length?:0)
            }
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }





}
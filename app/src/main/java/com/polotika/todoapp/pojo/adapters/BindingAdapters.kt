package com.polotika.todoapp.pojo.adapters

import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.polotika.todoapp.R
import com.polotika.todoapp.pojo.data.models.PriorityModel

val prioritiesList = listOf("Low Priority", "Medium Priority", "High Priority")

@BindingAdapter("setAppropriateText")
fun setAppropriateText(textView: AutoCompleteTextView,priorityModel: PriorityModel){
    when (priorityModel)
    {
        PriorityModel.High -> {
            textView.setTextColor(
                ContextCompat.getColor(
                    textView.context,
                    R.color.red
                )
            )
            textView.setText( prioritiesList[2],false)
        }

        PriorityModel.Medium -> {
            textView.setTextColor(
                ContextCompat.getColor(
                    textView.context,
                    R.color.green
                )
            )

            textView.setText( prioritiesList[1],false)
        }
        PriorityModel.Low -> {
            textView.setTextColor(
                ContextCompat.getColor(
                    textView.context,
                    R.color.yellow
                )
            )

            textView.setText( prioritiesList[0],false)
        }

    }

}

@BindingAdapter("onItemClick")
fun onItemClick(textView: AutoCompleteTextView,value:Boolean){
    textView.onItemClickListener =
        AdapterView.OnItemClickListener { parent, view, position, id ->

            when (position) {
                0 -> {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            textView.context,
                            R.color.yellow
                        )
                    )
                }
                1 -> {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            textView.context,
                            R.color.green
                        )
                    )
                }
                2 -> {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            textView.context,
                            R.color.red
                        )
                    )
                }
            }
        }
}
package com.polotika.todoapp.ui

import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.polotika.todoapp.R
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.models.PriorityModel
import com.polotika.todoapp.ui.home.HomeFragmentDirections

class BindingAdapters {

    companion object{
        @BindingAdapter("android:navigateToAddFragment")
        @JvmStatic
        fun navigateToAddFragment(view: FloatingActionButton, value: Boolean) {
            when (value) {
                true -> {
                    view.setOnClickListener {
                        view.findNavController().navigate(R.id.action_homeFragment_to_addFragment)
                    }
                }

            }


        }

        @BindingAdapter("android:parseCardColor")
        @JvmStatic
        fun parseCardColor(cardView: CardView,priorityModel: PriorityModel){
            when(priorityModel){
                PriorityModel.Low -> cardView.setCardBackgroundColor(cardView.context.getColor(R.color.yellow))
                PriorityModel.Medium -> cardView.setCardBackgroundColor(cardView.context.getColor(R.color.green))
                PriorityModel.High -> cardView.setCardBackgroundColor(cardView.context.getColor(R.color.red))
            }
        }

        @BindingAdapter("android:sendDataToUpdateFragment")
        @JvmStatic
        fun sendDataToUpdateFragment(layout:ConstraintLayout,noteModel: NoteModel){
            layout.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToUpdateFragment(noteModel)
                layout.findNavController().navigate(action)
            }

        }
    }

}
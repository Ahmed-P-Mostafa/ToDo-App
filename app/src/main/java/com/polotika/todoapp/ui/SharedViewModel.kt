package com.polotika.todoapp.ui

import android.app.Application
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.AndroidViewModel
import com.polotika.todoapp.R
import com.polotika.todoapp.data.models.PriorityModel

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "SharedViewModel"


    fun getPriority(priority: String): PriorityModel {
        return when (priority) {
            "High Priority" -> PriorityModel.High
            "Medium Priority" -> PriorityModel.Medium
            "Low Priority" -> PriorityModel.Low

            else -> PriorityModel.Low
        }
    }


    fun validateUserData(title: String, desc: String): Boolean {

        return !(TextUtils.isEmpty(title) || TextUtils.isEmpty(desc))
    }
}
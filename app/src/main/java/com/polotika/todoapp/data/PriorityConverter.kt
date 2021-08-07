package com.polotika.todoapp.data

import androidx.room.TypeConverter
import com.polotika.todoapp.data.models.PriorityModel

class PriorityConverter {

    @TypeConverter
    fun fromPriorityToString(priorityModel: PriorityModel):String{
        return priorityModel.name
    }

    @TypeConverter
    fun fromStringToPriority(priorityModel: String): PriorityModel {
        return PriorityModel.valueOf(priorityModel)
    }
}
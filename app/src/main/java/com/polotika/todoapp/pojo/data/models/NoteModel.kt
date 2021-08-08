package com.polotika.todoapp.pojo.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "notes_table")
@Parcelize
data class NoteModel(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var title: String,
    var priority: PriorityModel,
    var description: String
) : Parcelable
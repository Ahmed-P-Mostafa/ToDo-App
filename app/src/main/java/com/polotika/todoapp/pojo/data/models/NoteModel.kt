package com.polotika.todoapp.pojo.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "notes_table")
@Parcelize
data class NoteModel(
    @PrimaryKey(autoGenerate = true) var id: Int?=null,
    var title: String?=null,
    var priority: PriorityModel?=null,
    var description: String?=null
) : Parcelable
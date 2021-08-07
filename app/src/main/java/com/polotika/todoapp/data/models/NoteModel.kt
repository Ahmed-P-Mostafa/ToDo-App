package com.polotika.todoapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class NoteModel(@PrimaryKey(autoGenerate = true) var id:Int?=0, var title:String, var priority: PriorityModel, var description:String)
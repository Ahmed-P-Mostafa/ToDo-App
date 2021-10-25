package com.polotika.todoapp.pojo.utils

import androidx.recyclerview.widget.DiffUtil
import com.polotika.todoapp.pojo.data.models.NoteModel

class NotesListDiffUtil(private val oldList:List<NoteModel>, private val newList:List<NoteModel>) : DiffUtil.Callback() {
    override fun getOldListSize()= oldList.size

    override fun getNewListSize()= newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
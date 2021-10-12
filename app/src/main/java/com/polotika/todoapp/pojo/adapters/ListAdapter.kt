package com.polotika.todoapp.pojo.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.polotika.todoapp.R
import com.polotika.todoapp.databinding.NoteItemBinding
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.utils.NotesListDiffUtil

class ListAdapter(var list: List<NoteModel>? = emptyList()) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private val TAG = "ListAdapter"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent = parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = list!!.get(position)
        holder.bind(note = note)
    }

    override fun getItemCount() = list?.size ?: 0

    class ViewHolder(val item: NoteItemBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(note: NoteModel) {
            item.note = note
            item.invalidateAll()
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.note_item, parent, false
                ))
            }
        }


    }

    fun changeData(list: List<NoteModel>) {
        Log.d(TAG, "changeData: $list")
        Log.d(TAG, "changeData: ${list.size}")
        val diffUtil = NotesListDiffUtil(this.list!!, list)
        val results = DiffUtil.calculateDiff(diffUtil)
        this.list = list
        results.dispatchUpdatesTo(this)
    }


}
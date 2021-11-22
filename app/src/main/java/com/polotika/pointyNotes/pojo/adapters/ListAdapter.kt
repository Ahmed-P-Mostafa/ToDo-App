package com.polotika.pointyNotes.pojo.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.polotika.pointyNotes.R
import com.polotika.pointyNotes.databinding.NoteItemBinding
import com.polotika.pointyNotes.pojo.data.models.NoteModel
import com.polotika.pointyNotes.pojo.utils.NotesListDiffUtil

class ListAdapter(var list: MutableList<NoteModel>? = mutableListOf()) :
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

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.note_item, parent, false
                    )
                )
            }
        }


    }

    fun changeData(list: MutableList<NoteModel>) {
        Log.d(TAG, "changeData: ${list.toString()}")
        val diffUtil = NotesListDiffUtil(this.list!!, list)
        val results = DiffUtil.calculateDiff(diffUtil)
        this.list = list
        results.dispatchUpdatesTo(this)
    }

    fun addNewNoteToAdapter(newNote :NoteModel,index:Int){
        list?.add(index,newNote)
        notifyDataSetChanged()
    }

    fun removeNoteFromAdapter(note:NoteModel){
        list?.remove(note)
        notifyDataSetChanged()
    }
}
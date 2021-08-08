package com.polotika.todoapp.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.polotika.todoapp.R
import com.polotika.todoapp.data.models.NoteModel
import com.polotika.todoapp.data.models.PriorityModel
import com.polotika.todoapp.databinding.NoteItemBinding
import com.polotika.todoapp.ui.HomeFragment
import com.polotika.todoapp.ui.HomeFragmentDirections

class ListAdapter(private var list: List<NoteModel>? = emptyList()) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<NoteItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.note_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = list!!.get(position)
        holder.bind(note = note)
        holder.item.priorityIndicator.setCardBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                getPriorityColor(note.priority)
            )
        )
        val action = HomeFragmentDirections.actionHomeFragmentToUpdateFragment(note = note)
        holder.itemView.setOnClickListener { holder.itemView.findNavController().navigate(action) }


    }

    override fun getItemCount() = list?.size ?: 0

    class ViewHolder(val item: NoteItemBinding) : RecyclerView.ViewHolder(item.root) {

        fun bind(note: NoteModel) {
            item.note = note
            item.invalidateAll()
        }


    }

    fun changeData(list: List<NoteModel>) {
        val diffUtil = NotesListDiffUtil(this.list!!, list)
        val results = DiffUtil.calculateDiff(diffUtil)
        this.list = list
        results.dispatchUpdatesTo(this)
    }

    private fun getPriorityColor(priorityModel: PriorityModel): Int {

        return when (priorityModel) {
            PriorityModel.Medium -> R.color.green
            PriorityModel.Low -> R.color.yellow
            PriorityModel.High -> R.color.red


        }
    }
}
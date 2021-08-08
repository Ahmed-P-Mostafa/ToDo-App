package com.polotika.todoapp.ui.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.polotika.todoapp.R
import com.polotika.todoapp.pojo.adapters.ListAdapter
import com.polotika.todoapp.databinding.FragmentHomeBinding
import com.polotika.todoapp.pojo.adapters.SwipeHelper
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.ui.NotesViewModel
import java.text.FieldPosition

class HomeFragment : Fragment(),SearchView.OnQueryTextListener {
    private val TAG = "HomeFragment"
    private val viewModel: NotesViewModel by viewModels()
    lateinit var adapter: ListAdapter
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        adapter = ListAdapter()
        binding.recyclerView.adapter = adapter
        swipeToDelete(binding.recyclerView)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        observers()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query!=null){
            searchInDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query!=null){
            searchInDatabase(query)
        }
        return true    }

    private fun searchInDatabase(query: String){
        viewModel.searchInDatabase("%$query%").observe(viewLifecycleOwner, Observer {
            adapter.changeData(it)
        })
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeHelper() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val noteToDelete = adapter.list?.get(viewHolder.adapterPosition)
                viewModel.deleteNote(noteToDelete!!)
                restoreDeletedItem(viewHolder.itemView,noteToDelete,viewHolder.adapterPosition)
            }
        }


        val itemTouchHelper =ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedItem(view: View,deletedNote:NoteModel,position: Int){
        Snackbar.make(view,"'${deletedNote.title}' Deleted",Snackbar.LENGTH_LONG).setAction("Undo") {
            viewModel.addNote(deletedNote)
            adapter.notifyItemChanged(position)
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as SearchView
        //searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
    }

    private fun observers() {

        viewModel.getAllNotes.observe(requireActivity(), Observer {
            when (it.size) {
                0 -> viewModel.isEmptyList.value = true
                else -> viewModel.isEmptyList.value = false
            }
            adapter.changeData(it)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_deleteAll -> {
                showDialog()
            }
            R.id.menu_priority_low ->{ viewModel.sortByLowPriority().observe(viewLifecycleOwner,
                Observer {
                    adapter.changeData(it)
                })}
            R.id.menu_priority_high ->{ viewModel.sortByHighPriority().observe(viewLifecycleOwner,
                Observer {
                    adapter.changeData(it)
                })}

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog() {
        AlertDialog.Builder(requireContext()).setTitle("Delete all notes ?")
            .setMessage("Are you sure you want to delete all notes ?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                deleteAllNotes()
            }).setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            }).create().show()
    }

    private fun deleteAllNotes() {
        viewModel.deleteAllNotes()
        Toast.makeText(requireContext(), "deleted successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }



}
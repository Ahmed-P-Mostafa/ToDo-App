package com.polotika.todoapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.polotika.todoapp.R
import com.polotika.todoapp.databinding.FragmentHomeBinding
import com.polotika.todoapp.pojo.adapters.ListAdapter
import com.polotika.todoapp.pojo.adapters.SwipeHelper
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.utils.hideKeyboard
import com.polotika.todoapp.pojo.utils.observeOnce
import com.polotika.todoapp.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(),SearchView.OnQueryTextListener {
    private val viewModel: HomeViewModel by viewModels()
    lateinit var adapter: ListAdapter
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        adapter = ListAdapter()
        binding.recyclerView.adapter = adapter
        swipeToDelete(binding.recyclerView)
        setHasOptionsMenu(true)


        hideKeyboard(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("HomeFragment: onViewCreated")
        observers()

        setFragmentResultListener("add_edit_request"){_,bundle ->
           val message = bundle.get("add_edit_result")
            Snackbar.make(requireContext(),requireView(), message.toString(), Snackbar.LENGTH_SHORT)
                .show()
        }
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
        viewModel.searchInDatabase("%$query%").observeOnce(viewLifecycleOwner, {
            adapter.changeData(it)
        })
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeHelper() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val noteToDelete = adapter.list?.get(viewHolder.adapterPosition)
                viewModel.deleteNote(noteToDelete!!)
                restoreDeletedItem(noteToDelete,viewHolder.adapterPosition)
            }
        }


        val itemTouchHelper =ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedItem(deletedNote:NoteModel,position: Int){
        Snackbar.make(requireContext(),requireView(),"'${deletedNote.title}' Deleted",Snackbar.LENGTH_LONG).setAction("Undo") {
            viewModel.addNote(deletedNote)
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    private fun observers() {

        viewModel.getAllNotes.observe(requireActivity(), {
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
                {
                    adapter.changeData(it)
                })}
            R.id.menu_priority_high ->{ viewModel.sortByHighPriority().observe(viewLifecycleOwner,
                {
                    adapter.changeData(it)
                })}

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog() {
        AlertDialog.Builder(requireContext()).setTitle("Delete all notes ?")
            .setMessage("Are you sure you want to delete all notes ?")
            .setPositiveButton("Yes") { _, _ ->
                deleteAllNotes()
            }.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
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
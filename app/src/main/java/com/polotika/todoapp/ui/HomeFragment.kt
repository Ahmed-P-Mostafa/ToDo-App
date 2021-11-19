package com.polotika.todoapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.polotika.todoapp.R
import com.polotika.todoapp.databinding.FragmentHomeBinding
import com.polotika.todoapp.pojo.adapters.ListAdapter
import com.polotika.todoapp.pojo.adapters.SwipeHelper
import com.polotika.todoapp.pojo.adapters.SwipeUtils
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.utils.hideKeyboard
import com.polotika.todoapp.pojo.utils.observeOnce
import com.polotika.todoapp.pojo.utils.onQueryTextChanged
import com.polotika.todoapp.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class HomeFragment : Fragment() ,  TourGuideCallbacks {
    private val TAG = "HomeFragment"
    private val viewModel: HomeViewModel by viewModels()
    lateinit var adapter: ListAdapter
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        adapter = ListAdapter()
        binding.recyclerView.adapter = adapter

        swipeToDelete()
        setHasOptionsMenu(true)

        ShowCaseTourGuide.setListener(this)
        hideKeyboard(requireActivity())
        viewModel.getToken()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observers()
        viewModel.getAllNotesSorted()

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val message = bundle.get("add_edit_result")
            Snackbar.make(
                requireContext(),
                requireView(),
                message.toString(),
                Snackbar.LENGTH_SHORT
            )
                .show()
        }

    }

/*
    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d(TAG, "onQueryTextSubmit: $query")
        if (query != null) {
            searchInDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        Log.d(TAG, "onQueryTextChange: $query")
        if (query !=null) {
            searchInDatabase(query)
        }
        return true
    }
*/


    private fun searchInDatabase(query: String) {
        Log.d(TAG, "searchInDatabase: $query")
        viewModel.searchInDatabase("%$query%").observeOnce(viewLifecycleOwner){
            //adapter.changeData(it)
            viewModel.notesList.value = it
        }
    }

    private fun swipeToDelete() {
        val swipeToDeleteCallback = object : SwipeHelper() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val noteToDelete = adapter.list?.get(viewHolder.adapterPosition)
                viewModel.deleteNote(noteToDelete!!)
                restoreDeletedItem(noteToDelete)
            }
        }


        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun restoreDeletedItem(deletedNote: NoteModel) {
        Snackbar.make(
            requireContext(),
            requireView(),
            "'${deletedNote.title}' Deleted",
            Snackbar.LENGTH_LONG
        ).setAction("Undo") {
            viewModel.addNote(deletedNote)
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as SearchView
        searchView.onQueryTextChanged{
            searchInDatabase(it)
        }
    }

    private fun observers() {

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.notesList.observe(viewLifecycleOwner){ notes ->
                if (notes!=null){
                    when(notes.size){
                        0-> viewModel.isEmptyList.value = true
                        else -> viewModel.isEmptyList.value = false
                    }
                    adapter.changeData(notes)
                }
            }
        }

        viewModel.isTourGuideUiState.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    ShowCaseTourGuide.showCaseSearch(view?.rootView?.findViewById(R.id.menu_search)!!,requireActivity())
                }
                false -> {

                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_deleteAll -> {
                showDeleteAllDialog()
            }
            R.id.menu_priority_low -> {
                viewModel.sortByLowPriority()
            }
            R.id.menu_priority_high -> {
                viewModel.sortByHighPriority()
            }
            R.id.menu_date -> {
                viewModel.sortByDate()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteAllDialog() {
        AlertDialog.Builder(requireContext()).setTitle("Delete all notes ?")
            .setMessage("Are you sure you want to delete all notes ?")
            .setPositiveButton("Yes") { _, _ ->
                deleteAllNotes()
            }.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.create()
            .show()
    }

    private fun deleteAllNotes() {
        viewModel.deleteAllNotes()
        Toast.makeText(requireContext(), "deleted successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    override fun onSearchDoneCallback() {
        ShowCaseTourGuide.showCaseSwipeToDelete(binding.recyclerView.getChildAt(0),requireActivity())
        lifecycleScope.launchWhenResumed {
            delay(1000L)
            SwipeUtils.swipeRecyclerViewItem(binding.recyclerView,0,300,ItemTouchHelper.START,500)
            delay(1000)
            SwipeUtils.swipeRecyclerViewItem(binding.recyclerView,0,300,ItemTouchHelper.END,500)
        }
    }

    override fun onNewNoteDoneCallback() {
        viewModel.showCaseTourGuideFinished()
    }

    override fun onSwipeDoneCallback() {
        ShowCaseTourGuide.showCaseNewNoteButton(binding.fabAdd,requireActivity())
    }



}
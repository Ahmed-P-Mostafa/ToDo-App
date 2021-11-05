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
import com.polotika.todoapp.pojo.adapters.*
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.utils.hideKeyboard
import com.polotika.todoapp.pojo.utils.observeOnce
import com.polotika.todoapp.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity

@AndroidEntryPoint
class HomeFragment : Fragment(), SearchView.OnQueryTextListener, TourGuideCallbacks {
    private val TAG = "HomeFragment"
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

        ShowCaseTourGuide.setListener(this)


        hideKeyboard(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: ${viewModel.savedInstance}")
        observers()
        //requireActivity().openOptionsMenu()

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

    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d(TAG, "onQueryTextSubmit: $query")
        if (query != null) {
            searchInDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        Log.d(TAG, "onQueryTextChange: $query")
        if (query != null) {
            searchInDatabase(query)
        }
        return true
    }


    private fun searchInDatabase(query: String) {
        viewModel.searchInDatabase("%$query%").observeOnce(viewLifecycleOwner, {
            adapter.changeData(it)
        })
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeHelper() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val noteToDelete = adapter.list?.get(viewHolder.adapterPosition)
                viewModel.deleteNote(noteToDelete!!)
                restoreDeletedItem(noteToDelete, viewHolder.adapterPosition)
            }
        }


        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedItem(deletedNote: NoteModel, position: Int) {
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
        searchView.setOnQueryTextListener(this)
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.notesChannel.receiveAsFlow().collect {
                when (it.size) {
                    0 -> viewModel.isEmptyList.value = true
                    else -> viewModel.isEmptyList.value = false
                }
                adapter.changeData(it)
            }
        }

      /*  viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.getAllNotes(viewModel.sortingState.first()).observe(viewLifecycleOwner) {
                when (it.size) {
                    0 -> viewModel.isEmptyList.value = true
                    else -> viewModel.isEmptyList.value = false
                }
                adapter.changeData(it)
            }
        }*/

        viewModel.uistate.observe(viewLifecycleOwner) {
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
                viewModel.sortByLowPriority().observe(viewLifecycleOwner) {
                    adapter.changeData(it)
                }
            }
            R.id.menu_priority_high -> {
                viewModel.sortByHighPriority().observe(viewLifecycleOwner, {
                    adapter.changeData(it)
                })
            }
            R.id.menu_date -> {
                viewModel.sortByDate().observe(viewLifecycleOwner, {
                    adapter.changeData(it)
                })
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

    override fun onNewNoteDoneCallback() {
        Toast.makeText(requireContext(), "add note done", Toast.LENGTH_SHORT).show()
    }

    override fun onSwipeDoneCallback() {
        lifecycleScope.launchWhenResumed {
            delay(1000L)
            //binding.recyclerView.performSwipeToRight(binding.recyclerView.getChildAt(3),100f)
            SwipeUtils.swipeRecyclerViewItem(binding.recyclerView,0,200,ItemTouchHelper.START,500)
            delay(500)
            //SwipeUtils.swipeRecyclerViewItem(binding.recyclerView,0,50,ItemTouchHelper.END,1000)
        }
    }

    override fun onSearchDoneCallback() {
        ShowCaseTourGuide.showCaseOverflowMenu(view?.rootView?.findViewById(R.id.overflowActionButton)!!,requireActivity())
    }

    override fun onOverflowMeuDoneCallback() {
        ShowCaseTourGuide.showCaseSwipeToDelete(binding.recyclerView.getChildAt(3),requireActivity())
    }
}
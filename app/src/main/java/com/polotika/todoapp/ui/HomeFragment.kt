package com.polotika.todoapp.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.polotika.todoapp.R
import com.polotika.todoapp.data.ListAdapter
import com.polotika.todoapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    val viewModel :NotesViewModel by viewModels()
    lateinit var adapter :ListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }
        adapter = ListAdapter()
        binding.recyclerView.adapter = adapter
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        observers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu,menu)
    }
    private fun observers(){

        viewModel.getAllNotes.observe(requireActivity(), Observer {
            adapter.changeData(it)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_deleteAll->{
                showDialog()
            }
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
        findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
    }


}
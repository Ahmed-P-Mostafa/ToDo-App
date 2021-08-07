package com.polotika.todoapp.ui

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.polotika.todoapp.R
import com.polotika.todoapp.data.models.NoteModel
import com.polotika.todoapp.data.models.PriorityModel
import com.polotika.todoapp.databinding.FragmentAddBinding
import com.polotika.todoapp.databinding.FragmentHomeBinding


class AddFragment : Fragment() {

    private val viewModel: NotesViewModel by viewModels()
    lateinit var binding: FragmentAddBinding
    val prioritiesList = listOf("Low Priority" , "Medium Priority","High Priority")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false)
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            prioritiesList
        )
        binding.priorityTv.setAdapter(adapter)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                if (validateUserData()) {
                    val note = NoteModel(
                        title = binding.titleEt.text.toString(),
                        description = binding.descriptionEt.text.toString(),
                        priority = getPriority(binding.priorityTv.text.toString())
                    )
                    viewModel.addNote(noteModel = note)
                    Snackbar.make(binding.root, "Successfully added", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_addFragment_to_homeFragment)
                } else {
                    Snackbar.make(binding.root, "Empty field", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getPriority(priority: String): PriorityModel {
        return when (priority) {
            "High Priority" -> PriorityModel.High
            "Medium Priority" -> PriorityModel.Medium
            "Low Priority" -> PriorityModel.Low

            else -> PriorityModel.Low
        }
    }

    private fun validateUserData(): Boolean {
        return !(binding.titleEt.text.isNullOrBlank() || binding.descriptionEt.text.isNullOrBlank())
    }


}
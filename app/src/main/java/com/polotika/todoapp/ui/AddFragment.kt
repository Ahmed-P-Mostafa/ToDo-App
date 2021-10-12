package com.polotika.todoapp.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.polotika.todoapp.R
import com.polotika.todoapp.databinding.FragmentAddBinding
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.viewModel.AddViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddFragment : Fragment() {
    private val TAG = "AddFragment"

    private val viewModel: AddViewModel by viewModels()
    lateinit var binding: FragmentAddBinding
    val prioritiesList = listOf("Low Priority" , "Medium Priority","High Priority")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container,false)
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            prioritiesList
        )
        binding.priorityTv.setAdapter(adapter)

        binding.priorityTv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            when (position) {
                0-> {
                    binding.priorityTv.setTextColor(ContextCompat.getColor(requireContext(),R.color.yellow))}
                1-> {
                    binding.priorityTv.setTextColor(ContextCompat.getColor(requireContext(),R.color.green))}
                2-> {
                    binding.priorityTv.setTextColor(ContextCompat.getColor(requireContext(),R.color.red))}
            }
        }
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
                if (viewModel.validateUserData(binding.titleEt.text.toString(),binding.descriptionEt.text.toString())) {
                    val note = NoteModel(
                        id= 0,
                        title = binding.titleEt.text.toString(),
                        description = binding.descriptionEt.text.toString(),
                        priority = viewModel.getPriority(binding.priorityTv.text.toString())
                    )
                    viewModel.addNote(noteModel = note)
                    Snackbar.make(requireContext(),requireView(), "added successfully", Snackbar.LENGTH_SHORT)
                        .show()

                    findNavController().navigate(R.id.action_addFragment_to_homeFragment)
                } else {
                    Snackbar.make(requireContext(),requireView(), "Empty field", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }





}
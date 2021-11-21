package com.polotika.todoapp.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.polotika.todoapp.R
import com.polotika.todoapp.databinding.FragmentAddBinding
import com.polotika.todoapp.viewModel.AddNoteState
import com.polotika.todoapp.viewModel.AddViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class AddFragment : Fragment() {
    private val TAG = "AddFragment"
    private val args by navArgs<AddFragmentArgs>()

    private val viewModel: AddViewModel by viewModels()
    lateinit var binding: FragmentAddBinding
    val prioritiesList = listOf("Low Priority" , "Medium Priority","High Priority")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container,false)
        binding.vm = viewModel
        Log.d(TAG, "onCreateView: ${args.shareNote}")

        if (args.shareNote!=null){
            Log.d(TAG, "onCreateView: ${args.shareNote}")
            viewModel.body.setValue("• "+args.shareNote)
        }
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
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.addNoteState.collect {
                when(it){
                    AddNoteState.CompleteState -> {
                        setFragmentResult("add_edit_request", bundleOf("add_edit_result" to "Added successfully"))
                        findNavController().popBackStack()
                    }
                    AddNoteState.EmptyDataState -> {
                        Snackbar.make(requireContext(),requireView(), "Empty field", Snackbar.LENGTH_SHORT).show()

                    }
                    AddNoteState.EmptyState ->{

                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                viewModel.onAddClicked()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }





}
package com.polotika.todoapp.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.polotika.todoapp.R
import com.polotika.todoapp.databinding.FragmentUpdateBinding
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.pojo.data.models.PriorityModel
import com.polotika.todoapp.viewModel.NotesViewModel
import kotlinx.coroutines.flow.collect


class UpdateFragment : Fragment() {

    val args by navArgs<UpdateFragmentArgs>()
    lateinit var binding: FragmentUpdateBinding
    val viewModel: NotesViewModel by viewModels()
    val prioritiesList = listOf("Low Priority", "Medium Priority", "High Priority")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(container?.context),
            R.layout.fragment_update,
            container,
            false
        )
        binding.note = args.note
        viewModel.body.value = args.note.description
        viewModel.title.value = args.note.title
        setHasOptionsMenu(true)
        binding.currentTitleEt.setText(args.note.title)
        binding.currentDescriptionEt.setText(args.note.description)
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            prioritiesList
        )
        binding.currentPriorityTv.setAdapter(adapter)

        binding.currentPriorityTv.setText(getPriorityText(args.note.priority), false)

        binding.currentPriorityTv.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                when (position) {
                    0 -> {
                        binding.currentPriorityTv.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.yellow
                            )
                        )
                    }
                    1 -> {
                        binding.currentPriorityTv.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                    }
                    2 -> {
                        binding.currentPriorityTv.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                    }
                }
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*
        viewModel.noteBodySingleLiveEvent.observe(viewLifecycleOwner, Observer {
            Log.d("TAG", "onViewCreated: $it")
            shareNote(it,"")
        })*/

    }

    private fun getPriorityText(priority: PriorityModel): String {
        when (priority) {
            PriorityModel.High -> {
                binding.currentPriorityTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext().applicationContext,
                        R.color.red
                    )
                )
                return prioritiesList[2]
            }

            PriorityModel.Medium -> {
                binding.currentPriorityTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext().applicationContext,
                        R.color.green
                    )
                )

                return prioritiesList[1]
            }
            PriorityModel.Low -> {
                binding.currentPriorityTv.setTextColor(
                    ContextCompat.getColor(
                        requireContext().applicationContext,
                        R.color.yellow
                    )
                )

                return prioritiesList[0]
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> updateNote()
            R.id.menu_delete -> showDialog()
            R.id.menu_share -> lifecycleScope.launchWhenStarted {
                    viewModel.com.collect {
                        shareNote(it.second,it.first)
                    }
                }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog() {
            AlertDialog.Builder(requireContext()).setTitle("Delete '${args.note.title}' ?")
                .setMessage("Are you sure you want to delete '${args.note.title}' ?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                    deleteNote()
                }).setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                }).create().show()
    }

    private fun deleteNote() {

        viewModel.deleteNote(getNote())
        Toast.makeText(requireContext(), "deleted successfully", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
    }

    private fun updateNote() {
        if (viewModel.validateUserData(
                binding.currentTitleEt.text.toString(),
                binding.currentDescriptionEt.text.toString()
            )
        ) {
            val note = getNote()
            viewModel.updateNote(note)
            Snackbar.make(requireContext(),requireView(), "updated successfully", Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
        } else Snackbar.make(requireContext(),requireView(), "data incomplete", Snackbar.LENGTH_SHORT).show()

    }

    private fun shareNote(body:String,title:String){
       val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val shareMethod =  getString(R.string.share_method)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, body)
        startActivity(Intent.createChooser(shareIntent,shareMethod))
    }


    private fun getNote(): NoteModel {
        return NoteModel(
            id = args.note.id,
            title = binding.currentTitleEt.text.toString(),
            description = binding.currentDescriptionEt.text.toString(),
            priority = viewModel.getPriority(binding.currentPriorityTv.text.toString())
        )

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }


}
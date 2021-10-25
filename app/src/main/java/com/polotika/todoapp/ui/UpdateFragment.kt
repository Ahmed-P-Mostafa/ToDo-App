package com.polotika.todoapp.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
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
import com.polotika.todoapp.databinding.FragmentUpdateBinding
import com.polotika.todoapp.pojo.data.models.NoteModel
import com.polotika.todoapp.viewModel.UpdateFragmentState
import com.polotika.todoapp.viewModel.UpdateViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class UpdateFragment : Fragment() {

    // TODO fix latency in updating note color after update

    val args by navArgs<UpdateFragmentArgs>()
    lateinit var binding: FragmentUpdateBinding
    val viewModel: UpdateViewModel by viewModels()
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.updateFragmentState.collect {
                when (it) {
                    UpdateFragmentState.CompleteState -> {
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to "updated successfully")
                        )
                        findNavController().popBackStack()
                    }
                    UpdateFragmentState.EmptyDataState -> {
                        Snackbar.make(
                            requireContext(),
                            requireView(),
                            "data incomplete",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }
                    UpdateFragmentState.EmptyState -> {

                    }
                    UpdateFragmentState.DeleteDialogState -> {
                        showDeleteDialog()
                    }
                    UpdateFragmentState.ShareNoteState -> {
                        viewModel.com.collect {
                            shareNote(it.second, it.first)
                        }
                    }
                    UpdateFragmentState.ConfirmDeleteState -> {
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to "Deleted successfully")
                        )
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> viewModel.onUpdateClicked()
            R.id.menu_delete -> viewModel.onDeleteClicked()
            R.id.menu_share -> viewModel.onShareClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext()).setTitle("Delete '${args.note.title}' ?")
            .setMessage("Are you sure you want to delete '${args.note.title}' ?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                viewModel.onConfirmDeleteClicked()
            }).setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            }).create().show()
    }


    private fun shareNote(body: String, title: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val shareMethod = getString(R.string.share_method)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, body)
        startActivity(Intent.createChooser(shareIntent, shareMethod))
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

}
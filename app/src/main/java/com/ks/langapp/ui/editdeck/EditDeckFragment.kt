package com.ks.langapp.ui.editdeck

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ks.langapp.R
import com.ks.langapp.databinding.FragmentEditDeckBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditDeckFragment : Fragment() {

    private val viewModel: EditDeckViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding: FragmentEditDeckBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_deck, container, false)

        val arguments = EditDeckFragmentArgs.fromBundle(requireArguments())
        viewModel.processArguments(arguments.deckId, arguments.navigatedFromDeckFragment)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.buttonSave.setOnClickListener{
            if(!binding.deckName.text.isNullOrBlank()) {
                viewModel.onSaveDeck(binding.deckName.text.toString())
            } else Toast.makeText(context, R.string.form_incomplete, Toast.LENGTH_SHORT).show()
        }

        binding.buttonDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_deck)
                .setMessage(getString(R.string.are_you_sure, viewModel.deck.value!!.name))
                .setPositiveButton(R.string.delete) { _, _ ->
                    viewModel.onDeleteDeck()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setCancelable(true)
                .show()
        }

        lifecycleScope.launch {
            viewModel.navigateBackSkipDeckFragment.collectLatest {
                if (it) {
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                } else findNavController().popBackStack()
            }
        }

        return binding.root
    }
}
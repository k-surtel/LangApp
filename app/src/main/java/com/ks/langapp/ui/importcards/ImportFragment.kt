package com.ks.langapp.ui.importcards

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ks.langapp.R
import com.ks.langapp.databinding.FragmentImportBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImportFragment : Fragment() {

    private val viewModel: ImportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentImportBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_import, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val fileResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    data?.data?.let {
                        viewModel.processUri(it, requireContext().contentResolver)
                    }
                }
            }

        binding.chooseFileButton.setOnClickListener { openFile(fileResultLauncher) }

        binding.chooseDeckButton.setOnClickListener { onChooseDeck() }

        binding.termsSeparatorButton.setOnClickListener { onSeparatorButtonClick(ImportViewModel.SeparatorType.TERMS) }

        binding.cardsSeparatorButton.setOnClickListener { onSeparatorButtonClick(ImportViewModel.SeparatorType.CARDS) }

        binding.importButton.setOnClickListener { viewModel.onImportClick() }

        lifecycleScope.launch {
            viewModel.event.collectLatest {
                when (it) {
                    ImportViewModel.ImportEvent.NAVIGATE_BACK ->
                        findNavController().popBackStack()
                    ImportViewModel.ImportEvent.NO_FILE_SELECTED ->
                        showNoFileSelectedMessage()
                    ImportViewModel.ImportEvent.BAD_FILE ->
                        showBadFileMessage()
                    ImportViewModel.ImportEvent.NO_CARDS_CREATED ->
                        showBadFileMessage()
                    ImportViewModel.ImportEvent.NO_DECK_SELECTED ->
                        showNoDeckSelectedMessage()
                }
            }
        }

        return binding.root
    }

    private fun onChooseDeck() {
        lifecycleScope.launch {
            viewModel.getAllDecks().collectLatest {

                val deckNames = it.map { deck -> deck.name }
                val deckNamesArray = deckNames.toTypedArray() + arrayOf(getString(R.string.create_new_deck))

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.choose_deck)
                    .setItems(deckNamesArray) { _, which ->
                        if (which < it.size) viewModel.chooseDeck(it[which])
                        else createANewDeck()
                    }
                    .setCancelable(true)
                    .show()
            }
        }
    }

    private fun createANewDeck() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_new_deck, null)
        val deckName = dialogView.findViewById<EditText>(R.id.name)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.create_new_deck)
            .setView(dialogView)
            .setPositiveButton(R.string.ok) { _, _ -> viewModel.saveDeck(deckName.text.toString()) }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setCancelable(true)
            .show()
    }

    private fun openFile(resultLauncher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*"
            //putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(""))
        }
        resultLauncher.launch(intent)
    }

    private fun onSeparatorButtonClick(separatorType: ImportViewModel.SeparatorType) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_choose_separator, null)

        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radio_group)
        val editText = dialogView.findViewById<EditText>(R.id.character)

        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) radioGroup.check(R.id.character_radio)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.choose_separator)
            .setView(dialogView)
            .setPositiveButton(R.string.ok) { _, _ ->
                when (radioGroup.checkedRadioButtonId) {
                    R.id.new_line_radio -> viewModel.onSeparatorChange(separatorType, ImportViewModel.Separator.NewLine)
                    R.id.character_radio -> {
                        if (editText.text.isNullOrEmpty()) {
                            Snackbar.make(requireView(), R.string.char_field_is_empty, Snackbar.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }
                        viewModel.onSeparatorChange(separatorType, ImportViewModel.Separator.CharSeparator(editText.text[0]))
                    }
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setCancelable(true)
            .show()
    }

    private fun showNoFileSelectedMessage() {
        Toast.makeText(requireContext(), R.string.no_file, Toast.LENGTH_SHORT).show()
    }

    private fun showBadFileMessage() {
        Toast.makeText(requireContext(), R.string.no_file, Toast.LENGTH_SHORT).show()
    }

    private fun showNoDeckSelectedMessage() {
        Toast.makeText(requireContext(), R.string.no_deck, Toast.LENGTH_SHORT).show()
    }
}
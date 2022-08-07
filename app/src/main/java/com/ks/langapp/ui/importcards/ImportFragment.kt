package com.ks.langapp.ui.importcards

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ks.langapp.R
import com.ks.langapp.databinding.FragmentImportBinding
import dagger.hilt.android.AndroidEntryPoint

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

        binding.chooseFileButton.setOnClickListener {
            openFile(fileResultLauncher, null)
        }

        binding.termsSeparatorButton.setOnClickListener { onSeparatorButtonClick(ImportViewModel.SeparatorType.TERMS) }

        binding.cardsSeparatorButton.setOnClickListener { onSeparatorButtonClick(ImportViewModel.SeparatorType.CARDS) }

        return binding.root
    }

    private fun openFile(resultLauncher: ActivityResultLauncher<Intent>, pickerInitialUri: Uri?) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*"
            //putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(""))

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
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
}
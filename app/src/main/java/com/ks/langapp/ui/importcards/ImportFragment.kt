package com.ks.langapp.ui.importcards

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

        val fileResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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

        binding.cardsSeparatorButton.setOnClickListener { onCardsSeparatorClick() }

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

    private fun onCardsSeparatorClick() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.choose_cards_separator))
        builder.setView(R.layout.dialog_choose_cards_separator)
        val dialog = builder.create()
        dialog.show()
    }
}
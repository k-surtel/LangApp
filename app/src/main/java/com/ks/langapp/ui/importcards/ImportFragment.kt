package com.ks.langapp.ui.importcards

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ks.langapp.R
import com.ks.langapp.database.LangDatabase
import com.ks.langapp.databinding.FragmentImportBinding

class ImportFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding: FragmentImportBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_import, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = LangDatabase.getInstance(application).langDatabaseDao
        //val arguments = EditCardFragmentArgs.fromBundle(requireArguments())

        val viewModelFactory = ImportViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(requireActivity(), viewModelFactory)
            .get(ImportViewModel::class.java)

        //viewModel.processArguments(arguments.cardId)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this



        openFile(null)


        return binding.root
    }

    fun openFile(pickerInitialUri: Uri?) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, 2)
    }
}
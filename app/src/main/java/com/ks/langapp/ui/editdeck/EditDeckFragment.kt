package com.ks.langapp.ui.editdeck

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.ks.langapp.R
import com.ks.langapp.data.database.LangDatabase
import com.ks.langapp.databinding.FragmentEditDeckBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditDeckFragment : Fragment() {

    private val viewModel: EditDeckViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding: FragmentEditDeckBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_deck, container, false)

        val arguments = EditDeckFragmentArgs.fromBundle(requireArguments())
        viewModel.processArguments(arguments.deckId)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.buttonSave.setOnClickListener{
            if(binding.name.text.isNotBlank()) viewModel.onSaveDeck(binding.name.text.toString())
             else Toast.makeText(context, R.string.form_incomplete, Toast.LENGTH_SHORT).show()
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) {
            if(it) {
                viewModel.onNavigateBackCalled()
                //navigateBack()
            }
        }

        return binding.root
    }

//    private fun navigateBack() {
//        navigate(EditDeckFragmentDirections.actionEditDeckFragmentToDecksFragment())
//    }
}
package com.ks.langapp.ui.editcard

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
import com.ks.langapp.R
import com.ks.langapp.databinding.FragmentEditCardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditCardFragment : Fragment() {

    private val viewModel: EditCardViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding: FragmentEditCardBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_card, container, false)

        val arguments = EditCardFragmentArgs.fromBundle(requireArguments())
        viewModel.processArguments(arguments.cardId)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.saveButton.setOnClickListener{
            if(!binding.front.text.isNullOrBlank() && !binding.back.text.isNullOrBlank()) {
                viewModel.onSaveCard(arguments.deckId, binding.front.text.toString(),
                    binding.back.text.toString())
            } else
                Toast.makeText(context, R.string.form_incomplete, Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            viewModel.navigateBack.collectLatest {
                findNavController().popBackStack()
            }
        }

        return binding.root
    }
}
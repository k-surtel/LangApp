package com.ks.langapp.ui.editcard

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
import com.ks.langapp.databinding.FragmentEditCardBinding
import com.ks.langapp.ui.utils.navigate
import dagger.hilt.android.AndroidEntryPoint

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
            if(!binding.deckName.text.isNullOrBlank() && !binding.description.text.isNullOrBlank()) {
                viewModel.onSaveCard(arguments.deckId, binding.deckName.text.toString(), binding.description.text.toString())
                navigateBack(arguments.deckId)
            } else
                Toast.makeText(context, R.string.form_incomplete, Toast.LENGTH_SHORT).show()
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) {
            if(it) {
                viewModel.onNavigateBackCalled()
                navigateBack(arguments.deckId)
            }
        }

        binding.buttonDelete.setOnClickListener { viewModel.onDeleteCard() }

        return binding.root
    }

    private fun navigateBack(deckId: Long) {
        navigate(EditCardFragmentDirections.actionEditCardFragmentToCardsFragment(deckId))
    }
}
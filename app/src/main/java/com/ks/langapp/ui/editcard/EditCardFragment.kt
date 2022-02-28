package com.ks.langapp.ui.editcard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ks.langapp.R
import com.ks.langapp.database.LangDatabase
import com.ks.langapp.databinding.FragmentEditCardBinding
import com.ks.langapp.ui.utils.navigate

class EditCardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding: FragmentEditCardBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_card, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = LangDatabase.getInstance(application).langDatabaseDao
        val arguments = EditCardFragmentArgs.fromBundle(requireArguments())

        val viewModelFactory = EditCardViewModelFactory(dataSource, application)
        val viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[EditCardViewModel::class.java]

        viewModel.processArguments(arguments.cardId)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.saveButton.setOnClickListener{
            if(!binding.deckName.text.isNullOrBlank() && !binding.description.text.isNullOrBlank()) {
                if(arguments.cardId == Long.MIN_VALUE)
                    viewModel.onSaveCard(arguments.deckId, binding.deckName.text.toString(), binding.description.text.toString())
                else
                    viewModel.onUpdateCard(binding.deckName.text.toString(), binding.description.text.toString())

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
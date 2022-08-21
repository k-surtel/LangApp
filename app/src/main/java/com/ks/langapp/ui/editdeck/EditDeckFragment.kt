package com.ks.langapp.ui.editdeck

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.ks.langapp.R
import com.ks.langapp.databinding.FragmentEditDeckBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class EditDeckFragment : Fragment() {

    private val viewModel: EditDeckViewModel by viewModels()
    private lateinit var binding: FragmentEditDeckBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_deck, container, false)

        val arguments = EditDeckFragmentArgs.fromBundle(requireArguments())
        viewModel.processArguments(arguments.deckId, arguments.navigatedFromDeckFragment)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupCustomEndIcon(binding.frontLanguageLayout)
        setupCustomEndIcon(binding.backLanguageLayout)
        //binding.backLanguage.inputType = InputType.TYPE_NULL


        binding.frontLanguage.setOnClickListener { chooseLanguage(Side.FRONT) }
        binding.backLanguage.setOnClickListener { chooseLanguage(Side.BACK) }

        binding.buttonSave.setOnClickListener{
            if(!binding.deckName.text.isNullOrBlank()) {
                if (binding.frontLanguage.text.toString().isBlank())
                    viewModel.setLanguage(null, Side.FRONT)
                if (binding.backLanguage.text.toString().isBlank())
                    viewModel.setLanguage(null, Side.BACK)
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
            viewModel.event.collectLatest {
                when (it) {
                    EditDeckViewModel.EditDeckEvent.NAVIGATE_BACK_SKIP_DECK_FRAGMENT -> {
                        findNavController().popBackStack()
                        findNavController().popBackStack()
                    }
                    EditDeckViewModel.EditDeckEvent.NAVIGATE_BACK ->
                        findNavController().popBackStack()
                    EditDeckViewModel.EditDeckEvent.DECK_LOADED -> {
                        if (viewModel.deck.value == null) {
                            setEditTextEndIconVisible(false, Side.FRONT)
                            setEditTextEndIconVisible(false, Side.BACK)
                        } else {
                            setEditTextEndIconVisible(viewModel.deck.value!!.frontLanguage != null, Side.FRONT)
                            setEditTextEndIconVisible(viewModel.deck.value!!.backLanguage != null, Side.BACK)
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun chooseLanguage(side: Side) {
        val locales = Locale.getAvailableLocales().distinctBy { it.language }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.choose_language)
            .setItems(locales.map { it.displayLanguage }.toTypedArray()) { dialog, which ->
                when (side) {
                    Side.FRONT -> {
                        binding.frontLanguage.setText(locales[which].displayLanguage)
                        viewModel.setLanguage(locales[which].language, Side.FRONT)
                        setEditTextEndIconVisible(true, Side.FRONT)
                    }
                    Side.BACK -> {
                        binding.backLanguage.setText(locales[which].displayLanguage)
                        viewModel.setLanguage(locales[which].language, Side.BACK)
                        setEditTextEndIconVisible(true, Side.BACK)
                    }
                }
            }
            .setCancelable(true)
            .show()
    }


    private fun setupCustomEndIcon(view: TextInputLayout) {
        view.setEndIconOnClickListener {
            view.editText?.setText("")
            view.isEndIconVisible = false
        }
    }

    private fun setEditTextEndIconVisible(value: Boolean, side: Side) {
        when (side) {
            Side.FRONT -> binding.frontLanguageLayout.isEndIconVisible = value
            Side.BACK -> binding.backLanguageLayout.isEndIconVisible = value
        }
    }

    enum class Side { FRONT, BACK }
}
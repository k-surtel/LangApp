package com.ks.langapp.ui.flashcard

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ks.langapp.R
import com.ks.langapp.databinding.FragmentFlashcardBinding
import com.ks.langapp.ui.utils.convertTimeFromTimestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FlashcardFragment : Fragment() {

    private val viewModel: FlashcardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentFlashcardBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_flashcard, container, false
        )

        val arguments = FlashcardFragmentArgs.fromBundle(requireArguments())
        viewModel.processArguments(arguments.deckId)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.editButton.setOnClickListener { editCard() }

        lifecycleScope.launch {
            viewModel.event.collectLatest { findNavController().popBackStack() }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val reviewStats = viewModel.saveStats()

        reviewStats?.let {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.well_done)
                .setMessage(getString(
                    R.string.review_stats,
                    it.cardsReviewed,
                    convertTimeFromTimestamp(it.time)
                ))
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        }
    }

    private fun editCard() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_card, null)
        val front = dialogView.findViewById<EditText>(R.id.front)
        val back = dialogView.findViewById<EditText>(R.id.back)

        front.setText(viewModel.currentCard.value?.front ?: "")
        back.setText(viewModel.currentCard.value?.back ?: "")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_card)
            .setView(dialogView)
            .setPositiveButton(R.string.ok) { _, _ ->
                if (!front.text.isNullOrBlank() && !back.text.isNullOrBlank())
                    viewModel.saveCard(front.text.toString(), back.text.toString())
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setCancelable(true)
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.flashcards_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        // todo
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}
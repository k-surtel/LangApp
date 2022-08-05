package com.ks.langapp.ui.listOfDecks

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils.loadAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.ks.langapp.R
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.databinding.FragmentListOfDecksBinding
import com.ks.langapp.ui.adapters.DecksAdapter
import com.ks.langapp.ui.adapters.DecksListener
import com.ks.langapp.ui.utils.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListOfDecksFragment : Fragment() {

    private lateinit var binding: FragmentListOfDecksBinding
    private val viewModel: ListOfDecksViewModel by viewModels()

    private val fabOpenRotation: Animation by lazy { loadAnimation(requireContext(), R.anim.fab_open_rotate) }
    private val fabCloseRotation: Animation by lazy { loadAnimation(requireContext(), R.anim.fab_close_rotate) }
    private val fabAppear: Animation by lazy { loadAnimation(requireContext(), R.anim.fab_appear) }
    private val fabDisappear: Animation by lazy { loadAnimation(requireContext(), R.anim.fab_disappear) }
    private var fabMenuOpened = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list_of_decks, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = DecksAdapter(DecksListener { deck ->
            onDeckClick(deck)
        })
        lifecycleScope.launch { viewModel.decks.collectLatest { adapter.submitList(it) } }
        binding.decks.adapter = adapter

        fabDisappear.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                setFabViewsVisibility(View.GONE)
            }
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        binding.fab.setOnClickListener {
            onFabClick()
        }

        binding.fabAdd.setOnClickListener {
            fabMenuOpened = false
            navigate(ListOfDecksFragmentDirections
                .actionListOfDecksFragmentToEditDeckFragment(Long.MIN_VALUE, false))
        }

        binding.fabImport.setOnClickListener {
            fabMenuOpened = false
            navigate(ListOfDecksFragmentDirections.actionListOfDecksFragmentToImportFragment())
        }

        return binding.root
    }

    private fun onDeckClick(deck: Deck) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(deck.name)
        builder.setItems(
            R.array.dialog_deck_actions
        ) { _, which ->

            when (which) {
                0 -> navigate(ListOfDecksFragmentDirections
                    .actionListOfDecksFragmentToDeckFragment(deck.deckId))

                1 -> {
                    if (deck.cardsCount > 0) navigate(ListOfDecksFragmentDirections
                        .actionListOfDecksFragmentToFlashcardFragment(deck.deckId))
                    else Snackbar.make(requireView(), R.string.this_deck_is_empty, Snackbar.LENGTH_SHORT)
                        .show()
                }

                2 -> navigate(ListOfDecksFragmentDirections
                    .actionListOfDecksFragmentToEditDeckFragment(deck.deckId, false))

                3 -> navigate(ListOfDecksFragmentDirections.actionListOfDecksFragmentToDeckStatsFragment())
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun onFabClick() {
        if (fabMenuOpened) {
            binding.fab.startAnimation(fabCloseRotation)
            startFabsAnimations(fabDisappear)
        } else {
            setFabViewsVisibility(View.INVISIBLE)
            binding.fab.startAnimation(fabOpenRotation)
            startFabsAnimations(fabAppear)
        }

        fabMenuOpened = !fabMenuOpened
    }

    private fun setFabViewsVisibility(visibility: Int) {
        binding.fabAdd.visibility = visibility
        binding.fabAddLabel.visibility = visibility
        binding.fabImport.visibility = visibility
        binding.fabImportLabel.visibility = visibility
    }

    private fun startFabsAnimations(animation: Animation) {
        binding.fabAdd.startAnimation(animation)
        binding.fabAddLabel.startAnimation(animation)
        binding.fabImport.startAnimation(animation)
        binding.fabImportLabel.startAnimation(animation)
    }
}
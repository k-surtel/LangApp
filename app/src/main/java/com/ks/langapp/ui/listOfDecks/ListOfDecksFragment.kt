package com.ks.langapp.ui.listOfDecks

import android.app.AlertDialog
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

        setHasOptionsMenu(true)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = DecksAdapter(DecksListener { deck ->
            onDeckClick(deck)
        })
        lifecycleScope.launch { viewModel.decks.collectLatest { adapter.submitList(it) } }
        binding.decks.adapter = adapter

        fabAppear.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                binding.fabAdd.visibility = View.INVISIBLE
                binding.fabAddLabel.visibility = View.INVISIBLE
                binding.fabImport.visibility = View.INVISIBLE
                binding.fabImportLabel.visibility = View.INVISIBLE

            }

            override fun onAnimationEnd(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

        })

        fabDisappear.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                binding.fabAdd.visibility = View.GONE
                binding.fabAddLabel.visibility = View.GONE
                binding.fabImport.visibility = View.GONE
                binding.fabImportLabel.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

        })

        binding.fab.setOnClickListener {
            onFabClick()
        }

        return binding.root
    }

    private fun onDeckClick(deck: Deck) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(deck.name)
        builder.setItems(
            R.array.dialog_deck_actions
        ) { dialog, which ->

            when (which) {
                0 -> navigate(ListOfDecksFragmentDirections
                    .actionListOfDecksFragmentToDeckFragment(deck.deckId))

                1 -> navigate(ListOfDecksFragmentDirections
                    .actionListOfDecksFragmentToFlashcardFragment(deck.deckId))

                2 -> navigate(ListOfDecksFragmentDirections
                    .actionListOfDecksFragmentToEditDeckFragment(deck.deckId, false))
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun onFabClick() {
        if (fabMenuOpened) {
            binding.fab.startAnimation(fabCloseRotation)
            binding.fabAdd.startAnimation(fabDisappear)
            binding.fabAddLabel.startAnimation(fabDisappear)
            binding.fabImport.startAnimation(fabDisappear)
            binding.fabImportLabel.startAnimation(fabDisappear)
        } else {
            binding.fab.startAnimation(fabOpenRotation)
            binding.fabAdd.startAnimation(fabAppear)
            binding.fabAddLabel.startAnimation(fabAppear)
            binding.fabImport.startAnimation(fabAppear)
            binding.fabImportLabel.startAnimation(fabAppear)
        }

        fabMenuOpened = !fabMenuOpened
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            navigate(ListOfDecksFragmentDirections.actionListOfDecksFragmentToEditDeckFragment(Long.MIN_VALUE, false))
            true
        }
        R.id.action_import -> {
            navigate(ListOfDecksFragmentDirections.actionListOfDecksFragmentToImportFragment())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
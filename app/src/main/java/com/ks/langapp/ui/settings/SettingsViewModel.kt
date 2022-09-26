package com.ks.langapp.ui.settings

import androidx.lifecycle.ViewModel
import com.ks.langapp.data.repository.LangRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: LangRepository
) : ViewModel() {

}
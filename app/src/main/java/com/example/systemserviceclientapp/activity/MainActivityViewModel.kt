package com.example.systemserviceclientapp.activity

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private var _currentTextState = mutableStateOf<String>("None")
    var currentTextState : State<String> = _currentTextState

    fun onRandomNumberChanged(randomNumberText : String) {
        _currentTextState.value = randomNumberText
    }

}
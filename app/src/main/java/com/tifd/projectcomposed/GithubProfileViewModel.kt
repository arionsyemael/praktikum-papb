package com.tifd.projectcomposed

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GithubProfileViewModel: ViewModel() {
    private val _githubProfileState = MutableStateFlow<GithubProfile?>(null)
    val githubProfileState: StateFlow<GithubProfile?> = _githubProfileState

    fun fetchGithubProfile(username: String) {
        viewModelScope.launch {
            try {
                Log.d("GithubProfileViewModel", "Fetching profile for username: $username")
                val profile = RetrofitInstance.api.getGithubProfile(username)
                _githubProfileState.value = profile
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }
}
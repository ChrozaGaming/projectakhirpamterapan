package com.example.projectakhirpamterapan.ui.auth

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectakhirpamterapan.data.AuthRepository
import com.example.projectakhirpamterapan.data.remote.ApiConfig
import com.example.projectakhirpamterapan.model.User
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val token: String? = null,
    val user: User? = null,
    val errorMessage: String? = null,
    val registerSuccess: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiConfig.getApiService()
    private val repository = AuthRepository(apiService)

    var loginState: LoginUiState by mutableStateOf(LoginUiState())
        private set

    fun clearError() {
        loginState = loginState.copy(errorMessage = null)
    }

    fun clearRegisterFlag() {
        loginState = loginState.copy(registerSuccess = false)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginState = loginState.copy(isLoading = true, errorMessage = null)

            val result = repository.login(email, password)
            result
                .onSuccess { resp ->
                    loginState = loginState.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        token = resp.token,
                        user = resp.user,
                        errorMessage = null
                    )
                }
                .onFailure { e ->
                    loginState = loginState.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        token = null,
                        user = null,
                        errorMessage = e.message ?: "Login gagal"
                    )
                }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            loginState = loginState.copy(
                isLoading = true,
                errorMessage = null,
                registerSuccess = false
            )

            val result = repository.register(name, email, password)
            result
                .onSuccess {
                    loginState = loginState.copy(
                        isLoading = false,
                        errorMessage = null,
                        registerSuccess = true
                    )
                }
                .onFailure { e ->
                    loginState = loginState.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Registrasi gagal",
                        registerSuccess = false
                    )
                }
        }
    }
}

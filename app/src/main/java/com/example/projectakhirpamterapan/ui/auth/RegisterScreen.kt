package com.example.projectakhirpamterapan.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RegisterScreen(
    vm: AuthViewModel,
    goToLogin: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val uiState = vm.loginState

    val (name, setName) = remember { mutableStateOf("") }
    val (email, setEmail) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val (confirmPassword, setConfirmPassword) = remember { mutableStateOf("") }
    val (localError, setLocalError) = remember { mutableStateOf<String?>(null) }

    // kalau registerSuccess true → balik ke login
    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            vm.clearRegisterFlag()
            goToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = androidx.compose.ui.res.painterResource(id = com.example.projectakhirpamterapan.R.drawable.logo_eventaura),
                contentDescription = "Login Illustration",
                modifier = Modifier
                    .size(200.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "REGISTER",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF1E40AF)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Buat akun baru untuk mengelola atau ikut event kampus.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)){
                        Text(
                            text = "Nama Lengkap",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Start),
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                setName(it)
                                setLocalError(null)
                                vm.clearError()
                            },
                            placeholder = { Text("Nama lengkap", style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic), color = Color(0xFFC2C2C2)) },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E40AF),
                                unfocusedBorderColor = Color.Gray
                            ),
                            label = null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)){
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Start),
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                setEmail(it)
                                setLocalError(null)
                                vm.clearError()
                            },
                            placeholder = { Text("Email", style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic), color = Color(0xFFC2C2C2)) },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E40AF),
                                unfocusedBorderColor = Color.Gray
                            ),
                            label = null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }


                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)){
                        Text(
                            text = "Password",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Start),
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                setPassword(it)
                                setLocalError(null)
                                vm.clearError()
                            },
                            placeholder = { Text("Password", style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic), color = Color(0xFFC2C2C2)) },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E40AF),
                                unfocusedBorderColor = Color.Gray
                            ),
                            label = null,
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)){
                        Text(
                            text = " Konfirmasi Password",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Start),
                        )
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                setConfirmPassword(it)
                                setLocalError(null)
                                vm.clearError()
                            },
                            placeholder = { Text("Konfirmasi password", style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic), color = Color(0xFFC2C2C2)) },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E40AF),
                                unfocusedBorderColor = Color.Gray
                            ),
                            label = null,
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }


                    val errorText = localError ?: uiState.errorMessage
                    if (errorText != null) {
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            when {
                                name.isBlank() || email.isBlank() ||
                                        password.isBlank() || confirmPassword.isBlank() ->
                                    setLocalError("Semua kolom wajib diisi.")

                                password != confirmPassword ->
                                    setLocalError("Password dan konfirmasi tidak sama.")

                                else -> {
                                    setLocalError(null)
                                    vm.register(
                                        name = name.trim(),
                                        email = email.trim(),
                                        password = password
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) Color(0xFFC2C2C2) else Color(0xFF1E40AF),
                            contentColor = Color.White),
                    ) {
                        Text("Daftar")
                    }

                    TextButton(
                        onClick = goToLogin,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Sudah punya akun? Masuk",
                            color = Color(0xFF1E40AF)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    // AuthViewModel membutuhkan parameter 'application' — berikan instance Application untuk preview.
    RegisterScreen(vm = AuthViewModel(android.app.Application()), goToLogin = {})
}
package com.vasundhara.app.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vasundhara.app.ui.components.*
import com.vasundhara.app.ui.theme.*

@Composable
fun AuthScreen(onAuthenticated: () -> Unit, onNewUser: () -> Unit, vm: AuthViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.isAuthenticated) { if (state.isAuthenticated) { if (state.isNewUser) onNewUser() else onAuthenticated() } }

    VasundharaBackground {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(64.dp))
            // Logo
            Box(Modifier.size(80.dp).clip(RoundedCornerShape(24.dp)).background(Brush.radialGradient(listOf(EcoGreen, EcoGreenDark))), Alignment.Center) {
                Icon(Icons.Filled.Park, null, tint = PureBlack, modifier = Modifier.size(44.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("Vasundhara", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black))
            Text("Environmental Intelligence", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            Spacer(Modifier.height(40.dp))

            // Toggle
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.surface).padding(4.dp)) {
                listOf("Login", "Register").forEachIndexed { i, label ->
                    val sel = (i == 0) == state.isLoginMode
                    Box(Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (sel) EcoGreen else MaterialTheme.colorScheme.surface).clickable { if (!sel) vm.toggleMode() }.padding(vertical = 12.dp), Alignment.Center) {
                        Text(label, style = MaterialTheme.typography.labelLarge.copy(color = if (sel) PureBlack else MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                }
            }
            Spacer(Modifier.height(28.dp))

            // Form
            var name by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var pwVisible by remember { mutableStateOf(false) }

            AnimatedVisibility(!state.isLoginMode) {
                Column {
                    VasundharaTextField(name, { name = it }, "Full Name", Icons.Filled.Person)
                    Spacer(Modifier.height(14.dp))
                }
            }
            VasundharaTextField(email, { email = it }, "Email", Icons.Filled.Email, keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(14.dp))
            VasundharaTextField(password, { password = it }, "Password", Icons.Filled.Lock, keyboardType = KeyboardType.Password,
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton({ pwVisible = !pwVisible }) { Icon(if (pwVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) } }
            )
            Spacer(Modifier.height(8.dp))

            // Error
            AnimatedVisibility(state.error != null) {
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(ErrorRed.copy(alpha = 0.12f)).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ErrorOutline, null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                    Text(state.error ?: "", style = MaterialTheme.typography.bodySmall.copy(color = ErrorRed))
                }
            }
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { if (state.isLoginMode) vm.login(email, password) else vm.register(name, email, password) },
                modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreen, contentColor = PureBlack),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = PureBlack, strokeWidth = 2.dp)
                else Text(if (state.isLoginMode) "Login" else "Create Account", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
                Text("  or  ", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
            }
            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { vm.googleSignIn() }, modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Icon(Icons.Filled.AccountCircle, null, tint = InfoBlue, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Continue with Google", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

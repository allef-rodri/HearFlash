package br.com.hearflash.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.hearflash.AuthViewModel

fun launchedEffect(value: AuthViewModel.AuthState?, function: () -> Unit) {

}

@Composable
fun Home(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthViewModel.AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Home", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("dashboard") }) {
            Text(text = "Dashboard")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("carbonCalculator") }) {
            Text(text = "Pegada de CO2")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { authViewModel.logout() }) {
            Text(text = "Sair")
        }
    }
}
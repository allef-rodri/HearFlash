package br.com.hearflash.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.hearflash.AuthViewModel
import br.com.hearflash.R

@Composable
fun Home(navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthViewModel.AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF09203F), Color(0xFF537895), Color(0xFF84A9C0))
    )

    val cardGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1E2A38), Color(0xFF283D52), Color(0xFF345B73))
    )

    Box(
        modifier = Modifier.fillMaxSize().background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .background(cardGradient, shape = RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Bem-vindo!",
                        fontSize = 28.sp,
                        color = Color(0xFF00E5FF),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("dashboard") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFFF))
                    ) {
                        Text(text = "Dashboard", color = Color.White, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { navController.navigate("carbonCalculator") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFFF))
                    ) {
                        Text(text = "Pegada de CO2", color = Color.White, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { authViewModel.logout() }) {
                        Text(text = "Sair", color = Color(0xFF00E5FF))
                    }
                }
            }
        }
    }
}

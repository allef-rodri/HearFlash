package br.com.hearflash.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.hearflash.AuthViewModel
import br.com.hearflash.ui.theme.BackgroundColor
import br.com.hearflash.ui.theme.PrimaryColor
import br.com.hearflash.ui.theme.SecondaryColor
import br.com.hearflash.ui.theme.SurfaceColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    pegadaCarbono: Double,
    meta: Double
) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        if (authState.value is AuthViewModel.AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    Scaffold(containerColor = BackgroundColor, topBar = {
        TopAppBar(
            title = { Text("Dashboard", color = PrimaryColor) }, navigationIcon = {
            IconButton(onClick = { navController.navigate("home") }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar para Home",
                    tint = PrimaryColor
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(BackgroundColor)
        )
    }, content = { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(SurfaceColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sua Pegada de Carbono",
                        style = MaterialTheme.typography.headlineSmall,
                        color = PrimaryColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${pegadaCarbono} kg CO₂", fontSize = 24.sp, color = SecondaryColor)
                    Text(text = "Meta: ${meta} kg CO₂", fontSize = 16.sp, color = SecondaryColor)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(SurfaceColor)
            ) {
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Gráfico de Evolução (a implementar)", color = SecondaryColor)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(SurfaceColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Detalhamento",
                        style = MaterialTheme.typography.headlineSmall,
                        color = PrimaryColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Transporte: 40%", color = SecondaryColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "Energia: 35%", color = SecondaryColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "Carne: 25%", color = SecondaryColor)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(SurfaceColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Recomendações",
                        style = MaterialTheme.typography.headlineSmall,
                        color = PrimaryColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("• Considere usar transporte público.", color = SecondaryColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "• Reduza o consumo de energia em horários de pico.",
                            color = SecondaryColor
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("• Opte por produtos sustentáveis.", color = SecondaryColor)
                    }
                }
            }
        }
    })
}

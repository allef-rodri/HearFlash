package br.com.hearflash.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.hearflash.AuthViewModel
import br.com.hearflash.ui.theme.BackgroundColor
import br.com.hearflash.ui.theme.PrimaryColor
import br.com.hearflash.ui.theme.TextPrimaryColor
import br.com.hearflash.ui.theme.TextSecondaryColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarbonFootprintCalculator(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthViewModel.AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    Scaffold(containerColor = BackgroundColor, topBar = {
        TopAppBar(title = { Text("Pegada de Carbono", color = PrimaryColor) }, navigationIcon = {
            IconButton(onClick = { navController.navigate("home") }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar para Home",
                    tint = PrimaryColor
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(BackgroundColor)
        )
    }, content = {

        var kmDriven by remember { mutableStateOf("") }
        var energyConsumption by remember { mutableStateOf("") }
        var meatConsumption by remember { mutableStateOf("") }
        var totalCarbon by remember { mutableStateOf(0f) }

        val db = FirebaseFirestore.getInstance()

        // Função para salvar a pegada de carbono no Firebase
        fun saveCarbonFootprintToFirebase(
            kmDriven: Float,
            energyConsumption: Float,
            meatConsumption: Float,
            totalCarbon: Float
        ) {
            val user = FirebaseAuth.getInstance().currentUser
            val carbonData = hashMapOf(
                "userId" to (user?.uid ?: ""),
                "km_driven" to kmDriven,
                "energy_consumption" to energyConsumption,
                "meat_consumption" to meatConsumption,
                "total_carbon" to totalCarbon,
                "timestamp" to System.currentTimeMillis()  // Adicionando um timestamp
            )
            db.collection("carbon_footprints").add(carbonData)
                .addOnSuccessListener { documentReference ->
                    println("Pegada de carbono salva com ID: ${documentReference.id}")
                }.addOnFailureListener { e ->
                    println("Erro ao salvar pegada de carbono: $e")
                    Log.e("FirestoreError", "Erro ao salvar pegada de carbono: ", e)
                }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Calculadora de Pegada de Carbono", color = PrimaryColor)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = kmDriven,
                onValueChange = { kmDriven = it },
                label = { Text(text = "Quantidade de Km por dia", color = TextSecondaryColor) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = TextPrimaryColor),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor, unfocusedBorderColor = TextSecondaryColor
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = energyConsumption,
                onValueChange = { energyConsumption = it },
                label = {
                    Text(
                        text = "Consumo de energia (kWh por mês)", color = TextSecondaryColor
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = TextPrimaryColor),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor, unfocusedBorderColor = TextSecondaryColor
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = meatConsumption,
                onValueChange = { meatConsumption = it },
                label = {
                    Text(
                        text = "Consumo de carne (kg por semana)", color = TextSecondaryColor
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = TextPrimaryColor),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor, unfocusedBorderColor = TextSecondaryColor
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val kmDrivenFloat = kmDriven.toFloatOrNull() ?: 0f
                    val energyFloat = energyConsumption.toFloatOrNull() ?: 0f
                    val meatFloat = meatConsumption.toFloatOrNull() ?: 0f

                    // Cálculo da pegada de carbono
                    val carbonFromCar = kmDrivenFloat * 0.24f
                    val carbonFromEnergy = energyFloat * 0.5f
                    val carbonFromMeat = meatFloat * 2.5f

                    totalCarbon = carbonFromCar + carbonFromEnergy + carbonFromMeat

                    saveCarbonFootprintToFirebase(
                        carbonFromCar, carbonFromEnergy, carbonFromMeat, totalCarbon
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text(
                    text = "Salvar Pegada de Carbono",
                    color = TextSecondaryColor,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Pegada de Carbono Total: $totalCarbon kg CO₂", color = TextSecondaryColor)
        }
    })
}

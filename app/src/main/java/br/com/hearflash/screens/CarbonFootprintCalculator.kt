package br.com.hearflash.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import br.com.hearflash.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CarbonFootprintCalculator(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthViewModel.AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    var kmDriven by remember { mutableStateOf("") }
    var energyConsumption by remember { mutableStateOf("") }
    var meatConsumption by remember { mutableStateOf("") }
    var totalCarbon by remember { mutableStateOf(0f) }

    val db = FirebaseFirestore.getInstance()

    // Função para salvar a pegada de carbono no Firebase
    fun saveCarbonFootprintToFirebase(kmDriven: Float, energyConsumption: Float, meatConsumption: Float, totalCarbon: Float) {
        val carbonData = hashMapOf(
            "km_driven" to kmDriven,
            "energy_consumption" to energyConsumption,
            "meat_consumption" to meatConsumption,
            "total_carbon" to totalCarbon,
            "timestamp" to System.currentTimeMillis()  // Adicionando um timestamp
        )
        val user = FirebaseAuth.getInstance().currentUser
        db.collection("carbon_footprints")
            .add(carbonData)
            .addOnSuccessListener { documentReference ->
                println("Pegada de carbono salva com ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Erro ao salvar pegada de carbono: $e")
                Log.e("FirestoreError", "Erro ao salvar pegada de carbono: ", e)
            }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Calculadora de Pegada de Carbono")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = kmDriven,
            onValueChange = { kmDriven = it },
            label = { Text("Quilômetros dirigidos por dia") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = energyConsumption,
            onValueChange = { energyConsumption = it },
            label = { Text("Consumo de energia (kWh por mês)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = meatConsumption,
            onValueChange = { meatConsumption = it },
            label = { Text("Consumo de carne (kg por semana)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val kmDrivenFloat = kmDriven.toFloatOrNull() ?: 0f
            val energyFloat = energyConsumption.toFloatOrNull() ?: 0f
            val meatFloat = meatConsumption.toFloatOrNull() ?: 0f

            // Cálculo da pegada de carbono
            val carbonFromCar = kmDrivenFloat * 0.24f
            val carbonFromEnergy = energyFloat * 0.5f
            val carbonFromMeat = meatFloat * 2.5f

            totalCarbon = carbonFromCar + carbonFromEnergy + carbonFromMeat

            // Salvar no Firestore
            saveCarbonFootprintToFirebase(kmDrivenFloat, energyFloat, meatFloat, totalCarbon)
        }) {
            Text("Calcular e Salvar Pegada de Carbono")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Pegada de Carbono Total: $totalCarbon kg CO₂")
    }
}

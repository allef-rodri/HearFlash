package br.com.hearflash.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.hearflash.AuthViewModel
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
        if (authState.value is AuthViewModel.AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    var kmDriven by remember { mutableStateOf("") }
    var energyConsumption by remember { mutableStateOf("") }
    var meatConsumption by remember { mutableStateOf("") }
    var totalCarbon by remember { mutableStateOf(0f) }

    val db = FirebaseFirestore.getInstance()

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
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("carbon_footprints").add(carbonData)
            .addOnSuccessListener {
                Log.d("Firestore", "Pegada de carbono salva com sucesso!")
            }.addOnFailureListener { e ->
                Log.e("FirestoreError", "Erro ao salvar pegada de carbono: ", e)
            }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF09203F), Color(0xFF537895), Color(0xFF84A9C0))
    )

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        topBar = {
            TopAppBar(
                title = { Text("Pegada de Carbono", color = Color(0xFF00E5FF)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar para Home",
                            tint = Color(0xFF00E5FF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1E2A38),
                                    Color(0xFF283D52),
                                    Color(0xFF345B73)
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Calculadora",
                            fontSize = 22.sp,
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Bold

                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = kmDriven,
                            onValueChange = { kmDriven = it },
                            label = { Text("Quantidade de Km por dia", color = Color.White) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00E5FF),
                                unfocusedBorderColor = Color.LightGray
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = energyConsumption,
                            onValueChange = { energyConsumption = it },
                            label = { Text("Consumo de energia (kWh/mês)", color = Color.White) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00E5FF),
                                unfocusedBorderColor = Color.LightGray
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = meatConsumption,
                            onValueChange = { meatConsumption = it },
                            label = { Text("Consumo de carne (kg/semana)", color = Color.White) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00E5FF),
                                unfocusedBorderColor = Color.LightGray
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                val kmDrivenFloat = kmDriven.toFloatOrNull() ?: 0f
                                val energyFloat = energyConsumption.toFloatOrNull() ?: 0f
                                val meatFloat = meatConsumption.toFloatOrNull() ?: 0f

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
                                .height(60.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF00BFFF))
                        ) {
                            Text(
                                text = "Salvar Pegada de Carbono",
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Pegada de Carbono Total: ${String.format("%.2f", totalCarbon)} kg CO₂",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}


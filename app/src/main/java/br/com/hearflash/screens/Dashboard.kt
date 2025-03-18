package br.com.hearflash.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.hearflash.AuthViewModel
import br.com.hearflash.ui.theme.BackgroundColor
import br.com.hearflash.ui.theme.CarneColor
import br.com.hearflash.ui.theme.EnergiaColor
import br.com.hearflash.ui.theme.PrimaryColor
import br.com.hearflash.ui.theme.SecondaryColor
import br.com.hearflash.ui.theme.SurfaceColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class CarbonFootprintEntry(
    val userId: String = "",
    val km_driven: Float = 0f,
    val energy_consumption: Float = 0f,
    val meat_consumption: Float = 0f,
    val total_carbon: Float = 0f,
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel
) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        if (authState.value is AuthViewModel.AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    var carbonList by remember { mutableStateOf(listOf<CarbonFootprintEntry>()) }
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val pegadaCarbono = carbonList.sumOf { it.total_carbon.toDouble() }
    val transportePegada = carbonList.sumOf { it.km_driven.toDouble() }
    val energiaPegada = carbonList.sumOf { it.energy_consumption.toDouble() }
    val carnePegada = carbonList.sumOf { it.meat_consumption.toDouble() }

    val percTransporte = if (pegadaCarbono != 0.0) (transportePegada / pegadaCarbono) * 100 else 0.0
    val percEnergia = if (pegadaCarbono != 0.0) (energiaPegada / pegadaCarbono) * 100 else 0.0
    val percCarne = if (pegadaCarbono != 0.0) (carnePegada / pegadaCarbono) * 100 else 0.0

    String.format("%.2f", percTransporte)
    String.format("%.2f", percEnergia)
    String.format("%.2f", percCarne)

    var pegadaFormatada = String.format("%.2f", pegadaCarbono)


    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            firestore.collection("carbon_footprints").whereEqualTo("userId", user.uid).get()
                .addOnSuccessListener { querySnapshot ->
                    val list = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(CarbonFootprintEntry::class.java)
                    }
                    carbonList = list
                }.addOnFailureListener { exception ->
                    Log.e("Dashboard", "Erro ao buscar dados: ", exception)
                }
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF09203F), // Cor inicial
            Color(0xFF537895), // Cor intermediária
            Color(0xFF84A9C0)  // Cor final
        )
    )


    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", color = Color(0xFF00E5FF)) },
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
        },
        content = { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
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
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sua Pegada de Carbono",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$pegadaFormatada kg CO₂",
                            fontSize = 24.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Meta: 100 kg CO₂",
                            fontSize = 16.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }


            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
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
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Detalhamento",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            PieChartWithLegend(
                                transporte = transportePegada,
                                energia = energiaPegada,
                                carne = carnePegada
                            )
                        }
                    }
                }


                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
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
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Recomendações",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text("• Considere usar transporte público.", color = Color.White)
                                Text("• Reduza o consumo de energia em horários de pico.", color = Color.White)
                                Text("• Opte por produtos sustentáveis.", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    })
}

@Composable
fun PieChartComponent(
    transporte: Double, energia: Double, carne: Double
) {
    val total = transporte + energia + carne

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        if (total == 0.0) return@Canvas

        val diameter = minOf(size.width, size.height)
        val topLeft = Offset(
            (size.width - diameter) / 2, (size.height - diameter) / 2
        )

        var currentStartAngle = -90f

        val transporteAngle = ((transporte / total) * 360f).toFloat()
        val energiaAngle = ((energia / total) * 360f).toFloat()
        val carneAngle = ((carne / total) * 360f).toFloat()

        drawArc(
            color = PrimaryColor,
            startAngle = currentStartAngle,
            sweepAngle = transporteAngle,
            useCenter = true,
            topLeft = topLeft,
            size = Size(diameter, diameter)
        )

        val midAngleTransporte = currentStartAngle + transporteAngle / 2
        currentStartAngle += transporteAngle

        drawArc(
            color = EnergiaColor,
            startAngle = currentStartAngle,
            sweepAngle = energiaAngle,
            useCenter = true,
            topLeft = topLeft,
            size = Size(diameter, diameter)
        )

        val midAngleEnergia = currentStartAngle + energiaAngle / 2
        currentStartAngle += energiaAngle

        drawArc(
            color = CarneColor,
            startAngle = currentStartAngle,
            sweepAngle = carneAngle,
            useCenter = true,
            topLeft = topLeft,
            size = Size(diameter, diameter)
        )

        val midAngleCarne = currentStartAngle + carneAngle / 2
        currentStartAngle += carneAngle

        drawIntoCanvas { canvas ->
            val paint = Paint().asFrameworkPaint().apply {
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 40f
                color = android.graphics.Color.WHITE
            }


            fun drawPercentage(midAngle: Float, percentage: Double) {
                val radius = diameter / 3
                val radian = Math.toRadians(midAngle.toDouble())

                val centerX = topLeft.x + diameter / 2
                val centerY = topLeft.y + diameter / 2

                val x = centerX + radius * Math.cos(radian)
                val y = centerY + radius * Math.sin(radian)

                canvas.nativeCanvas.drawText(
                    String.format("%.0f%%", percentage), x.toFloat(), y.toFloat(), paint
                )
            }

            val percTransporte = if (total != 0.0) (transporte / total) * 100 else 0.0
            val percEnergia = if (total != 0.0) (energia / total) * 100 else 0.0
            val percCarne = if (total != 0.0) (carne / total) * 100 else 0.0

            drawPercentage(midAngleTransporte, percTransporte)
            drawPercentage(midAngleEnergia, percEnergia)
            drawPercentage(midAngleCarne, percCarne)
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 14.sp, color = SecondaryColor)
    }
}

@Composable
fun PieChartWithLegend(
    transporte: Double, energia: Double, carne: Double
) {
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PieChartComponent(
            transporte = transporte, energia = energia, carne = carne
        )
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = PrimaryColor, label = "Transporte")
            LegendItem(color = EnergiaColor, label = "Energia")
            LegendItem(color = CarneColor, label = "Carne")
        }
    }
}

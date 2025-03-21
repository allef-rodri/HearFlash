package br.com.hearflash

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.hearflash.screens.CarbonFootprintCalculator
import br.com.hearflash.screens.Dashboard
import br.com.hearflash.screens.Home
import br.com.hearflash.screens.Login
import br.com.hearflash.screens.SignUp

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") {
            Login(
                modifier,
                navController,
                authViewModel
            )
        }
        composable("signup") {
            SignUp(
                modifier,
                navController,
                authViewModel
            )
        }
        composable("home") {
            Home(
                navController,
                authViewModel
            )
        }

        composable("carbonCalculator") {
            CarbonFootprintCalculator(
                modifier,
                navController,
                authViewModel
            )
        }
        composable("dashboard") {
            Dashboard(
                modifier,
                navController,
                authViewModel
            )
        }
    })
}
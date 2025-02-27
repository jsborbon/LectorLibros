package com.jsborbon.lectordelibros


import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jsborbon.lectordelibros.screens.PantallaLibrosAlta
import com.jsborbon.lectordelibros.screens.PantallaLibrosListado
import com.jsborbon.lectordelibros.screens.PantallaLibrosModificar
import com.jsborbon.lectordelibros.screens.PantallaLibrosVer


@Composable
fun NavigationWrapper (navHostController: NavHostController) {

    NavHost(navController = navHostController, startDestination = "pantallaLibrosListado") {

        composable ("pantallaLibrosAlta") {PantallaLibrosAlta(navHostController)}

        composable("pantallaLibrosModificar/{isbn}") { query ->
            val isbn = query.arguments?.getString("isbn")!!
            PantallaLibrosModificar(navHostController, isbn)
        }

        composable("pantallaLibrosVer/{isbn}") { query ->
            val isbn = query.arguments?.getString("isbn")!!
            PantallaLibrosVer(navHostController, isbn)
        }
       composable ("pantallaLibrosListado") {PantallaLibrosListado(navHostController)}

    }
}


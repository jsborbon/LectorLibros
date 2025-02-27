package com.jsborbon.lectordelibros.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PantallaLibrosVer(navController: NavController, isbn: String) {
    var url by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var mensajeConfirmacion by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()
    val librosRef = db.collection("libros")

    LaunchedEffect(isbn) {
        librosRef.document(isbn).get()
            .addOnSuccessListener { document ->
                loading = false
                if (document.exists()) {
                    val urlRecibida = document.getString("url")
                    Log.d("PantallaLibrosVer", "URL obtenida: $urlRecibida")
                    if (!urlRecibida.isNullOrEmpty()) {
                        url = urlRecibida
                    } else {
                        mensajeConfirmacion = "URL no válida o vacía"
                    }
                } else {
                    mensajeConfirmacion = "Libro no encontrado"
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PantallaLibrosVer", "Error al cargar los datos", exception)
                mensajeConfirmacion = "Error al cargar los datos"
                loading = false
            }
    }

    if (loading) {
        CircularProgressIndicator()
    } else if (url.isNotEmpty()) {
        Log.d("PantallaLibrosVer", "Abriendo el PDF: $url")
        val context = LocalContext.current
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setDataAndType(Uri.parse(url), "application/pdf")
        }
        context.startActivity(intent)
    } else {
        Text(text = mensajeConfirmacion, color = Color.Red, textAlign = TextAlign.Center)
    }
}
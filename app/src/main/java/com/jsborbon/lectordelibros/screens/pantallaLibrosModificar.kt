package com.jsborbon.lectordelibros.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PantallaLibrosModificar(navController: NavController, isbn: String) {

    var nombre by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var picture by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var mensajeConfirmacion by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var dataLoaded by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()
    val librosRef = db.collection("libros")


    if (!dataLoaded) {
        loading = true
       librosRef.document(isbn).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("Libro", "DocumentSnapshot data: ${document.data}")

                    nombre = document.getString("nombre") ?: ""
                    genero = document.getString("genero") ?: ""
                    autor = document.getString("autor") ?: ""
                    picture = document.getString("picture") ?: ""
                    url = document.getString("url") ?: ""
                } else {
                    mensajeConfirmacion = "Libro no encontrado"
                }
                dataLoaded = true
                loading = false
            }
            .addOnFailureListener {
                mensajeConfirmacion = "Error al cargar los datos"
                loading = false
            }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp, start = 10.dp, end = 10.dp)
    ) {

        Text(
            text = "Modificar libro",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.size(20.dp))

        OutlinedTextField(
            value = isbn,
            onValueChange = { /* ISBN should be read-only in modification */ },
            label = { Text("ISBN") },
            enabled = false, // make it read-only
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(8.dp))

        OutlinedTextField(
            value = genero,
            onValueChange = { genero = it },
            label = { Text("Género") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(8.dp))

        OutlinedTextField(
            value = autor,
            onValueChange = { autor = it },
            label = { Text("Autor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(8.dp))

        OutlinedTextField(
            value = picture,
            onValueChange = { picture = it },
            label = { Text("Imagen URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            onClick = {
                if (isbn.isNotBlank()) {
                    loading = true
                    val dato = hashMapOf(
                        "isbn" to isbn,
                        "nombre" to nombre,
                        "genero" to genero,
                        "autor" to autor,
                        "picture" to picture,
                        "url" to url
                    )

                    librosRef.document(isbn)
                        .set(dato)
                        .addOnSuccessListener {
                            mensajeConfirmacion = "Datos Modificados Correctamente"
                            loading = false
                            navController.navigate("pantallaLibrosListado")
                        }
                        .addOnFailureListener {
                            mensajeConfirmacion = "No se ha podido modificar"
                            loading = false
                        }
                } else {
                    mensajeConfirmacion = "Por favor, ingresa un ISBN válido"
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xD04CAF50),
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text(text = "Modificar")
        }

        Spacer(modifier = Modifier.size(5.dp))
        Button(
            onClick = {
                navController.navigate("pantallaLibrosListado")


            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xD04CAF50),
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(text = "Cancelar")
        }

        Spacer(modifier = Modifier.size(5.dp))

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(40.dp))
        } else {
            Text(text = mensajeConfirmacion)
        }
    }
}
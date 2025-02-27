package com.jsborbon.lectordelibros.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
fun PantallaLibrosAlta(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    val coleccion = "libros"

    var isbn by remember { mutableStateOf("") }
    var picture by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("")  }
    var mensajeConfirmacion by remember { mutableStateOf("") }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        content = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Text(
                        text = "Guardar libro",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(top = 100.dp, bottom = 20.dp)
                    )

                    TextFieldComponent(value = isbn, onValueChange = { isbn = it }, label = "ISBN")
                    TextFieldComponent(value = picture, onValueChange = { picture = it }, label = "URL de imagen")
                    TextFieldComponent(value = nombre, onValueChange = { nombre = it }, label = "Título del libro")
                    TextFieldComponent(value = genero, onValueChange = { genero = it }, label = "Género")
                    TextFieldComponent(value = autor, onValueChange = { autor = it }, label = "Autor")
                    TextFieldComponent(value = url , onValueChange = {url = it}, label = "URL")

                    Spacer(modifier = Modifier.size(16.dp))

                    val dato = hashMapOf(
                        "picture" to picture,
                        "isbn" to isbn,
                        "nombre" to nombre,
                        "genero" to genero,
                        "autor" to autor,
                        "url" to url
                    )

                    Button(
                        onClick = {
                            mensajeConfirmacion = ""
                            if (isbn.isBlank() || nombre.isBlank() || genero.isBlank() || autor.isBlank()) {
                                mensajeConfirmacion = "Por favor, completa todos los campos."
                            } else {
                                db.collection(coleccion)
                                    .document(isbn)
                                    .set(dato)
                                    .addOnSuccessListener {
                                        mensajeConfirmacion = "Datos guardados correctamente"
                                        isbn = ""
                                        picture = ""
                                        nombre = ""
                                        genero = ""
                                        autor = ""
                                        url = ""

                                        // Navega a la pantalla de informes
                                        navController.navigate("pantallaLibrosListado") {
                                            popUpTo("pantallaLibrosAlta") { inclusive = true }
                                        }
                                    }
                                    .addOnFailureListener {
                                        mensajeConfirmacion = "No se ha podido guardar"
                                    }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xD04CAF50),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.Black),
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(text = "Guardar")
                    }
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
                    Text(text = mensajeConfirmacion, color = Color.Red)
                }
            }
        }
    )
}

@Composable
fun TextFieldComponent(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .width(280.dp)
            .padding(vertical = 4.dp)
    )
}
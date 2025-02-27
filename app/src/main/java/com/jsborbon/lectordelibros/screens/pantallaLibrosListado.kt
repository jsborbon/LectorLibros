package com.jsborbon.lectordelibros.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun getLibros(): List<Libro> {
    val db = FirebaseFirestore.getInstance()
    val librosRef = db.collection("libros")
    return try {
        val querySnapshot = librosRef.get().await()
        querySnapshot.documents.mapNotNull { it.toObject(Libro::class.java) }
    } catch (e: Exception) {
        Log.e("ErrorFirestore", "Exception fetching data: ${e.message}")
        emptyList()
    }
}

fun deleteLibro(libroIsbn: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("libros").document(libroIsbn)
        .delete()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure("No se ha podido eliminar el libro") }
}

@Composable
fun PantallaLibrosListado(navController: NavHostController) {
    var listaLibros by remember { mutableStateOf(emptyList<Libro>()) }
    var loading by remember { mutableStateOf(true) }
    var mensajeBorrado by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            listaLibros = getLibros()
        } catch (e: Exception) {
            Log.e("Error en la Base de Datos", "No se han podido obtener los libros debido a : ${e.message}")
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("pantallaLibrosAlta") }) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                    Icon(Icons.Filled.Add, contentDescription = "Agrega un libro")
                    Text("Agrega un libro", modifier = Modifier.padding(start = 4.dp))
                }
            }
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else if (listaLibros.isEmpty()) {
                    Text("No hay libros disponibles para leer. Agrega uno para comenzar...", Modifier.padding(16.dp))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        items(listaLibros) { libro ->
                            LibroItem(
                                libro = libro,
                                onClick = { navController.navigate("pantallaLibrosVer/${libro.isbn}") },
                                onEdit = { navController.navigate("pantallaLibrosModificar/${libro.isbn}") },
                                onDelete = {
                                    deleteLibro(
                                        libroIsbn = libro.isbn,
                                        onSuccess = {
                                            mensajeBorrado = "Libro eliminado correctamente"
                                            listaLibros = listaLibros.filter { it.isbn != libro.isbn }
                                        },
                                        onFailure = { mensajeBorrado = it }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibroItem(libro: Libro, onClick: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showIcons by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showIcons = !showIcons }
            ),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            if (libro.picture.isNotEmpty()) {
                AsyncImage(
                    model = libro.picture,
                    contentDescription = libro.nombre,
                    placeholder = rememberAsyncImagePainter(model = "url_de_la_imagen"),
                    modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
                )
            }
            Text(text = libro.nombre, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
            Text(text = libro.genero, fontWeight = FontWeight.Bold)
            Text(text = libro.autor)
            Text("ISBN: ${libro.isbn}")

            if (showIcons) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Editar Libro")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar Libro")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text(text = "Mis Libros", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 16.dp)) }
    )
}
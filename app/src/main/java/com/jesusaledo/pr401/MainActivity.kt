package com.jesusaledo.pr401

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jesusaledo.pr401.ui.theme.PR401Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PR401Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Cuaderno()
                }
            }
        }
    }
}

@Composable
fun Cuaderno() {
    var capacidadCuaderno by rememberSaveable { mutableIntStateOf(0) }
    var notas by rememberSaveable { mutableStateOf(DoubleArray(capacidadCuaderno)) }
    var nuevaNota by remember { mutableStateOf("") }
    var showSizeDialog by remember { mutableStateOf(true) }
    val context = LocalContext.current
    var showMaxDialog by remember { mutableStateOf(false) }
    var showMediaDialog by remember { mutableStateOf(false) }
    var showBorrarPorPosicionDialog by remember { mutableStateOf(false) }
    var showBorrarTodasDialog by remember { mutableStateOf(false) }
    if (showSizeDialog) {
        DialogArrSize { size ->
            capacidadCuaderno = size
            notas = DoubleArray(capacidadCuaderno)
            showSizeDialog = false
        }
    }
    else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nuevaNota,
                onValueChange = { nuevaNota = it },
                label = { Text("Introduce una nota") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Button(onClick = {
                val nota = nuevaNota.toDoubleOrNull()
                if (nota != null && nota in 1.0..10.0) {
                    val index = notas.indexOfFirst { it == 0.0 }
                    if (index != -1) {
                        notas[index] = nota
                        nuevaNota = ""
                    } else {
                        Toast.makeText(context, "Array lleno. Tamaño: ${notas.size}", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(context, "Introduce solo números entre 1 y 10", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Añadir Nota")
            }
            Spacer(modifier = Modifier.height(50.dp))
            Button(onClick = {
                showMaxDialog = true
            }) {
                Text("Mostrar la Nota Máxima")
            }
            if (showMaxDialog && notas.size > 0) {
                val maxAndPos = notaMax(notas)
                MostrarNotaMaxDialog(maxAndPos) {
                    showMaxDialog = false
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                showMediaDialog = true
            }) {
                Text("Mostrar la Media")
            }
            if (showMediaDialog && notas.size > 0) {
                val media = mediaMasMenos(notas)
                MostrarMediaDialog(media) {
                    showMediaDialog = false
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                showBorrarPorPosicionDialog = true
            }) {
                Text("Borrar por Posición")
            }
            if (showBorrarPorPosicionDialog && notas.isNotEmpty()) {
                BorrarPorPosicionDialog(notas.size, { posicion ->
                    notas[posicion] = 0.0
                    showBorrarPorPosicionDialog = false
                }, {
                    showBorrarPorPosicionDialog = false
                })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                showBorrarTodasDialog = true
            }) {
                Text("Borrar Todas las Notas")
            }
            if (showBorrarTodasDialog && notas.isNotEmpty()) {
                BorrarTodasDialog({
                    for (i in notas.indices)
                        notas[i] = 0.0
                    showBorrarTodasDialog = false
                }) {
                    showBorrarTodasDialog = false
                }
            }
        }
    }
}
@Composable
fun DialogArrSize(onSubmit: (Int) -> Unit) {
    var show by rememberSaveable { mutableStateOf(true) }
    var userInput by remember { mutableStateOf("") }
    val context = LocalContext.current
    if (show) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = {
                    val size = userInput.toIntOrNull()
                    if (size != null && size > 0) {
                        show = false
                        onSubmit(size)
                    }
                    else {
                        Toast.makeText(context, "Introduce solo números enteros mayores o iguales a 1", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Enviar")
                }
            },
            title = { Text(text = "Introduce el número de alumnos") },
            text = {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
        )
    }
}
fun notaMax(arr: DoubleArray):DoubleArray {
    val maxAndPos = DoubleArray(2)
    maxAndPos[0] = arr[0]; maxAndPos[1] = 0.0
    for (i in arr.indices)
        if (arr[i] > maxAndPos[0]) {
            maxAndPos[0] = arr[i]
            maxAndPos[1] = i.toDouble()
        }
    return maxAndPos
}
@Composable
fun MostrarNotaMaxDialog(maxAndPos: DoubleArray, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onClose() },
        title = { Text("Nota Máxima") },
        text = {
            Text("La nota máxima es ${maxAndPos[0]} y está en la posición ${maxAndPos[1].toInt()}.")
        },
        confirmButton = {
            Button(onClick = { onClose() }) {
                Text("Cerrar")
            }
        }
    )
}
@Composable
fun mediaMasMenos(arr:DoubleArray): Double {
    var media = 0.0
    if (arr.size < 3) {
        val context = LocalContext.current
        Toast.makeText(context, "El array tiene menos de 3 elementos, así que la media es de los que hay", Toast.LENGTH_LONG).show()
        when (arr.size) {
            1 -> media = arr[0]
            2 -> media = (arr[0] + arr[1]) / 2
        }
    }
    else {
        val arrOrdenado = bubbleSort(arr)
        val arrRecortado = DoubleArray(arr.size - 2)
        for (i in arrRecortado.indices) {
            arrRecortado[i] = arrOrdenado[i]
            media += arrRecortado[i]
        }
        media /= arrRecortado.size
    }
    return media
}
fun bubbleSort(arr: DoubleArray): DoubleArray {
    val n = arr.size
    var swapped: Boolean
    do {
        swapped = false
        for (i in 1 until n) {
            if (arr[i - 1] > arr[i]) {
                val temp = arr[i - 1]
                arr[i - 1] = arr[i]
                arr[i] = temp
                swapped = true
            }
        }
    } while (swapped)
    return arr
}
@Composable
fun MostrarMediaDialog(media: Double, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onClose() },
        confirmButton = {
            Button(onClick = { onClose() }) {
                Text("Cerrar")
            }
        },
        title = { Text("Media") },
        text = {
            Text("La media de las notas es: $media")
        }
    )
}
@Composable
fun BorrarPorPosicionDialog(maxPosicion: Int, onBorrar: (Int) -> Unit, onClose: () -> Unit) {
    var userInput by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onClose() },
        confirmButton = {
            Button(onClick = {
                val posicion = userInput.toIntOrNull()
                if (posicion != null && posicion in 0 until maxPosicion) {
                    onBorrar(posicion)
                    onClose()
                }
            }) {
                Text("Borrar")
            }
        },
        dismissButton = {
            Button(onClick = { onClose() }) {
                Text("Cancelar")
            }
        },
        title = { Text(text = "Introduce la posición a borrar") },
        text = {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    )
}
@Composable
fun BorrarTodasDialog(onBorrar: () -> Unit, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onClose() },
        confirmButton = {
            Button(onClick = {
                onBorrar()
                onClose()
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = { onClose() }) {
                Text("Cancelar")
            }
        },
        title = { Text(text = "¿Quieres borrar todas las notas?") },
        text = {
            Text("Esta acción borrará todas las notas. ¿Quieres proceder?")
        }
    )
}
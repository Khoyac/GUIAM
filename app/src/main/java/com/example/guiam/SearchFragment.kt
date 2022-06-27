package com.example.guiam

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.guiam.MainActivity.Companion.createPoli
import com.example.guiam.MainActivity.Companion.filtrarLista
import com.example.guiam.MainActivity.Companion.listaFija
import com.example.guiam.MainActivity.Companion.listaMostrar
import com.example.guiam.MainActivity.Companion.place
import com.example.guiam.MainActivity.Companion.sampleData
import com.example.guiam.models.Cities
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchFragment : Fragment() {

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {

            setContent {
                //  inflater.inflate(R.layout.fragment_search, container, false)
                val dataFileString = getJsonFromAsset(context, "valencia.json")
                val gson = Gson()
                val gridSampleType = object : TypeToken<List<Cities>>() {}.type
                sampleData = gson.fromJson(dataFileString, gridSampleType)
                sampleData.sortedBy { it.name }

                sampleData = sampleData.sortedWith(compareBy { it.name })
                //val aBuscar = "Pa"
                //sortedList = sortedList.filter { it.name.contains( aBuscar, ignoreCase = true )}

                listaFija.addAll(sampleData)


                Column(
                    modifier = Modifier.padding(top = 10.dp, bottom = 100.dp)
                ) {
                    Box {
                        TextFieldSearch(context)
                    }

                    CargarLista(context = context)

//                    LazyVerticalGrid(
//                        cells = GridCells.Fixed(1),
//                        modifier = Modifier
//                            .padding(10.dp)
//                    ) {
//                        items(sortedList.size) { index ->
//                            val city = sortedList[index];
//                            CitiesListCard(city, context)
//                        }
//                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CargarLista(context: Context) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(1),
        modifier = Modifier
            .padding(10.dp)
    ) {
        items(listaMostrar.size) { index ->
            val city = listaMostrar[index];
            CitiesListCard(city, context)
        }
    }

}

@Composable
fun CitiesListCard(data: Cities, context: Context) {
    Column() {
        CitieCard(
            //title = data.name.toByteArray().decodeToString(),
            title = String(data.name.toByteArray(Charsets.UTF_8)),
            cords = data.cords,
            context = context
        )
        Spacer(modifier = Modifier
            .height(1.dp))
    }

}

@Composable
fun CitieCard(title: String, cords: String, context: Context, modifier: Modifier = Modifier) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
        .background(Color.White)
        .clickable(
            onClick = {
                createPoli(cords)
                place = title
                Toast
                    .makeText(context, title, Toast.LENGTH_SHORT)
                    .show()
            }
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(title, style = TextStyle(color = Color.Black, fontSize = 12.sp), textAlign = TextAlign.Center)
    }
}

@Composable
fun TextFieldSearch(context: Context) {
    Column(Modifier.padding(16.dp)) {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = textState.value,
            onValueChange = {
                textState.value = it
                filtrarLista(textState.value)
            }
        )
        Text("Buscar: " + textState.value.text)
    }
}

@Composable
fun TextFieldStyleExample() {
    var nameOnCard by remember {
        mutableStateOf("Américo Vespucio")
    }

    val color = Color(0xFF120524)
    val shape = CutCornerShape(ZeroCornerSize)

    TextField(
        value = nameOnCard,
        onValueChange = { nameOnCard = it },
        label = { Text("Nombre en la tarjeta") },
        colors = TextFieldDefaults.textFieldColors(
            textColor = color,
            backgroundColor = Color.White,
            focusedLabelColor = color.copy(alpha = ContentAlpha.high),
            focusedIndicatorColor = Color.Transparent,
            cursorColor = color,
        ),
        shape = shape,
        modifier = Modifier.border(1.dp, color)
    )
}

//@Preview
//@Composable
//fun testCitiCard() {
//    CitieCard(title = "Test", cords = ".." context = cre)
//}

//Funcion que recoge los datos del JSON
fun getJsonFromAsset(context: Context, data: String): String {
    return context.assets.open(data).bufferedReader().use { it.readText() }
}
package com.example.guiam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.guiam.MainActivity.Companion.sampleData
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

                Column(
                    modifier = Modifier.padding(top = 100.dp)
                ) {
                    Box {
                        TextFieldDemo()
                    }
                    LazyVerticalGrid(
                        cells = GridCells.Fixed(1),
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        items(sampleData.size) { index ->
                            val city = sampleData.get(index);
                            citiesListCard(city, context)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun citiesListCard(data: Cities, context: Context) {
    citieCard(
        title = data.name,
        cords = data.cords,
        context = context
    )
}

@Composable
fun citieCard(title: String, cords: String, context: Context, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth()
            .clickable(
                onClick = {
                    Toast.makeText(context, "Home Item reselected", Toast.LENGTH_SHORT).show()
                    //Creamos el intent para cambiar de activity
                    //Y pasar la posicion del JSON que se ha clickado
                    Log.i("CIUDAD", "CIUDAD -------------: $title")
                }
            ),
        shape = RoundedCornerShape(15.dp),
        elevation = 5.dp
    ) {
        Box(modifier = Modifier.height(200.dp)) {
            Box(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(title, style = TextStyle(color = Color.White, fontSize = 16.sp))
            }
        }
    }
}

@Composable
fun TextFieldDemo() {
    Column(Modifier.padding(16.dp)) {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it }
        )
        Text("The textfield has this text: " + textState.value.text)
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

//Funcion que recoge los datos del JSON
fun getJsonFromAsset(context: Context, data: String): String {
    return context.assets.open(data).bufferedReader().use { it.readText() }
}
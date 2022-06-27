package com.example.guiam.component.twyperComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guiam.MainActivity
import com.example.guiam.component.twyper.flip.TwyperFlip
import com.example.guiam.utils.Generator.randomColor
import com.github.theapache64.twyper.flip.rememberTwyperFlipController

@Preview
@Composable
fun TwyperFlipPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val items = remember { mutableStateListOf(*('A'..'Z').toList().toTypedArray()) }
        val twyperFlipController = rememberTwyperFlipController()

        val generateBoxModifier: () -> Modifier = {
            Modifier
                .size(300.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            randomColor(),
                            randomColor()
                        )
                    )
                )
        }

        TwyperFlip(
            items = items,
            twyperFlipController = twyperFlipController,
            onItemRemoved = { item, direction ->
                //When swiping, the function that manages the results is called
                MainActivity.mainActivity.gestionarLista(item, direction)
                println("Item removed: $item -> $direction")
                items.remove(item)
            },
            cardModifier = generateBoxModifier,
            onEmpty = {
                //At the end of the cards, it closes
                MainActivity.mainActivity.ocultarTodo()
            },
            modifier = Modifier,
            front = { item ->
                //The content to display in front
                FrontText(item = item)
            },
            back = { item ->
                //Sample text used for the back of the card
                ReverseText(item = "${item.code - 65 + 1}")
            })

        Spacer(modifier = Modifier.height(50.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(30.dp),
        ) {

            IconButton(onClick = {
                twyperFlipController.swipeLeft()
            }) {
                Text(text = "❌", fontSize = 30.sp)
            }

            IconButton(onClick = {
                twyperFlipController.flip()
            }) {
                Text(text = "🔀", fontSize = 30.sp)
            }

            IconButton(onClick = {
                twyperFlipController.swipeRight()
            }) {
                Text(text = "✅", fontSize = 30.sp)
            }
        }
    }
}

@Composable
fun FrontText(modifier: Modifier = Modifier, item: Char){
    Text(modifier = modifier, text = "$item", fontSize = 200.sp, color = Color.White)
}

@Composable
fun ReverseText(modifier: Modifier = Modifier, item: String){
    Text(modifier = modifier, text = item, fontSize = 200.sp, color = Color.White)
}



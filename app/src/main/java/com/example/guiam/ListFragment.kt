package com.example.guiam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.guiam.MainActivity.Companion.ListPlaces
import com.example.guiam.MainActivity.Companion.place

class ListFragment : Fragment() {


    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                Column(
                    modifier = Modifier
                        .padding(
                            top = 20.dp,
                            bottom = 20.dp,
                            end = 20.dp,
                            start = 20.dp)
                ) {
                    Card (
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(15.dp),
                        elevation = 8.dp,
                        backgroundColor = Color.White,
                        shape = RoundedCornerShape(8.dp)
                            ) {
                        Box (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                ){
                            Text(text = place, textAlign = TextAlign.Center)
                        }
                        Spacer(modifier = Modifier
                            .height(1.dp))
                        LazyColumn(
                            modifier = Modifier
                                .padding(10.dp).padding(top = 30.dp)
                        ) {
                            items(ListPlaces.size) { index ->
                                val lista = ListPlaces[index];
                                Card(
                                    Modifier
                                    .fillMaxWidth()
                                    .padding(1.dp),
                                    elevation = 2.dp,
                                ) {
                                    Text(
                                        text = lista.toString(),
                                        Modifier.padding(4.dp)
                                            .fillMaxWidth()
                                    )
                                }

                            }
                        }
                    }

                }
            }
        }
    }
}
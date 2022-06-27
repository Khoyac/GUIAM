package com.example.guiam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.guiam.component.twyperComponent.TwyperFlipPreview
import com.github.theapache64.twyper.ui.theme.TwyperTheme

class SwypeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                TwyperTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        //color = MaterialTheme.colors.background
                        //color = Color.Blue
                        color = Color.Transparent
                    ) {
                        TwyperFlipPreview()
                    }
                }
            }
        }
    }
}

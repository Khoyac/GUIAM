package com.github.theapache64.twyper.flip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.guiam.component.twyper.flip.FlipCardController

/**
 * To control the TwyperFlip.
 * Used to swipe card and rotate programmatically
 */
@Composable
fun rememberTwyperFlipController(): TwyperFlipController {
    return remember { TwyperFlipControllerImpl() }
}

interface TwyperFlipController {
    /**
     * Points to the top card's [FlipCardController]
     */
    var currentCardController: FlipCardController?
    fun swipeRight()
    fun swipeLeft()
    fun flip()
}

class TwyperFlipControllerImpl : TwyperFlipController {
    override var currentCardController: FlipCardController? = null

    override fun swipeRight() {
        currentCardController?.swipeRight()
    }

    override fun swipeLeft() {
        currentCardController?.swipeLeft()
    }

    override fun flip() {
        currentCardController?.setCardFlipState()
    }
}
package com.twoonethree.painttoolbox.sealed

sealed class DrawMode {
    object brush:DrawMode()
    object line:DrawMode()
    object circle:DrawMode()
    object square:DrawMode()
    object eraser:DrawMode()
    object move:DrawMode()
    object multicircle:DrawMode()
    object multiline:DrawMode()
    object anyshape:DrawMode()
    object empty:DrawMode()
}
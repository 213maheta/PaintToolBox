package com.twoonethree.painttoolbox.model

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class DrawModel(
 var path: Path,
 val color:Color,
 val alpha:Float,
 val size:Float,
 val blendMode: BlendMode
)


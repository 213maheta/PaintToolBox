package com.twoonethree.painttoolbox.viewmodels

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import com.twoonethree.painttoolbox.model.DrawModel
import com.twoonethree.painttoolbox.sealed.DrawMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PaintModel: ViewModel() {

    private var _upperrowselection = MutableStateFlow(0)
    var upperrowselection:StateFlow<Int> = _upperrowselection

    private var _lowerrowselection = MutableStateFlow(-1)
    var lowerrowselection:StateFlow<Int> = _lowerrowselection

    private var _drawmode = MutableStateFlow<DrawMode>(DrawMode.brush)
    private var drawmode:StateFlow<DrawMode> = _drawmode

    val drawmodellist = mutableListOf<DrawModel>()
    val redodrawmodellist = mutableListOf<DrawModel>()

    var colorlist = mutableListOf<String>()

    private var _drawcounter = MutableStateFlow(-1)
    var drawcounter:StateFlow<Int> = _drawcounter

    var path = Path()
    var color = Color.Red
    var size = 20f
    var blendMode = BlendMode.Color
    var alpha = 1f
    var offsetX = 0f
    var offsetY = 0f

    init {

    }

    fun setUpperRowSelection(position:Int)
    {
        _upperrowselection.value = position
    }

    fun setLowerRowSelection(position:Int)
    {
        _lowerrowselection.value = position
    }

    fun reDrawCanvas()
    {
        _drawcounter.value++
    }

    fun setDrawMode(drawmode:DrawMode)
    {
        _drawmode.value = drawmode
    }

    fun getDrawMode(): DrawMode {
        return drawmode.value
    }

    fun addDrwaModel()
    {
        drawmodellist.add(DrawModel(
            path,
            color = if(drawmode.value == DrawMode.eraser)Color.Transparent else color,
            size = size,
            blendMode = if(drawmode.value == DrawMode.eraser) BlendMode.Clear else blendMode,
            alpha = alpha
        ))
    }

    fun undoDraw()
    {
        val drawmodel = drawmodellist.removeLastOrNull()
        drawmodel?.let {
            redodrawmodellist.add(drawmodel)
            reDrawCanvas()
        }
    }

    fun redoDraw()
    {
        val drawmodel = redodrawmodellist.removeLastOrNull()
        drawmodel?.let {
            drawmodellist.add(drawmodel)
            reDrawCanvas()
        }
    }

    fun clearCanvas()
    {
        drawmodellist.clear()
        redodrawmodellist.clear()
        reDrawCanvas()
    }


    private val _isColorPicker = MutableStateFlow(false)
    val isColorPicker:StateFlow<Boolean> = _isColorPicker

    fun showColorPicker()
    {
        _isColorPicker.value = !_isColorPicker.value
    }

    private val _drawSetting = MutableStateFlow(false)
    val drawSetting:StateFlow<Boolean> = _drawSetting

    fun showdrawSettingDialog()
    {
        _drawSetting.value = !_drawSetting.value
    }

    fun setColor(colorcode: String) {
        color = Color(android.graphics.Color.parseColor(colorcode))
    }

    fun setDrawSize(it: Float) {
        size = it
    }

    fun setDrawAlpha(it: Float) {
        alpha = it
    }



}
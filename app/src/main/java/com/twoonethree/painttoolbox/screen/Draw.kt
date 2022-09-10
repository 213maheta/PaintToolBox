package com.twoonethree.painttoolbox.screen

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twoonethree.painttoolbox.R
import com.twoonethree.painttoolbox.sealed.DrawMode
import com.twoonethree.painttoolbox.viewmodels.PaintModel
import kotlinx.coroutines.flow.collect
import kotlin.math.roundToInt

@Composable
fun DrawScreen()
{
    val paintViewModel = PaintModel()
    Log.e("TAG", "draw: screen", )

    Column(modifier = Modifier
        .fillMaxSize()
        ) {
        val buttonmodifier = Modifier
            .weight(1f, true)
            .size(60.dp)
            .padding(8.dp)
            .border(3.dp, color = Color.Black, CircleShape)
            .clip(CircleShape)

        LazyRow(modifier = Modifier
            .weight(1f, true)
            .background(color = Color.LightGray),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                TopButtonRow(buttonmodifier, paintViewModel)
            }
            Log.e("TAG", "draw: screen1", )
        }

        Box(modifier = Modifier
            .weight(8f, true)
        ) {
            MyCanvas(paintViewModel)
            Log.e("TAG", "draw: screen2", )
        }

        Row(modifier = Modifier
            .weight(1f, true)
            .background(color = Color.LightGray),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LowerButtonRow(buttonmodifier, paintViewModel)
            Log.e("TAG", "draw: screen3", )
        }
    }

    paintViewModel.colorlist = stringArrayResource(id = R.array.colorlist).toMutableList()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter)
    {
        ColorPicker(paintViewModel = paintViewModel)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
    {
        DrawSettingDialog(paintViewModel = paintViewModel)
    }
}

@Composable
fun BoxOffset(paintViewModel: PaintModel)
{
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(modifier = Modifier
        .fillMaxSize()
        .offset {
            IntOffset(
                offsetX.roundToInt(),
                offsetY.roundToInt()
            )
        }
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                if (paintViewModel.getDrawMode() == DrawMode.move) {
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
        }
    )
    {

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MyCanvas(paintViewModel: PaintModel)
{
    var drawcounter by remember{ mutableStateOf(0)}
    var pretouchX = 0f
    var pretouchY = 0f

    LaunchedEffect(key1 = Unit)
    {
        paintViewModel.drawcounter.collect {
            drawcounter = it
        }
    }
    Canvas(modifier = Modifier
        .fillMaxSize()
        .offset {
            IntOffset(
                paintViewModel.offsetX.roundToInt(),
                paintViewModel.offsetY.roundToInt()
            )
        }
        .graphicsLayer(alpha = 0.99f)
        .pointerInteropFilter { event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pretouchX = event.x
                    pretouchY = event.y
                    when (paintViewModel.getDrawMode()) {
                        DrawMode.brush, DrawMode.line, DrawMode.circle, DrawMode.square, DrawMode.eraser,
                        DrawMode.multiline, DrawMode.multicircle
                        -> {
                            paintViewModel.path = Path()
                            paintViewModel.path.moveTo(event.x, event.y)
                            paintViewModel.addDrwaModel()
                        }
                    }

                }
                MotionEvent.ACTION_MOVE -> {
                    when (paintViewModel.getDrawMode()) {
                        DrawMode.brush -> {
                            paintViewModel.path.lineTo(event.x, event.y)
                        }
                        DrawMode.square -> {
                            paintViewModel.path.reset()
                            paintViewModel.path.addRect(
                                Rect(
                                    pretouchX,
                                    pretouchY,
                                    event.x,
                                    event.y
                                )
                            )
                        }
                        DrawMode.circle -> {
                            paintViewModel.path.reset()
                            paintViewModel.path.addOval(
                                Rect(
                                    pretouchX,
                                    pretouchY,
                                    event.x,
                                    event.y
                                )
                            )
                        }
                        DrawMode.multicircle -> {
                            paintViewModel.path.addOval(
                                Rect(
                                    pretouchX,
                                    pretouchY,
                                    event.x,
                                    event.y
                                )
                            )
                        }
                        DrawMode.line -> {
                            paintViewModel.path.reset()
                            paintViewModel.path.moveTo(pretouchX, pretouchY)
                            paintViewModel.path.lineTo(event.x, event.y)
                        }
                        DrawMode.multiline -> {
                            paintViewModel.path.moveTo(pretouchX, pretouchY)
                            paintViewModel.path.lineTo(event.x, event.y)
                        }

                        DrawMode.eraser -> {
                            paintViewModel.path.lineTo(event.x, event.y)
                        }
                        DrawMode.move -> {
                            Log.e(
                                "TAG",
                                "MyCanvas: ${paintViewModel.offsetX.roundToInt()}  ${paintViewModel.offsetY.roundToInt()}",
                            )
                            paintViewModel.offsetX = event.x
                            paintViewModel.offsetY = event.y
                        }
                    }
                    paintViewModel.reDrawCanvas()
                }
                MotionEvent.ACTION_UP -> {

                }
            }
            true
        }
        ,
        onDraw = {
            Log.e("TAG", "MyCanvas: ${drawcounter}", )
            for(drawmodel in paintViewModel.drawmodellist)
            {
                drawPath(path = drawmodel.path,
                    color = drawmodel.color,
                    alpha = drawmodel.alpha,
                    blendMode = drawmodel.blendMode,
                    style = Stroke(
                        width = drawmodel.size,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        })
}

@Composable
fun TopButtonRow(buttonmodifier: Modifier, paintViewModel: PaintModel)
{
    var buttonid = -1
    val clickedid = remember { mutableStateOf(0) }
    val setSelection = { id:Int -> paintViewModel.setUpperRowSelection(id) }
    val setDrawMode = { drawmode:DrawMode -> paintViewModel.setDrawMode(drawmode) }

    LaunchedEffect(key1 = Unit)
    {
        paintViewModel.upperrowselection.collect {
            clickedid.value = it
        }
    }
    Log.e("TAG", "draw: UpperButtonRow", )
    MyImageButton(
        R.drawable.brush,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        setSelection,
        setDrawMode,
        DrawMode.brush
    )
    MyImageButton(
        R.drawable.square,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        setSelection,
        setDrawMode,
        DrawMode.square
    )
    MyImageButton(
        R.drawable.circle,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        setSelection,
        setDrawMode,
        DrawMode.circle
    )
    MyImageButton(
        R.drawable.line,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        setSelection,
        setDrawMode,
        DrawMode.line
    )
    MyImageButton(
        R.drawable.eraser,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        setSelection,
        setDrawMode,
        DrawMode.eraser
    )
    MyImageButton(
        R.drawable.multi_circle,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        setSelection,
        setDrawMode,
        DrawMode.multicircle
    )
    MyImageButton(
        R.drawable.multi_line,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        setSelection,
        setDrawMode,
        DrawMode.multiline
    )
    MyImageButton(
        R.drawable.multi_line,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        setSelection,
        setDrawMode,
        DrawMode.anyshape
    )
}

@Composable
fun LowerButtonRow(buttonmodifier: Modifier, paintViewModel: PaintModel)
{
    var buttonid = -1
    val clickedid = remember { mutableStateOf(-1) }
    val onclick = { id:Int -> paintViewModel.setLowerRowSelection(id)}
    LaunchedEffect(key1 = Unit)
    {
        paintViewModel.lowerrowselection.collect {
            clickedid.value = it
        }
    }
    Log.e("TAG", "draw: LowerButtonRow", )
    MyImageButton(
        R.drawable.undo,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        onclick,
        bottomonclick = {paintViewModel.undoDraw()}
    )
    MyImageButton(
        R.drawable.redo,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        onclick,
        bottomonclick = {paintViewModel.redoDraw()}
    )
    MyImageButton(
        R.drawable.refresh,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        onclick,
        bottomonclick = {paintViewModel.clearCanvas()}
    )
    MyImageButton(
        R.drawable.color,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        onclick,
        bottomonclick = {paintViewModel.showColorPicker()}
    )
    MyImageButton(
        R.drawable.alpha,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        onclick,
        bottomonclick = {paintViewModel.showdrawSettingDialog()}
    )
    MyImageButton(
        R.drawable.move,
        buttonmodifier,
        ++buttonid,
        clickedid.value,
        onclick,
        bottomonclick = {paintViewModel.setDrawMode(DrawMode.move)}
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MyImageButton(
    buttonmodifier1: Int,
    buttonmodifier: Modifier,
    buttonid: Int,
    clickedid: Int,
    onclick: (Int) -> Unit,
    setDrawMode: (DrawMode) -> Unit = {},
    drawMode: DrawMode = DrawMode.empty,
    bottomonclick: () -> Unit = {}
)
{
    Image(painter = painterResource(id = buttonmodifier1),
        contentDescription = "",
        modifier = buttonmodifier
            .background(
                color = if (buttonid == clickedid) Color.Magenta else Color.White,
                shape = CircleShape
            )
            .clickable {
                onclick(buttonid)
                if (drawMode != DrawMode.empty) {
                    setDrawMode(drawMode)
                }
                bottomonclick()
            }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorPicker(paintViewModel: PaintModel)
{
    var showColorPicker by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit)
    {
        paintViewModel.isColorPicker.collect {
            showColorPicker = it
        }
    }
    AnimatedVisibility(visible = showColorPicker) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(color = Color.White)) {

            Row(modifier = Modifier.weight(1f, true)) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Image(painter = painterResource(id = R.drawable.ok),
                        contentDescription = "ok",
                        alignment = Alignment.TopEnd,
                        modifier = Modifier
                            .clickable {
                                paintViewModel.showColorPicker()
                            }
                            .size(50.dp)
                            .padding(3.dp)
                    )
                }
            }
            LazyVerticalGrid(cells = GridCells.Fixed(8),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(4f, true)
                    .background(color = Color.White),
                verticalArrangement = Arrangement.Bottom,
            )
            {
                itemsIndexed(paintViewModel.colorlist) { index, colorcode ->
                    Box(modifier = Modifier
                        .size(50.dp)
                        .padding(5.dp)
                        .border(width = 3.dp, color = Color.Black, CircleShape)
                        .clip(CircleShape)
                        .background(
                            color = Color(android.graphics.Color.parseColor(colorcode)),
                            shape = CircleShape
                        )
                        .clickable {
                            paintViewModel.setColor(colorcode)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DrawSettingDialog(paintViewModel: PaintModel)
{
    var showSettingdialog by remember { mutableStateOf(false) }
    var sizeslidervalue by remember{ mutableStateOf(20f)}
    var alphaslidervalue by remember{ mutableStateOf(1f)}

    LaunchedEffect(key1 = Unit)
    {
        paintViewModel.drawSetting.collect {
            showSettingdialog = it
        }
    }

    AnimatedVisibility(visible = showSettingdialog) {
        Card(modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.6f)
            .clip(RoundedCornerShape(20.dp))
            ,
            elevation = 20.dp
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
            ) {
                Row(modifier = Modifier
                    .weight(3f, true)
                ){
                    Box(modifier = Modifier
                        .fillMaxSize()
                    ){
                        smallCanvas(paintViewModel = paintViewModel)
                    }
                }
                Row(modifier = Modifier
                    .weight(3f, true)
                    .padding(top = 5.dp, start = 5.dp, end = 5.dp)
                    .background(color = Color.LightGray),
                    verticalAlignment = Alignment.CenterVertically){
                    Text(text = "size",
                        fontSize = 16.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .size(60.dp)
                            .background(color = Color.Cyan, shape = CircleShape)
                            .wrapContentHeight(Alignment.CenterVertically)
                            .weight(2f, true),
                    )
                    Slider(value = sizeslidervalue,
                        onValueChange = {
                            sizeslidervalue = it
                            paintViewModel.setDrawSize(it) },
                        valueRange = 0f..100f,
                        modifier = Modifier
                            .weight(8f, true)
                            .padding(horizontal = 5.dp)
                    )
                }
                Row(modifier = Modifier
                    .weight(3f, true)
                    .padding(top = 5.dp, start = 5.dp, end = 5.dp)
                    .background(color = Color.LightGray),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(text = "Alpha",
                        fontSize = 16.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .size(60.dp)
                            .background(color = Color.Cyan, shape = CircleShape)
                            .wrapContentHeight(Alignment.CenterVertically)
                            .weight(2f, true),
                    )
                    Slider(value = alphaslidervalue,
                        onValueChange = {
                            alphaslidervalue = it
                            paintViewModel.setDrawAlpha(it)
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier
                            .weight(8f, true)
                            .padding(horizontal = 5.dp)
                    )
                }
                Button(onClick = { paintViewModel.showdrawSettingDialog() },
                    modifier = Modifier
                        .weight(1.5f, fill = true)
                        .padding(top = 5.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Ok",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}


@Composable
fun smallCanvas(paintViewModel: PaintModel)
{
    Canvas(modifier = Modifier
        .fillMaxSize(),
        onDraw = {
            drawCircle(color = paintViewModel.color,
                radius = paintViewModel.size/2,
                alpha = paintViewModel.alpha,
                center = center
            )
        })
}
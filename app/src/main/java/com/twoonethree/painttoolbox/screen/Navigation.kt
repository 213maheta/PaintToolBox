package com.twoonethree.painttoolbox.screen

import android.graphics.drawable.Icon
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.twoonethree.painttoolbox.R
import com.twoonethree.painttoolbox.sealed.NavigationMode
import kotlinx.coroutines.delay

@Composable
fun Navigation()
{
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavigationMode.splash.route){
        composable(route = NavigationMode.splash.route){
            SplashScreen(navController)
        }
        composable(route = NavigationMode.draw.route){
            DrawScreen()
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController)
{
    ShowSplashIcon()
    LaunchedEffect(key1 = Unit)
    {
        delay(2000)
        navController.popBackStack()
        navController.navigate(NavigationMode.draw.route)
    }
}

@Composable
fun ShowSplashIcon()
{
    val scale = remember{ androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(key1 = Unit){
        scale.animateTo(1f, tween(2000))
    }
    Box(modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
        )
    {
        Image(painter = painterResource(id = R.drawable.splashicon),
            contentDescription = "Spalsh Icon",
            modifier = Modifier
                .scale(scale.value)
        )
    }
}
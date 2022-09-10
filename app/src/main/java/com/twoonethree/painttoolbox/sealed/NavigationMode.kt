package com.twoonethree.painttoolbox.sealed

sealed class NavigationMode(val route:String)
{
    object splash:NavigationMode("splashscreen")
    object draw:NavigationMode("drawscreen")
}

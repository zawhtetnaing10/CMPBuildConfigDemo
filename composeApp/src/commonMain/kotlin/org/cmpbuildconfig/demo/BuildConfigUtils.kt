package org.cmpbuildconfig.demo

import androidx.compose.ui.graphics.Color

object BuildConfigUtils{
    enum class Flavors(val variant : String){
        GRAB("grab"), FOOD_PANDA("foodpanda")
    }

    // Get Primary Color
    fun getPrimaryColor() : Color{
        return when(BuildKonfig.variant){
            Flavors.GRAB.variant -> {
                Color(0xFF02B150)
            }
            Flavors.FOOD_PANDA.variant -> {
                Color(0xFFFF2B85)
            }
            else -> {
                Color.White
            }
        }
    }

    // App Name
    fun getAppName() : String {
        return when(BuildKonfig.variant){
            Flavors.GRAB.variant -> {
                "Grab"
            }
            Flavors.FOOD_PANDA.variant -> {
                "Food Panda"
            }
            else -> {
               ""
            }
        }
    }
}
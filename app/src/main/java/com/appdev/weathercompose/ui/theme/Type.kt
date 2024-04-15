package com.appdev.weathercompose.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.appdev.weathercompose.R

val russoFont = FontFamily(
    Font(R.font.russo_one_regular),
)

val chakraFont = FontFamily(
    Font(R.font.chakra_petch_regular),
)

// Set of Material typography styles to start with
val Typography = Typography(
    defaultFontFamily = russoFont,
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)
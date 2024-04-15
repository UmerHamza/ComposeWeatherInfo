package com.appdev.weathercompose.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appdev.weathercompose.ui.theme.russoFont

@Composable
fun AppText(
    modifier: Modifier = Modifier,
    text: String?,
    textSize: TextUnit = 15.sp,
    color: Color = Color.White,
    textAlign: TextAlign = TextAlign.Start,
    isUnderlineText: Boolean = false,
    lines: Int = 1,
    fontType: FontFamily = russoFont
) {
    var decoration = TextDecoration.None
    if (isUnderlineText) {
        decoration = TextDecoration.Underline
    }
    Text(
        text = text ?: "",
        maxLines = lines,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            fontSize = textSize,
            color = color,
            textAlign = textAlign,
            fontFamily = fontType
        ),
        modifier = modifier,
        textDecoration = decoration,
    )
}

@Composable
fun AppButton(callBack: (Boolean) -> Unit?, modifier: Modifier, text: String, textColor: Color) {
    TextButton(
        onClick = { callBack.invoke(true) },
        modifier = modifier
    ) {
        AppText(text = text, modifier = Modifier, color = textColor, lines = 3)
    }
}

@Composable
fun AppIconButton(callBack: (Boolean) -> Unit?, icon: ImageVector,modifier: Modifier = Modifier) {
    IconButton(
        onClick = { callBack.invoke(true) }, modifier = modifier
    ) {
        Icon(
            icon, null, tint = Color.White
        )
    }
}

@Composable
fun FetchBackArrowHeader(
    callBack: (Boolean) -> Unit?, heading: String?, textSize: TextUnit = 20.sp
) {
    Row(
        modifier = Modifier.background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AppIconButton(callBack = {
            if (it) {
                callBack.invoke(true)
            }
        }, icon = Icons.Filled.ArrowBack)
        AppText(text = heading, textSize = textSize, modifier = Modifier)
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    initialValue:String = "",
    placeHolderText: String?,
    trailingIcon: ImageVector?,
    allowOnlyCharacters: Boolean = false,
    action: ImeAction = ImeAction.Done,
    callBack: (String) -> Unit?,
    maxLines: Int = 1
) {
    // to allow only characters
    val pattern = remember { Regex("[a-zA-z\\s]*") }
    // to remember soft keypad state
    val keyboardController = LocalSoftwareKeyboardController.current
    // to remember local focus manager
    val focusManager = LocalFocusManager.current
    // to remember text
    var info by remember { mutableStateOf(TextFieldValue(initialValue)) }
    // field
    OutlinedTextField(
        value = info,
        onValueChange = {
            info = if (allowOnlyCharacters && it.text.matches(pattern)) {
                it
            } else {
                it
            }
            callBack.invoke(info.text)
        },
        modifier = modifier,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.White
        ),
        maxLines = maxLines,
        singleLine = maxLines <= 1,
        keyboardOptions = KeyboardOptions(imeAction = action),
        keyboardActions = KeyboardActions(onSearch = {
            keyboardController?.hide()
            focusManager.clearFocus()
            callBack.invoke(info.text)
        }, onDone = {
            keyboardController?.hide()
            focusManager.clearFocus()
            callBack.invoke(info.text)
        }),
        trailingIcon = {
            if (trailingIcon != null) {
                AppIconButton(callBack = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    callBack.invoke(info.text)
                }, icon = trailingIcon)
            }
        },
        textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
        placeholder = {
            Text(
                text = placeHolderText ?: "",
                style = TextStyle(color = Color.White, fontSize = 15.sp)
            )
        }
    )
}
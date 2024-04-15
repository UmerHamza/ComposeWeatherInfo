package com.appdev.weathercompose.view.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.appdev.weathercompose.R
import com.appdev.weathercompose.ui.theme.DARK_BLUE
import com.appdev.weathercompose.ui.theme.GREY
import com.appdev.weathercompose.ui.theme.OFF_WHITE
import com.appdev.weathercompose.ui.theme.chakraFont
import com.appdev.weathercompose.utils.AppUtils
import com.appdev.weathercompose.utils.DateTimeUtils
import com.appdev.weathercompose.viewModel.NotesViewModel
import com.appdev.weathercompose.widgets.AppButton
import com.appdev.weathercompose.widgets.AppText
import com.appdev.weathercompose.widgets.AppTextField

@Composable
fun AddNoteView(
    notesInfo: String?,
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
) {
    // Update info
    viewModel.updateIsUpdateRequest(notesInfo)
    // layout
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (ivBack, tvAddItem, etTitle, tvLastUpdated, etDescription, btnSave) = createRefs()
        IconButton(
            onClick = { closeCurrentView(navController) },
            modifier = Modifier.constrainAs(ivBack) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }) {
            Icon(
                Icons.Filled.ArrowBack,
                null,
                tint = OFF_WHITE
            )
        }
        AppText(
            text = if (viewModel.checkIfUpdateRequest()) stringResource(id = R.string.update_note) else stringResource(
                id = R.string.add_note
            ),
            color = OFF_WHITE,
            fontType = chakraFont,
            modifier = Modifier.constrainAs(tvAddItem) {
                start.linkTo(ivBack.end, 12.dp)
                top.linkTo(ivBack.top)
                bottom.linkTo(ivBack.bottom)
            })
        AppTextField(
            placeHolderText = stringResource(id = R.string.text_title),
            initialValue = viewModel.title ?: "",
            trailingIcon = null,
            action = ImeAction.Next,
            modifier = Modifier
                .padding(5.dp)
                .height(50.dp)
                .constrainAs(etTitle) {
                    start.linkTo(parent.start, 5.dp)
                    end.linkTo(parent.end, 5.dp)
                    top.linkTo(ivBack.bottom, 16.dp)
                    width = Dimension.fillToConstraints
                },
            callBack = { setTitle(viewModel, it) })
        AppTextField(
            placeHolderText = stringResource(id = R.string.text_description),
            initialValue = viewModel.description ?: "",
            trailingIcon = null,
            maxLines = 2,
            modifier = Modifier
                .padding(5.dp)
                .height(150.dp)
                .constrainAs(etDescription) {
                    start.linkTo(parent.start, 5.dp)
                    end.linkTo(parent.end, 5.dp)
                    top.linkTo(etTitle.bottom, 4.dp)
                    width = Dimension.fillToConstraints
                },
            callBack = { setDescription(viewModel, it) })
        AppButton(
            callBack = {
                handleAddItemBtnSave(viewModel, navController)
            },
            text = if (viewModel.checkIfUpdateRequest()) stringResource(id = R.string.update) else stringResource(
                id = R.string.save
            ),
            textColor = OFF_WHITE,
            modifier = Modifier
                .padding(top = 5.dp)
                .background(color = DARK_BLUE)
                .constrainAs(btnSave) {
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(etDescription.bottom, 35.dp)
                }
        )
        if (viewModel.checkIfUpdateRequest()) {
            val time = DateTimeUtils.convertTimeStampToDate(viewModel.selectedNote?.timeStamp)
            AppText(
                text = stringResource(id = R.string.last_updated_at, time),
                color = GREY,
                fontType = chakraFont,
                modifier = Modifier.constrainAs(tvLastUpdated) {
                    top.linkTo(etDescription.bottom, 5.dp)
                    start.linkTo(etDescription.start, 5.dp)
                }
            )
        }
    }
}

fun closeCurrentView(navController: NavController) {
    navController.popBackStack()
}

fun setTitle(viewModel: NotesViewModel, title: String) {
    viewModel.title = title
}

fun setDescription(viewModel: NotesViewModel, description: String) {
    viewModel.description = description
}

fun handleAddItemBtnSave(viewModel: NotesViewModel, navController: NavController) {
    val isValidateSuccess = viewModel.validateInputs()
    if (isValidateSuccess) {
        viewModel.addOrUpdateNote()
        closeCurrentView(navController)
    } else {
        AppUtils.showToast(viewModel.notesState.errorMessage)
    }
}
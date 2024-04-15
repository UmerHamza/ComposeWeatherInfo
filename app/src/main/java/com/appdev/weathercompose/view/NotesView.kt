package com.appdev.weathercompose.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.appdev.weathercompose.R
import com.appdev.weathercompose.constants.AppConstants
import com.appdev.weathercompose.dal.network.AppModule
import com.appdev.weathercompose.info.notes.NotesInfo
import com.appdev.weathercompose.ui.theme.*
import com.appdev.weathercompose.utils.OnLifecycleEvent
import com.appdev.weathercompose.viewModel.NotesViewModel
import com.appdev.weathercompose.widgets.AppIconButton
import com.appdev.weathercompose.widgets.AppText

@Composable
fun NotesView(navController: NavHostController, viewModel: NotesViewModel = hiltViewModel()) {
    OnLifecycleEvent { owner, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                viewModel.fetchAllNotes()
            }
            Lifecycle.Event.ON_RESUME -> {

            }
            Lifecycle.Event.ON_STOP -> {

            }
            else -> {

            }
        }
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (ivAdd, tvNoNotes, loader, tvTotalNotes, rvNotes, ivViewType) = createRefs()
        if (viewModel.notesState.isLoading) {
            CircularProgressIndicator(color = DARK_BLUE, modifier = Modifier
                .constrainAs(loader) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                })
        } else {
            if (viewModel.notesState.isEmptyState) {
                AppText(text = stringResource(id = R.string.no_notes_found),
                    color = OFF_WHITE,
                    modifier = Modifier
                        .constrainAs(tvNoNotes) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        })
            } else {
                val viewType: String by viewModel.viewType.collectAsState()
                val notesList = viewModel.notesState.notesInfo ?: arrayListOf()
                AppText(text = stringResource(id = R.string.total_notes, notesList.size),
                    modifier = Modifier
                        .constrainAs(tvTotalNotes) {
                            top.linkTo(ivAdd.bottom, 16.dp)
                            start.linkTo(parent.start, 16.dp)
                        })

                if (viewType == AppConstants.LIST_VIEW_TYPE) {
                    LazyColumn(modifier = Modifier
                        .constrainAs(rvNotes) {
                            top.linkTo(tvTotalNotes.bottom, 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }) {
                        itemsIndexed(notesList) { _, item ->
                            NoteItemView(notesInfo = item, viewModel, navController)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), modifier = Modifier
                            .constrainAs(rvNotes) {
                                top.linkTo(tvTotalNotes.bottom, 16.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                                width = Dimension.fillToConstraints
                                height = Dimension.fillToConstraints
                            }
                    ) {
                        itemsIndexed(notesList) { _, item ->
                            NoteItemView(notesInfo = item, viewModel, navController)
                        }
                    }
                }
            }
            IconButton(onClick = {
                viewModel.updateViewTypeIcon()
            }, modifier = Modifier.constrainAs(ivViewType) {
                end.linkTo(parent.end, 16.dp)
                top.linkTo(parent.top, 16.dp)
            }) {
                Icon(
                    viewModel.viewTypeIcon,
                    null,
                    tint = GREY
                )
            }

            IconButton(onClick = {
                navController.navigate(Routes.ADD_NOTES)
            }, modifier = Modifier
                .constrainAs(ivAdd) {
                    start.linkTo(parent.start, 2.dp)
                    top.linkTo(parent.top, 16.dp)
                }) {
                Icon(
                    Icons.Filled.AddCircle,
                    null,
                    tint = OFF_WHITE
                )
            }
        }
    }
}

@Composable
fun NoteItemView(
    notesInfo: NotesInfo,
    viewModel: NotesViewModel,
    navController: NavHostController
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        elevation = 8.dp,
        backgroundColor = DARK_BLUE
    ) {
        val showDialogState: Boolean by viewModel.showDeleteDialog.collectAsState()
        ConstraintLayout(modifier = Modifier.clickable {
            val info = AppModule.provideGson().toJson(notesInfo)
            val route = Routes.ADD_NOTES.replace("{notesInfo}", info)
            navController.navigate(route)
        }) {
            val (tvTitle, tvDescription, tvDate, tvTime, ivDelete) = createRefs()
            AppText(
                text = notesInfo.title,
                textSize = 20.sp,
                color = OFF_WHITE,
                modifier = Modifier
                    .padding(start = 10.dp, end = 5.dp, top = 5.dp)
                    .constrainAs(tvTitle) {
                        start.linkTo(parent.start)
                        end.linkTo(ivDelete.start, 16.dp)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    })
            AppIconButton(
                callBack = {
                    viewModel.selectedNote = notesInfo
                    viewModel.onOpenDialogClicked()
                },
                icon = Icons.Filled.Delete,
                modifier = Modifier.constrainAs(ivDelete) {
                    end.linkTo(parent.end, 5.dp)
                    top.linkTo(tvTitle.top)
                    bottom.linkTo(tvTitle.bottom)
                })
            AppText(
                text = notesInfo.description,
                textSize = 15.sp,
                color = GREY,
                fontType = chakraFont,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .constrainAs(tvDescription) {
                        start.linkTo(parent.start)
                        top.linkTo(tvTitle.bottom)
                    })
        }
        if (showDialogState) {
            ShowDeleteAlertDialog(show = showDialogState, viewModel) {
                viewModel.onDialogDismiss()
            }
        }
    }
}

@Composable
fun ShowDeleteAlertDialog(show: Boolean, viewModel: NotesViewModel, onDismissRequest: () -> Unit) {
    if (show) {
        AlertDialog(onDismissRequest = onDismissRequest,
            properties = DialogProperties(dismissOnBackPress = false,dismissOnClickOutside = false),
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDialogConfirm()
                }) {
                    AppText(text = stringResource(id = R.string.yes), color = OFF_BLACK)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onDialogDismiss()
                }) {
                    AppText(
                        text = stringResource(id = R.string.cancel),
                        color = OFF_BLACK,
                        fontType = chakraFont
                    )
                }
            },
            title = {
                AppText(
                    text = stringResource(id = R.string.please_confirm),
                    color = OFF_BLACK,
                    fontType = chakraFont
                )
            },
            text = {
                AppText(
                    text = stringResource(id = R.string.confirm_delete),
                    color = DARK_BLUE,
                    lines = 2,
                    fontType = chakraFont
                )
            })
    }
}
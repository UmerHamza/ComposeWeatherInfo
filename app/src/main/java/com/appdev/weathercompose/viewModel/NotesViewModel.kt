package com.appdev.weathercompose.viewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.weathercompose.R
import com.appdev.weathercompose.constants.AppConstants
import com.appdev.weathercompose.dal.network.AppModule
import com.appdev.weathercompose.dal.network.ResourceProvider
import com.appdev.weathercompose.info.notes.NotesInfo
import com.appdev.weathercompose.info.notes.NotesState
import com.appdev.weathercompose.repository.NotesRepository
import com.appdev.weathercompose.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NotesRepository,
    private val provider: ResourceProvider
) : ViewModel() {

    // for adding/updating new notes
    var title: String? = null
    var description: String? = null

    // used while updating new notes
    var selectedNote: NotesInfo? = null
    var prevSelectedNote: NotesInfo? = null

    // check if it's an update request
    private var isUpdateRequest: Boolean = false

    private val _viewType = MutableStateFlow(AppConstants.LIST_VIEW_TYPE)
    val viewType: StateFlow<String> = _viewType.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    var viewTypeIcon: ImageVector = Icons.Filled.ViewList

    fun updateViewTypeIcon() {
        if (_viewType.value == AppConstants.LIST_VIEW_TYPE) {
            viewTypeIcon = Icons.Filled.GridView
            _viewType.value = AppConstants.GRID_VIEW_TYPE
        } else {
            viewTypeIcon = Icons.Filled.ViewList
            _viewType.value = AppConstants.LIST_VIEW_TYPE
        }
    }

    fun checkIfUpdateRequest(): Boolean {
        return isUpdateRequest
    }

    fun updateIsUpdateRequest(info: String?) {
        if (info.isNullOrEmpty() || info == "{notesInfo}") {
            isUpdateRequest = false
        } else {
            isUpdateRequest = true
            val notesInfo = AppModule.provideGson().fromJson(info, NotesInfo::class.java)
            notesInfo?.let { notes ->
                selectedNote = notes
                prevSelectedNote = notes
                title = notes.title
                description = notes.description
            }
        }
    }

    fun onOpenDialogClicked() {
        _showDeleteDialog.value = true
    }

    fun onDialogConfirm() {
        selectedNote?.let {
            viewModelScope.launch {
                repository.deleteNote(it)
                fetchAllNotes()
            }
        }
        _showDeleteDialog.value = false
    }

    fun onDialogDismiss() {
        _showDeleteDialog.value = false
    }

    private val _notesState = mutableStateOf(
        NotesState(
            true,
            isEmptyState = false,
            errorMessage = null,
            notesInfo = null
        )
    )
    val notesState: NotesState
        get() = _notesState.value

    fun validateInputs(): Boolean {
        if (title.isNullOrEmpty()) {
            _notesState.value = NotesState(
                false,
                isEmptyState = false,
                errorMessage = provider.getString(R.string.enter_title),
                notesInfo = null
            )
            return false
        } else if (description.isNullOrEmpty()) {
            _notesState.value = NotesState(
                false,
                isEmptyState = false,
                errorMessage = provider.getString(R.string.enter_description),
                notesInfo = null
            )
            return false
        }
        return true
    }

    fun verifyIfDataIsUpdated(): Boolean {
        if (prevSelectedNote?.title == selectedNote?.title && prevSelectedNote?.description == selectedNote?.description) {
            _notesState.value = NotesState(
                false, isEmptyState = false,
                errorMessage = provider.getString(R.string.data_not_updated),
                notesInfo = null
            )
            return false
        }
        return true
    }


    fun addOrUpdateNote() = viewModelScope.launch {
        _notesState.value = NotesState(true, isEmptyState = false, null, null)
        val noteId = if (isUpdateRequest) selectedNote?.noteId else null
        val notesInfo = NotesInfo(
            title = title, description = description, isDeleted = false,
            timeStamp = DateTimeUtils.getTimeStamp(), deletionDate = null, noteId = noteId
        )
        if (isUpdateRequest) {
            repository.updateNote(notesInfo)
        } else {
            repository.addNote(notesInfo)
        }
        fetchAllNotes()
    }


    fun fetchAllNotes() = viewModelScope.launch {
        _notesState.value = NotesState(true, isEmptyState = false, null, null)
        val response = repository.fetchNotesList()
        if (response.isNotEmpty()) {
            _notesState.value =
                NotesState(false, isEmptyState = false, null, response as ArrayList<NotesInfo>)
        } else {
            _notesState.value = NotesState(false, isEmptyState = true, null, null)
        }
    }
}
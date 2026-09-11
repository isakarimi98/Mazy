package ir.docscan.app.ui.screens.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ir.docscan.app.data.model.ScannedDoc
import ir.docscan.app.data.processor.ScanSessionManager
import ir.docscan.app.data.repository.DocumentRepository
import ir.docscan.app.data.util.ShamsiDateHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val documents: List<ScannedDoc> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val showNewScanSheet: Boolean = false,
    val selectedDocForAction: ScannedDoc? = null,
    val docToRename: ScannedDoc? = null,
    val docToDelete: ScannedDoc? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DocumentRepository.getInstance(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = combine(
        _uiState,
        repository.documents
    ) { state, repoDocs ->
        state.copy(documents = repoDocs)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleSearch(active: Boolean) {
        _uiState.update {
            it.copy(
                isSearchActive = active,
                searchQuery = if (!active) "" else it.searchQuery
            )
        }
    }

    fun showNewScanOptions(show: Boolean) {
        _uiState.update { it.copy(showNewScanSheet = show) }
    }

    fun toggleFavorite(docId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(docId)
        }
    }

    fun setDocToRename(doc: ScannedDoc?) {
        _uiState.update { it.copy(docToRename = doc) }
    }

    fun setDocToDelete(doc: ScannedDoc?) {
        _uiState.update { it.copy(docToDelete = doc) }
    }

    fun renameDocument(docId: String, newTitle: String) {
        viewModelScope.launch {
            if (newTitle.isNotBlank()) {
                repository.renameDocument(docId, newTitle.trim())
            }
            _uiState.update { it.copy(docToRename = null) }
        }
    }

    fun deleteDocument(docId: String) {
        viewModelScope.launch {
            repository.deleteDocument(docId)
            _uiState.update { it.copy(docToDelete = null) }
        }
    }

    fun onMediaSelected(uri: Uri, isCamera: Boolean, onReadyToNavigate: () -> Unit) {
        viewModelScope.launch {
            val title = if (isCamera) {
                "اسکن دوربین ${ShamsiDateHelper.getCurrentShamsiDate()}"
            } else {
                "سند گالری ${ShamsiDateHelper.getCurrentShamsiDate()}"
            }
            ScanSessionManager.setCapturedImage(getApplication(), uri, title)
            _uiState.update { it.copy(showNewScanSheet = false) }
            onReadyToNavigate()
        }
    }

    fun openExistingDoc(doc: ScannedDoc, onReadyToNavigate: () -> Unit) {
        viewModelScope.launch {
            ScanSessionManager.loadExistingDoc(getApplication(), doc)
            onReadyToNavigate()
        }
    }
}

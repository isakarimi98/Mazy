package ir.docscan.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.docscan.app.data.mock.SampleDocs
import ir.docscan.app.data.model.ScannedDoc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val documents: List<ScannedDoc> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val showNewScanSheet: Boolean = false,
    val selectedDocForAction: ScannedDoc? = null
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDocuments()
    }

    private fun loadDocuments() {
        _uiState.update { it.copy(documents = SampleDocs.initialDocuments) }
    }

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
        _uiState.update { state ->
            val updated = state.documents.map { doc ->
                if (doc.id == docId) doc.copy(isFavorite = !doc.isFavorite) else doc
            }
            state.copy(documents = updated)
        }
    }

    fun deleteDocument(docId: String) {
        _uiState.update { state ->
            state.copy(documents = state.documents.filter { it.id != docId })
        }
    }
}

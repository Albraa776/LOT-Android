package com.albraa.lot.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.albraa.lot.LOTApplication
import com.albraa.lot.core.database.HistoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as LOTApplication
    private val dao = app.database.historyDao()
    private val tts = app.ttsEngine

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow("ALL") // "ALL", "FAVORITES", "TEXT", "IMAGE", "SPEECH"
    val selectedFilter: StateFlow<String> = _selectedFilter

    val historyList: StateFlow<List<HistoryEntity>> = combine(_searchQuery, _selectedFilter) { query, filter ->
        query to filter
    }.flatMapLatest { (query, filter) ->
        if (query.isNotBlank()) {
            dao.search(query)
        } else {
            when (filter) {
                "FAVORITES" -> dao.getFavorites()
                "TEXT" -> dao.getByMode("TEXT")
                "IMAGE" -> dao.getByMode("IMAGE")
                "SPEECH" -> dao.getByMode("SPEECH")
                else -> dao.getAllHistory()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun toggleFavorite(item: HistoryEntity) {
        viewModelScope.launch {
            dao.setFavorite(item.id, !item.isFavorite)
        }
    }

    fun deleteItem(item: HistoryEntity) {
        viewModelScope.launch {
            dao.delete(item)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }

    fun speak(text: String, langCode: String) {
        if (text.isNotBlank()) {
            tts.speak("hist_${System.currentTimeMillis()}", text, langCode)
        }
    }
}

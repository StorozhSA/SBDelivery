package ru.skillbranch.sbdelivery.ui.search

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.skillbranch.sbdelivery.data.RepoDataBase
import ru.storozh.common.EndPoints
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    val database: RepoDataBase,
    point: EndPoints,
) : ProtoViewModel<SearchViewModel.State>(initState, context, savedStateHandle, point) {
    val searchHistory by lazy { database.getSearchHistory() }
    fun searchCategory(text: String) = database.searchCategories(text)
    fun searchDishes(text: String) = database.searchDishes(text)
    fun searchHistoryItemDelete(id: String) = database.delete(id)
    fun searchHistoryItemAdd(text: String) {
        if (text.isNotBlank()) database.addSearchHistoryItem(text)
    }

    fun toggleFavorite(id: String) {
        database.toggleFavoriteDish(id)
    }

    data class State(
        val searchText: String = "",
        val searchHistory: List<String> = emptyList()
    ) : ProtoViewModelState

    override fun checkState() = true
}

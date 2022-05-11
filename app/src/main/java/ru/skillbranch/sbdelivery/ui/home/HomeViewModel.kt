package ru.skillbranch.sbdelivery.ui.home

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import ru.skillbranch.sbdelivery.data.RepoDataBase
import ru.storozh.common.EndPoints
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    val database: RepoDataBase,
    point: EndPoints,
) : ProtoViewModel<HomeViewModel.State>(initState, context, savedStateHandle, point) {
    fun getAllRecommendationDishes() = database.getAllRecommendationDishes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getAllBestDishes(minRating: Double = 4.8) = database.getAllBestDishes(minRating).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getAllPopularDishes() = database.getAllPopularDishes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleFavorite(id: String) {
        database.toggleFavoriteDish(id)
    }

    data class State(val key: String = "") : ProtoViewModelState

    override fun checkState() = true
}

package ru.skillbranch.sbdelivery.ui.favorites

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.skillbranch.sbdelivery.data.EndPointsImpl
import ru.skillbranch.sbdelivery.data.RepoDataBase
import ru.storozh.common.EndPoints
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class FavoritesPageViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    val database: RepoDataBase,
    point: EndPoints
) : ProtoViewModel<FavoritesPageViewModel.State>(initState, context, savedStateHandle, point) {
    private val points by lazy { point as EndPointsImpl }

    // private val _dishes = MutableSharedFlow<List<DishV>>(replay = 0)
    // val dishes = _dishes.asSharedFlow()
    val dishes = points.immutableFavoriteDbFlow()

    // init { database.getAllFavoriteDishes(_dishes, viewModelScope) }

    fun toggleFavorite(id: String) {
        database.toggleFavoriteDish(id)
    }

    data class State(val categoryId: String = "") : ProtoViewModelState

    override fun checkState() = true
}

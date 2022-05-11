package ru.skillbranch.sbdelivery.ui.categories

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.SORT_BY_ALPHABET
import ru.skillbranch.sbdelivery.data.RepoDataBase
import ru.storozh.common.EndPoints
import ru.storozh.extensions.logd
import ru.storozh.models.delivery.database.domains.DishV
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class CategoriesPageViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    val database: RepoDataBase,
    point: EndPoints
) : ProtoViewModel<CategoriesPageViewModel.State>(initState, context, savedStateHandle, point) {
    private val _dishes = MutableStateFlow<PagingData<DishV>>(PagingData.empty())
    val dishes = _dishes.asStateFlow().cachedIn(viewModelScope)

    init {
        state.observeForever {
            logd("Local state changed. Current value = ${state.value}")
            if (it.categoryId.isNotBlank()) {
                updatePagingDataFlow()
            }
        }
    }

    private fun updatePagingDataFlow() {
        logd("invoke updatePagingDataFlow()")
        state.value?.run {
            viewModelScope.launch {
                _dishes.emitAll(
                    Pager(config = config) {
                        database.getDishesPagingSource(
                            categoryId,
                            sortByMode,
                            sortByDirect
                        )
                    }.flow
                )
            }
        }
    }

    fun toggleFavorite(id: String) {
        database.toggleFavoriteDish(id)
    }

    data class State(
        val categoryId: String = "",
        val sortByMode: String = SORT_BY_ALPHABET,
        val sortByDirect: Boolean = false,
        val manualUpdate: Boolean = false
    ) : ProtoViewModelState

    override fun checkState() = true

    companion object {
        private val config = PagingConfig(
            maxSize = 30,
            pageSize = 10,
            prefetchDistance = 10,
            enablePlaceholders = false
        )
    }
}

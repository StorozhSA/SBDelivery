package ru.skillbranch.sbdelivery.ui.categories

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.SORT_BY_ALPHABET
import ru.skillbranch.sbdelivery.data.RepoDataBase
import ru.storozh.common.EndPoints
import ru.storozh.extensions.update
import ru.storozh.models.delivery.database.domains.Category
import ru.storozh.ui.ProtoViewModel
import ru.storozh.ui.ProtoViewModelState
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    private val database: RepoDataBase,
    point: EndPoints,
) : ProtoViewModel<CategoriesViewModel.State>(initState, context, savedStateHandle, point) {

    fun loadCats(parentCategoryId: String = "") {
        if (parentCategoryId.isNotBlank()) {
            viewModelScope.launch {
                database.getFlowCategories(parentCategoryId).collectLatest {
                    state.update { st -> st.copy(categories = it) }
                }
            }
        }
    }

    data class State(
        val currentTab: Int = 0,
        val sortByMode: String = SORT_BY_ALPHABET,
        val sortByDirect: Boolean = false,
        val categories: List<Category> = emptyList()
    ) : ProtoViewModelState

    override fun checkState() = true
}

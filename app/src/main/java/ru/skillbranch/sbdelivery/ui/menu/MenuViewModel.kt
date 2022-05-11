package ru.skillbranch.sbdelivery.ui.menu

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
class MenuViewModel @Inject constructor(
    initState: State,
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle,
    private val dataBase: RepoDataBase,
    point: EndPoints
) : ProtoViewModel<MenuViewModel.State>(initState, context, savedStateHandle, point) {

    fun categories() = dataBase.getFlowCategoriesView()

    data class State(val key: String = "") : ProtoViewModelState

    override fun checkState() = true
}

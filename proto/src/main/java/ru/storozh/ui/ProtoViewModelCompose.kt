package ru.storozh.ui

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.flow.asStateFlow
import ru.storozh.common.*
import ru.storozh.extensions.mutableStateFlow

@Suppress("UNUSED_PARAMETER")
abstract class ProtoViewModelCompose<T : ProtoViewModelState>(
    initState: T,
    context: Context,
    savedStateHandle: SavedStateHandle,
    val point: EndPoints
) : ViewModel() {
    val _state by savedStateHandle.mutableStateFlow(initState, viewModelScope)
    val state = _state.asStateFlow()

    protected val navigation = MutableLiveData<Event<Nav>>()

    open fun navigate(command: Nav) {
        Event(command).also { navigation.value = it }
    }

    fun observeNavigation(owner: LifecycleOwner, onNavigate: (command: Nav) -> Unit) {
        navigation.observe(owner, EventObserverSingle { onNavigate(it) })
    }

    fun notify(n: Notify) {
        point.notify(n)
    }
}









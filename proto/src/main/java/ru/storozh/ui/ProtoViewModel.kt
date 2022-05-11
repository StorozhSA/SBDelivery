package ru.storozh.ui

import android.content.Context
import androidx.lifecycle.*
import ru.storozh.common.*
import ru.storozh.extensions.logd
import ru.storozh.extensions.mediatorLiveData
import ru.storozh.extensions.subscribeStateToDataSource

@Suppress("UNUSED_PARAMETER")
abstract class ProtoViewModel<T : ProtoViewModelState>(
    initState: T,
    context: Context,
    savedStateHandle: SavedStateHandle,
    val point: EndPoints
) : ViewModel() {
    val state: MediatorLiveData<T> by savedStateHandle.mediatorLiveData(initState)

    protected val navigation = MutableLiveData<Event<Nav>>()

    init {
        state.observeForever { logd("State => ${state.value}") }
    }

    open fun navigate(command: Nav) {
        Event(command).also { navigation.value = it }
    }

    fun observeNavigation(owner: LifecycleOwner, onNavigate: (command: Nav) -> Unit) {
        navigation.observe(owner, EventObserverSingle { onNavigate(it) })
    }

    fun notify(n: Notify) {
        point.notify(n)
    }

    abstract fun checkState(): Boolean

    /***
     * Функция принимает источник данных и лямбда выражение обрабатывающее поступающие данные источника
     * лямбда принимает новые данные и текущее состояние ViewModel в качестве аргументов,
     * изменяет его и возвращает модифицированное состояние, которое устанавливается как текущее
     *
     * Пример использования:
     * subscribeOnDataSource(getDataFromDataBaseOrNet()) { info, state ->
     *      info?.let {
     *          state.copy(
     *              isBookmark = it.isBookmark,
     *              isLike = it.isLike
     *          )
     *      }
     *  }
     */
    fun <S> subscribeStateToDataSource(
        source: LiveData<S>,
        onChanged: (newValue: S, currentState: T) -> T
    ) {
        state.subscribeStateToDataSource(source, onChanged)
    }


    /*@SuppressLint("RestrictedApi")
    @Singleton
    class Factory<T : ProtoViewModelState, M:ProtoViewModel<T>> @Inject constructor(
        private val initState: T, @ApplicationContext private val context: Context
    ) : ViewModelAssistedFactory<M> {

        override fun create(handle: SavedStateHandle): M {
            return ProtoViewModel(initState, context, handle) as M
        }
    }*/
}


///**
// * Фабрика с передачей состояния из вне, для Hilt.
// * Пример использования вручную
// * @Inject
// * override lateinit var viewModelFactory: MainViewModel.Factory
// * override val viewModel by viewModels<MainViewModel> { FactoryProducer(viewModelFactory, this) }
// */
//@SuppressLint("RestrictedApi")
//@Singleton
//class Factory @Inject constructor(
//    private val initState: State, @ApplicationContext private val context: Context)
//    : ViewModelAssistedFactory<MainViewModel> {
//
//    override fun create(handle: SavedStateHandle): MainViewModel {
//        return MainViewModel(initState, context, handle)
//    }
//}
//
///**
// * Фабрика без передачи состояния из вне, для Hilt.
// * Пример использования вручную
// * @Inject
// * override lateinit var viewModelFactory: MainViewModel.FactoryNotState
// * override val viewModel by viewModels<MainViewModel> { FactoryProducer(viewModelFactory, this) }
// */
//@SuppressLint("RestrictedApi")
//@Singleton
//class FactoryNotState @Inject constructor(@ApplicationContext private val context: Context) :
//    ViewModelAssistedFactory<MainViewModel> {
//
//    override fun create(handle: SavedStateHandle): MainViewModel {
//        return MainViewModel(context = context, savedStateHandle = handle)
//    }
//}









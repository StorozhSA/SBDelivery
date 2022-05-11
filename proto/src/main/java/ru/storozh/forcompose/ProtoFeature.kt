package ru.storozh.forcompose

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.storozh.extensions.logd
import ru.storozh.forcompose.Feature.*
import kotlin.coroutines.EmptyCoroutineContext

/**
 * ProtoFeature class
 */
abstract class ProtoFeature<S : State, M : Message, EF : Effect, EFH : Handler<EF, M>>(
    initialState: S,
    effectHandler: EFH,
    reducer: Reducer<S, M, EF>,
    private val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
) : Feature<S, M, EF, EFH> {
    // Поток с сообщениями мутации
    private val mutations by lazy { MutableSharedFlow<M>() }

    // Состояние (должен быть доступен для переопределения из вне)
    var state = mutableStateOf(initialState)

    init {
        // Пробрасываем в обработчик ссылку на функцию отправки сообщения мутации
        effectHandler.setMutate(::mutate)
        // Запускаем прослушивание потока сообщений мутации
        scope.launch(Dispatchers.Default) {
            mutations.onEach { logd("MUTATION ${it.javaClass.simpleName}") }.collect {
                reducer.reduce(it, state.value).let { (first, second) ->
                    state.value = first
                    second.forEach { eff ->
                        logd("EFF ${eff.javaClass.simpleName}")
                        launch { effectHandler.handle(eff) }
                    }
                }
            }
        }
    }

    override fun mutate(msg: M) = scope.launch(Dispatchers.Default) { mutations.emit(msg) }
}
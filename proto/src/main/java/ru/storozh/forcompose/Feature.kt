package ru.storozh.forcompose

import androidx.navigation.NavController
import kotlinx.coroutines.Job
import ru.storozh.common.Nav
import ru.storozh.forcompose.Feature.*
import java.io.Serializable

/**
 * Feature interface
 */
interface Feature<S : State, M : Message, EF : Effect, EFH : Handler<EF, M>> {
    fun mutate(msg: M): Job

    /**
     * Интерфейс Message. Описывает формат сообщений мутации.
     */
    interface Message

    /**
     * Интерфейс Effect. Описывает формат эффектов в рамках ELM.
     */
    interface Effect

    /**
     * Интерфейс Reducer. Описывает редьюсер в рамках ELM.
     */
    interface Reducer<S, M, EF> {
        fun reduce(msg: M, state: S): Pair<S, Set<EF>>
    }

    /**
     * Интерфейс Handler. Описывает обработчие эффектов в рамках ELM.
     */
    interface Handler<EF, M> {
        suspend fun handle(effect: EF)
        fun setMutate(commit: (M) -> Unit)
        fun setNavController(nc: NavController)
        fun cancelChildrenJobs()
        fun navigateByCommand(command: Nav)
    }

    /**
     * Интерфейс State. Описывает состояние графического интерфейса соспоставленного с данной фичей.
     */
    interface State : Serializable {
        companion object {
            private const val serialVersionUID = 21000000000001L
        }
    }
}
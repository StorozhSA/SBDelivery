package ru.storozh.forcompose

import androidx.navigation.NavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import ru.storozh.common.Nav
import ru.storozh.extensions.loge

/**
 * ProtoHandler class
 */
abstract class ProtoHandler<EF, M> : Feature.Handler<EF, M> {
    val parentJob: Job by lazy { SupervisorJob() }
    lateinit var nc: NavController
    lateinit var commit: (M) -> Unit
    override fun setMutate(commit: (M) -> Unit) {
        this.commit = commit
    }

    override fun setNavController(nc: NavController) {
        this.nc = nc
    }

    override fun cancelChildrenJobs() {
        loge("Start cancelled all jobs")
        parentJob.cancelChildren()
    }

    abstract override suspend fun handle(effect: EF)

    override fun navigateByCommand(command: Nav) {
        when (command) {
            is Nav.To -> {
                nc.navigate(
                    command.destination,
                    command.args,
                    command.options,
                    command.extras
                )
            }
            is Nav.Direction -> {
                nc.navigate(command.directions)
            }
            is Nav.PopBackStack -> TODO()
        }
    }
}
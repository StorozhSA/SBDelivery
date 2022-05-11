package ru.storozh.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun LifecycleOwner.launchRepeatOnStarted(block: suspend CoroutineScope.() -> Unit): Job {
    return this.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            block.invoke(this)
        }
    }
}

fun LifecycleOwner.launchRepeatOnCreated(block: suspend CoroutineScope.() -> Unit): Job {
    return this.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            block.invoke(this)
        }
    }
}

fun LifecycleOwner.launchRepeatOnResumed(block: suspend CoroutineScope.() -> Unit): Job {
    return this.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            block.invoke(this)
        }
    }
}

fun LifecycleOwner.launchRepeatOnDestroyed(block: suspend CoroutineScope.() -> Unit): Job {
    return this.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.DESTROYED) {
            block.invoke(this)
        }
    }
}

fun LifecycleOwner.launchRepeatOnInitialized(block: suspend CoroutineScope.() -> Unit): Job {
    return this.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.INITIALIZED) {
            block.invoke(this)
        }
    }
}

/*
fun Lifecycle.launchRepeatOnStarted(block: suspend CoroutineScope.() -> Unit): Job {
    return this.coroutineScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            block.invoke(this)
        }
    }
}*/

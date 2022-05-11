package ru.storozh.common

import android.os.Bundle
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

sealed class Nav {
    data class To(
        val destination: Int,
        val args: Bundle? = null,
        val options: NavOptions? = null,
        val extras: Navigator.Extras? = null
    ) : Nav()

    data class Direction(
        val directions: NavDirections
    ) : Nav()

    data class PopBackStack(
        val destination: Int = 0,
        val inclusive: Boolean = false
    ) : Nav()

}

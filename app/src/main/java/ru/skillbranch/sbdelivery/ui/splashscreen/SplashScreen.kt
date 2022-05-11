package ru.skillbranch.sbdelivery.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //region Full screen
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        //endregion

        //region Delayed transition
        lifecycleScope.launch {
            delay(2000L)
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            finish()
        }
        //endregion
    }

    /*@SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //region Full screen
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        //endregion

        installSplashScreen().apply {
            setOnExitAnimationListener { view ->
                view.iconView.let { icon ->

                    val animator = ValueAnimator
                        .ofInt(icon.height, 0)
                        .setDuration(3000)
                        .apply {
                            addUpdateListener {
                                val value = it.animatedValue as Int
                                icon.layoutParams.width = (value * 1.44).toInt()
                                icon.layoutParams.height = value
                                icon.requestLayout()

                                if (value == 0) {
                                    startActivity(
                                        Intent(
                                            this@SplashScreen,
                                            MainActivity::class.java
                                        )
                                    )
                                }
                            }
                        }

                    AnimatorSet().apply {
                        interpolator = AccelerateDecelerateInterpolator()
                        play(animator)
                        start()
                    }
                }
            }
        }


    }*/
}

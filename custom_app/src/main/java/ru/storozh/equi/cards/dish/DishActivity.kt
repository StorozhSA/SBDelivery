package ru.storozh.equi.cards.dish

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.storozh.equi.R
import ru.storozh.equi.databinding.ActivityDishBinding
import ru.storozh.extensions.viewBinding

class DishActivity : AppCompatActivity() {
    private val bv by viewBinding(ActivityDishBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dish)
        bv.cardDish.registerLifecycle(this.lifecycle)
        //bv.cardDish.start()
    }
}

package ru.storozh.custom.cards.dish

import android.graphics.Rect

interface RoundedRectShadowOwner {
    fun cardBackgroundRect(): Rect

    fun cardCornerRadius(): Float
}
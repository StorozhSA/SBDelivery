package ru.storozh.custom.cards.dish

import android.annotation.TargetApi
import android.graphics.Outline
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class RoundedRectViewOutlineProvider : ViewOutlineProvider() {
    override fun getOutline(view: View?, outline: Outline?) {
        val shadowOwnerView: RoundedRectShadowOwner = view as RoundedRectShadowOwner
        val rect = shadowOwnerView.cardBackgroundRect()
        outline?.setRoundRect(rect, shadowOwnerView.cardCornerRadius())
    }
}
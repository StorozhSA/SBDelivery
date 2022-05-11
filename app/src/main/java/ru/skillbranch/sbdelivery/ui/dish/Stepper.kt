package ru.skillbranch.sbdelivery.ui.dish

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.InternalCoroutinesApi
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.ui.common.AppTheme

@InternalCoroutinesApi
@Composable
fun Stepper(
    amount: Int,
    modifier: Modifier = Modifier,
    state: DishFeature.State,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .border(
                0.dp,
                MaterialTheme.colors.onBackground,
                shape = RoundedCornerShape(4.dp)
            )
            .clip(RoundedCornerShape(4.dp))
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (amount > 1) {
            IconButton(
                enabled = !state.isDecrementInProcess,
                onClick = { onDecrement() },
                content = {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colors.secondary,
                        painter = painterResource(R.drawable.ic_baseline_remove_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .width(30.dp)
                    .fillMaxHeight()
                    .border(
                        0.dp,
                        MaterialTheme.colors.onBackground
                    )
                    .clipToBounds()
            )
        }

        Text(
            text = "$amount",
            fontSize = 24.sp,
            style = TextStyle(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colors.secondary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        )
        IconButton(
            enabled = !state.isIncrementInProcess,
            onClick = { onIncrement() },
            content = {
                Icon(
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colors.secondary,
                    painter = painterResource(R.drawable.ic_baseline_add_24),
                    contentDescription = null
                )
            },
            modifier = Modifier
                .width(30.dp)
                .fillMaxHeight()
                .border(
                    0.dp,
                    MaterialTheme.colors.onBackground
                )
                .clipToBounds()
        )
    }
}

@InternalCoroutinesApi
@Preview
@Composable
fun StepperPreview() {
    AppTheme {
        Stepper(amount = 10, state = DishFeature.State(), onDecrement = {}, onIncrement = {})
    }
}

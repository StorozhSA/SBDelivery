package ru.skillbranch.sbdelivery.ui.cart

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skillbranch.sbdelivery.R

@Composable
fun Stepper(
    value: Int,
    modifier: Modifier = Modifier,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier
            .height(48.dp)
            .animateContentSize()
    ) {
        Row(
            modifier = modifier
                .height(48.dp)
                .border(
                    0.dp,
                    MaterialTheme.colors.onBackground,
                    shape = RoundedCornerShape(4.dp)
                )
                .clip(RoundedCornerShape(4.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (value > 1) {
                IconButton(
                    onClick = { onDecrement.invoke() },
                    content = {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colors.secondary,
                            painter = painterResource(R.drawable.ic_baseline_remove_24),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .width(48.dp)
                        .fillMaxHeight()
                        .border(
                            0.dp,
                            MaterialTheme.colors.onBackground
                        )
                        .clipToBounds()
                )
            }

            Text(
                text = "$value",
                fontSize = 18.sp,
                style = TextStyle(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
            IconButton(
                onClick = { onIncrement.invoke() },
                content = {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colors.secondary,
                        painter = painterResource(R.drawable.ic_baseline_add_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight()
                    .border(
                        0.dp,
                        MaterialTheme.colors.onBackground
                    )
                    .clipToBounds()
            )
        }

        if (value == 1) {
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = { onRemove.invoke() },
                content = {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colors.secondary,
                        painter = painterResource(R.drawable.ic_baseline_delete_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .width(48.dp)
                    .border(
                        0.dp,
                        MaterialTheme.colors.onBackground,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

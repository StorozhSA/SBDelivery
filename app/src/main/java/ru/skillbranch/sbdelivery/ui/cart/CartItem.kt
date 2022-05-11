package ru.skillbranch.sbdelivery.ui.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import kotlinx.coroutines.InternalCoroutinesApi
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.ui.common.AppTheme
import ru.storozh.models.delivery.database.domains.CartItemJoined

@ExperimentalCoilApi
@InternalCoroutinesApi
@Composable
fun CartItem(
    dish: CartItemJoined,
    onProductClick: (dishId: String, title: String, amount: Int) -> Unit,
    onIncrement: (dishId: String) -> Unit,
    onDecrement: (dishId: String) -> Unit,
    onRemove: (dishId: String, title: String) -> Unit
) {
    ConstraintLayout {
        val (title, poster, stepper, price) = createRefs()
        val painter = rememberImagePainter(
            data = dish.image,
            builder = {
                crossfade(true)
                placeholder(R.drawable.ic_empty_dish)
                error(R.drawable.ic_empty_dish)
            }
        )

        Image(
            painter = painter,
            contentDescription = dish.name,
            contentScale = if (painter.state is ImagePainter.State.Success) ContentScale.Fit else ContentScale.Inside,
            modifier = Modifier
                .height(100.dp)
                .aspectRatio(1f)
                .clickable { onProductClick(dish.id, dish.name, dish.amount) }
                .constrainAs(poster) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                }
                .clip(RoundedCornerShape(16.dp))
        )
        Text(
            fontSize = 16.sp,
            color = MaterialTheme.colors.onPrimary,
            style = TextStyle(fontWeight = FontWeight.Bold),
            text = dish.name,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(title) {
                    top.linkTo(poster.top)
                    start.linkTo(stepper.start)
                    width = Dimension.preferredWrapContent
                }
        )
        Stepper(
            value = dish.amount,
            onIncrement = { onIncrement(dish.id) },
            onDecrement = { onDecrement(dish.id) },
            onRemove = { onRemove(dish.id, dish.name) },
            modifier = Modifier
                .constrainAs(stepper) {
                    start.linkTo(poster.end, margin = 16.dp)
                    bottom.linkTo(poster.bottom)
                }
        )
        Text(
            fontSize = 18.sp,
            color = MaterialTheme.colors.secondary,
            text = "${dish.price} Р",
            textAlign = TextAlign.End,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(price) {
                    top.linkTo(stepper.top)
                    bottom.linkTo(stepper.bottom)
                    start.linkTo(stepper.end, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.preferredWrapContent
                }
        )
    }
}

@InternalCoroutinesApi
@Preview
@Composable
fun CartItemPreview() {
    AppTheme {
        CartItem(
            dish = CartItemJoined("555", 1, 34, "Pizza", ""),
            onProductClick = { _, _, _ -> },
            onIncrement = {},
            onDecrement = {},
            onRemove = { _, _ -> }
        )
    }
}

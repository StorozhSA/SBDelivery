package ru.skillbranch.sbdelivery.ui.dish

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import kotlinx.coroutines.InternalCoroutinesApi
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.ui.common.AppTheme
import ru.storozh.models.delivery.database.domains.DishV

@ExperimentalCoilApi
@InternalCoroutinesApi
@Composable
fun DishContent(feature: DishFeature, dish: DishV) {
    feature.state = rememberSaveable { feature.state }
    DishAssembled(dish = dish, state = feature.state.value, accept = feature::mutate)
}

@ExperimentalCoilApi
@InternalCoroutinesApi
@Composable
fun DishAssembled(
    dish: DishV,
    state: DishFeature.State,
    accept: (DishFeature.Msg) -> Unit
) {
    if (state.isLoading()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {
            CircularProgressIndicator(color = MaterialTheme.colors.secondary)
        }
    }

    ConstraintLayout(modifier = Modifier.verticalScroll(rememberScrollState())) {

        val (title, poster, like, action, description, price, addBtn, reviewBox) = createRefs()

        Image(
            painter = rememberImagePainter(
                data = dish.image,
                builder = { placeholder(R.drawable.ic_empty_dish) }
            ),
            contentDescription = dish.description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1.44f)
                .fillMaxSize()
                .constrainAs(poster) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        dish.oldPrice?.let {
            if (it > 0) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .zIndex(1f)
                        .size(80.dp, 40.dp)
                        .background(
                            color = colorResource(R.color.color_yellow),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .constrainAs(action) {
                            top.linkTo(poster.top, margin = 10.dp)
                            start.linkTo(poster.start, margin = 10.dp)
                        }
                ) {
                    Text(
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.primaryVariant,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        text = stringResource(R.string.label_action)

                    )
                }
            }
        }

        IconButton(
            enabled = !state.isToggleLikeInProcess,
            onClick = { accept(DishFeature.Msg.ToggleLike(dishId = dish.id)) },
            /* onClick = { accept(DishFeature.Msg.CancelCommonJob) },*/
            content = {
                Icon(
                    modifier = Modifier.size(40.dp),
                    tint = (
                            if (state.isFavorite) {
                                MaterialTheme.colors.secondary
                            } else {
                                MaterialTheme.colors.secondaryVariant
                            }
                            ),
                    painter = painterResource(R.drawable.ic_favorite_off),
                    contentDescription = null
                )
            },
            modifier = Modifier
                .zIndex(1f)
                .constrainAs(like) {
                    top.linkTo(poster.top, margin = 10.dp)
                    end.linkTo(poster.end, margin = 10.dp)
                }
        )

        Text(
            fontSize = 24.sp,
            color = MaterialTheme.colors.onPrimary,
            style = TextStyle(fontWeight = FontWeight.Bold),
            text = dish.name,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(title) {
                    top.linkTo(poster.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.preferredWrapContent
                }

        )
        Text(
            fontSize = 14.sp,
            color = MaterialTheme.colors.onBackground,
            text = dish.description ?: "",
            style = TextStyle(fontWeight = FontWeight.ExtraLight),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(description) {
                    top.linkTo(title.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.preferredWrapContent
                }
        )

        DishPrice(
            price = dish.price, oldPrice = dish.oldPrice,
            amount = state.amount,
            onIncrement = { accept(DishFeature.Msg.Increment(dish.id)) },
            onDecrement = { accept(DishFeature.Msg.Decrement(dish.id)) },
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .constrainAs(price) {
                    top.linkTo(description.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        TextButton(
            enabled = !state.isBasketAddInProcess,
            onClick = { accept(DishFeature.Msg.AddToBasket) },
            /*onClick = { accept(DishFeature.Msg.Test1) },*/
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .constrainAs(addBtn) {
                    top.linkTo(price.bottom, margin = 32.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    bottom.linkTo(reviewBox.top, margin = 16.dp)
                    width = Dimension.preferredWrapContent
                }
        ) {
            Text(
                "Добавить в корзину ${if (state.amount > 0) "(${state.amount})" else ""}",
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
        }

        // Если отзывы загружались отображаем или грузим сперва
        if (state.isReviewLoaded) {
            Reviews(
                reviews = state.reviews,
                rating = state.rating,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(reviewBox) {
                        top.linkTo(addBtn.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                        width = Dimension.preferredWrapContent
                    }
            ) { accept(DishFeature.Msg.ReviewDialogOpen(dishId = dish.id)) }
        } else {
            if (!state.isReviewLoadingInProcess) {
                accept(DishFeature.Msg.UpdateReviews(dishId = dish.id))
            }
        }
    }

    if (state.isReviewDialogShow) DishReviewDialog(dishId = dish.id, accept)
}

@InternalCoroutinesApi
@Composable
fun DishPrice(
    price: Int,
    modifier: Modifier = Modifier,
    amount: Int = 1,
    oldPrice: Int? = null,
    fontSize: Int = 24,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        if (oldPrice != null) {
            Text(
                text = "${oldPrice * amount} Р",
                color = MaterialTheme.colors.onPrimary,
                textDecoration = TextDecoration.LineThrough,
                style = TextStyle(fontWeight = FontWeight.ExtraLight),
                fontSize = fontSize.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = "${price * amount} Р",
            color = MaterialTheme.colors.secondary,
            style = TextStyle(fontWeight = FontWeight.Bold),
            fontSize = fontSize.sp
        )
        Spacer(
            modifier = Modifier
                .defaultMinSize(minWidth = 16.dp)
                .weight(1f)
        )
        Stepper(
            amount = amount,
            state = DishFeature.State(),
            onIncrement = onIncrement,
            onDecrement = onDecrement
        )
    }
}

@InternalCoroutinesApi
@Preview
@Composable
fun PricePreview() {
    AppTheme {
        DishPrice(60, oldPrice = 100, amount = 5, onDecrement = {}, onIncrement = {})
    }
}

@ExperimentalCoilApi
@SuppressLint("UnrememberedMutableState")
@InternalCoroutinesApi
@Preview
@Composable
fun ContentPreview() {
    AppTheme {
        DishAssembled(
            dish = DishV(
                id = "0",
                image = "https://www.delivery-club.ru/media/cms/relation_product/32350/312372888_m650.jpg",
                name = "Бургер \"Америка\"",
                description = "320 г • Котлета из 100% говядины (прожарка medium) на гриле, картофельная булочка на гриле, фирменный соус, лист салата, томат, маринованный лук, жареный бекон, сыр чеддер.",
                oldPrice = 100,
                price = 200,
                updatedAt = 0,
                rating = 5.0,
                likes = 34,
                createdAt = 0,
                commentsCount = 23,
                category = "root",
                active = true,
                favorite = true
            ),
            state = DishFeature.State()
        ) {}
    }
}

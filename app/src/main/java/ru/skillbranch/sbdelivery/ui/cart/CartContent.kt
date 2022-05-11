package ru.skillbranch.sbdelivery.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import kotlinx.coroutines.InternalCoroutinesApi
import ru.skillbranch.sbdelivery.ui.cart.logic.CartFeature
import ru.skillbranch.sbdelivery.ui.cart.logic.CartFeature.Msg

@ExperimentalCoilApi
@InternalCoroutinesApi
@Composable
fun CartContent(feature: CartFeature) {
    feature.state = rememberSaveable { feature.state }
    CartScreen(state = feature.state.value, accept = feature::mutate)
}

@InternalCoroutinesApi
@ExperimentalCoilApi
@Composable
fun CartScreen(state: CartFeature.State, accept: (Msg) -> Unit) {
    when (state.list) {
        is CartFeature.StateUi.Value -> {
            Column() {
                LazyColumn(
                    contentPadding = PaddingValues(0.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    content = {
                        items(items = state.list.dishes, key = { it.id }) {
                            CartItem(
                                it,
                                onProductClick = { dishId: String, title: String, amount: Int ->
                                    accept(Msg.ClickOnDish(dishId, title, amount))
                                },
                                onIncrement = { dishId -> accept(Msg.IncrementCount(dishId)) },
                                onDecrement = { dishId -> accept(Msg.DecrementCount(dishId)) },
                                onRemove = { dishId, title ->
                                    accept(
                                        Msg.ShowConfirm(
                                            dishId,
                                            title
                                        )
                                    )
                                }
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row() {
                        val total = state.list.dishes.sumBy { it.amount * it.price }
                        Text(
                            "Итого",
                            fontSize = 24.sp,
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colors.onPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "$total Р",
                            fontSize = 24.sp,
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colors.secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val order = state.list.dishes.map { it.id to it.amount }.toMap()
                            accept(Msg.SendOrder(order))
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary,
                            contentColor = MaterialTheme.colors.onSecondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Оформить заказ", style = TextStyle(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
        is CartFeature.StateUi.Empty -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Пока ничего нет")
        }

        is CartFeature.StateUi.Loading -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(color = MaterialTheme.colors.secondary)
        }
    }

    if (state.isLoading()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(color = MaterialTheme.colors.secondary)
        }
    }

    if (state.confirmDialog is CartFeature.StateConfirmDialog.Show) {
        AlertDialog(
            onDismissRequest = { accept(Msg.HideConfirm) },
            backgroundColor = Color.White,
            contentColor = MaterialTheme.colors.primary,
            title = { Text(text = "Вы уверены?") },
            text = { Text(text = "Вы точно хотите удалить ${state.confirmDialog.title} из корзины") },
            buttons = {
                Row {
                    TextButton(
                        onClick = { accept(Msg.HideConfirm) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Нет", color = MaterialTheme.colors.secondary)
                    }
                    TextButton(
                        onClick = { accept(Msg.RemoveFromCart(state.confirmDialog.id)) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Да", color = MaterialTheme.colors.secondary)
                    }
                }
            }
        )
    }
}

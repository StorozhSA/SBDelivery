package ru.skillbranch.sbdelivery.ui.dish

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.InternalCoroutinesApi
import ru.skillbranch.sbdelivery.ui.common.AppTheme

/*@Composable
fun DishReviewsDialog(showDialog: Boolean, setShowDialog: (Boolean) -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Title") },
            confirmButton = {
                Button(onClick = { setShowDialog(false) }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { setShowDialog(false) }) {
                    Text("Dismiss")
                }
            },
            text = { Text("This is a text on the dialog") }
        )
    }
}

@Composable
fun ComposeDialogDemo() {
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { setShowDialog(true) }) {
            Text("Show Dialog")
        }
        DishReviewsDialog(showDialog, setShowDialog)
    }
}*/

@InternalCoroutinesApi
@Composable
fun DishReviewDialog(dishId: String, accept: (DishFeature.Msg) -> Unit) {

    var rating by rememberSaveable { mutableStateOf(0) }
    var review by rememberSaveable { mutableStateOf("") }

    Dialog(onDismissRequest = { /*accept(DishFeature.Msg.HideReviewDialog)*/ }) {
        Surface(shape = MaterialTheme.shapes.medium, color = Color.White) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Оставить отзыв",
                        color = MaterialTheme.colors.primary,
                        fontSize = 18.sp,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { accept(DishFeature.Msg.ReviewDialogClose) },
                        modifier = Modifier.size(18.dp)
                    ) {
                        Icon(
                            tint = MaterialTheme.colors.onBackground,
                            painter = painterResource(ru.skillbranch.sbdelivery.R.drawable.ic_baseline_close_24),
                            contentDescription = null
                        )
                    }
                }
                RatingBar(
                    value = rating,
                    onChoose = { rating = it },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = review,
                    onValueChange = { review = it },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colors.primary,
                        focusedBorderColor = MaterialTheme.colors.secondary,
                        unfocusedBorderColor = MaterialTheme.colors.onBackground
                    ),
                    placeholder = {
                        Text(
                            text = "Напишите отзыв",
                            color = MaterialTheme.colors.onBackground
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = { accept(DishFeature.Msg.SendReview(dishId, rating, review)) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                        contentColor = MaterialTheme.colors.onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Оставить отзыв",
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun RatingBar(value: Int = 0, maxValue: Int = 5, onChoose: (Int) -> Unit, modifier: Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        for (item in 0..maxValue.dec()) {
            IconButton(
                onClick = { onChoose(item.inc()) }
            ) {
                Icon(
                    tint = MaterialTheme.colors.secondary,
                    painter = painterResource(
                        if (item.inc() <= value) {
                            ru.skillbranch.sbdelivery.R.drawable.ic_baseline_star
                        } else {
                            ru.skillbranch.sbdelivery.R.drawable.ic_baseline_star_border
                        }
                    ),
                    contentDescription = null
                )
            }
        }
    }
}

@InternalCoroutinesApi
@Preview
@Composable
fun DishReviewDialogPreview() {
    AppTheme {
        DishReviewDialog("43354535", accept = {})
    }
}

@Preview
@Composable
fun RatingBarPreview() {
    AppTheme {
        RatingBar(value = 1, onChoose = {}, modifier = Modifier.fillMaxWidth())
    }
}

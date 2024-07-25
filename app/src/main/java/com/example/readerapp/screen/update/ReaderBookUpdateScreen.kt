package com.example.readerapp.screen.update

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.readerapp.R
import com.example.readerapp.components.InputField
import com.example.readerapp.components.RatingBar
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.components.RoundedButton
import com.example.readerapp.components.showToast
import com.example.readerapp.data.DataOrException
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreen
import com.example.readerapp.screen.home.HomeScreenViewModel
import com.example.readerapp.utils.formatDate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

@ExperimentalComposeApi
@Composable
fun BookUpdateScreen(
    navController: NavHostController,
    bookItemId: String,
    viewModel: HomeScreenViewModel = hiltViewModel()

) {
    Scaffold(topBar = {
        ReaderAppBar(
            title = "Update Book",
            icon = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController
        ) { navController.popBackStack() }
    }) { innerPadding ->
        val bookInfo =
            produceState<DataOrException<List<MBook>, Boolean, Exception>>(
                initialValue = DataOrException(
                    data = emptyList(),
                    true,
                    Exception("")
                )
            ) {
                value = viewModel.data.value
            }.value
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(3.dp)
        ) {
            Column(
                modifier = Modifier.padding(top = 3.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (bookInfo.loading == true) {
                    LinearProgressIndicator()
                    bookInfo.loading = false
                } else {
                    Surface(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth(),
                        shape = CircleShape,
                        tonalElevation = 4.dp
                    ) {
                        ShowBookUpdate(bookInfo = viewModel.data.value, bookItemId = bookItemId)

                    }
                    ShowSimpleForm(book = viewModel.data.value.data?.first { mBook ->
                        mBook.googleBookId == bookItemId
                    }!!, navController)
                }

            }

        }

    }

}

@Composable
fun ShowSimpleForm(book: MBook, navController: NavHostController) {
    val noteText = remember {
        mutableStateOf("")
    }

    val isStartedReading = remember {
        mutableStateOf(false)
    }

    val isFinishedReading = remember {
        mutableStateOf(false)
    }

    val ratingVal = remember {
        mutableStateOf(0)
    }
    val context = LocalContext.current

    SimpleForm(
        defaultValue = if (book.notes.toString().isNotEmpty()) {
            book.notes.toString()
        } else {
            "No thoughts available."
        }
    ) { note ->
        noteText.value = note
    }

    Row(
        modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TextButton(onClick = {
            isStartedReading.value = true
        }, enabled = book.startedReading == null) {
            if (book.startedReading == null) {
                if (!isStartedReading.value) {
                    Text(text = "Start Reading")
                } else {
                    Text(
                        text = "Started Reading",
                        modifier = Modifier.alpha(0.8f),
                        color = Color.Red.copy(alpha = 0.6f)
                    )
                }
            } else {
                Text(text = "Started On: ${formatDate(book.startedReading!!)}")  // TODO format date
            }

        }
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(onClick = {
            isFinishedReading.value = true
        }, enabled = book.finishedReading == null) {

            if (book.finishedReading == null) {
                if (!isFinishedReading.value) {
                    Text(text = "Mark as Read")
                } else {
                    Text(text = "Finished Reading!!!!")
                }
            } else {
                Text(text = "Finished On: ${formatDate(book.finishedReading!!)}")  // TODO format
            }

        }

    }

    Text(text = "Rating", modifier = Modifier.padding(bottom = 3.dp))
    book.rating?.toInt().let {
        RatingBar(rating = it!!) { rating ->
            ratingVal.value = rating

        }
    }
    Spacer(modifier = Modifier.padding(bottom = 15.dp))
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        val changedNotes = book.notes != noteText.value
        val changedRating = book.rating?.toInt() != ratingVal.value
        val isFinishedTimeStamp =
            if (isFinishedReading.value) Timestamp.now() else book.finishedReading
        val isStartedTimeStamp =
            if (isStartedReading.value) Timestamp.now() else book.startedReading

        val bookUpdate =
            changedNotes || changedRating || isStartedReading.value || isFinishedReading.value

        val bookToUpdate = hashMapOf(
            "finished_reading_at" to isFinishedTimeStamp,
            "started_reading_at" to isStartedTimeStamp,
            "rating" to ratingVal.value,
            "notes" to noteText.value
        ).toMap()
        RoundedButton(label = "Update") {
            if (bookUpdate) {
                FirebaseFirestore.getInstance().collection("books").document(book.id!!)
                    .update(bookToUpdate)
                    .addOnCompleteListener {
                        showToast(context, "Book updated SuccessFully")
                        navController.navigate(ReaderScreen.ReaderHomeScreen.name)
                    }
                    .addOnFailureListener {

                    }
            }


        }
        Spacer(modifier = Modifier.width(80.dp))
        val openDialog = remember {
            mutableStateOf(false)
        }
        if (openDialog.value) {
            ShowAlertDialog(
                message = stringResource(id = R.string.sure) + "\n" + stringResource(id = R.string.action),
                openDialog
            ) {
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book.id!!)
                    .delete()
                    .addOnSuccessListener {
                        //  if (it.isSuccessful) {
                        openDialog.value = false
                        showToast(context, "Book Deleted")
                        /*don't popbackStack() is we  want to immediate recompostion of the mainScreen
                        Ui, instead of navigation to tha mainScreen!*/
                        navController.navigate(ReaderScreen.ReaderHomeScreen.name)
                        //   }
                    }
            }
        }
        RoundedButton(label = "Delete") {
            openDialog.value = true

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAlertDialog(
    message: String,
    openDialog: MutableState<Boolean>,
    onYesPressed: () -> Unit
) {

    if (openDialog.value) {
        AlertDialog(onDismissRequest = { openDialog.value = false },
            title = { Text(text = "Delete Book") },
            text = { Text(text = message) },
            confirmButton = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { onYesPressed.invoke() }) {
                        Text(text = "Yes")
                    }
                    TextButton(onClick = { openDialog.value = false }) {
                        Text(text = "No")

                    }

                }
            })
    }
}

@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue: String = "Great Book!",
    onSearch: (String) -> Unit = {}
) {

    Column() {
        val textfieldValue = rememberSaveable {
            mutableStateOf(defaultValue)
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(textfieldValue.value) {
            textfieldValue.value.trim().isNotEmpty()
        }
        InputField(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(4.dp)
                .background(
                    Color.Unspecified,
                    CircleShape
                )
                .padding(horizontal = 20.dp, vertical = 12.dp),
            valueState = textfieldValue,
            labelId = "Enter Your Thoughts",
            enabled = true,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(textfieldValue.value.trim())
                keyboardController?.hide()

            }
        )
    }
}
//@Composable
//fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>, Boolean, Exception>, bookItemId: String) {
//    Row {
//        Spacer(modifier = Modifier.width(43.dp))
//        bookInfo.data?.let { books ->
//            val book = books.firstOrNull { it.googleBookId == bookItemId }
//            if (book != null) {
//                Column(
//                    modifier = Modifier.padding(4.dp),
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    CardListItem(book = book, onPressDetails = {})
//                }
//            } else {
//                Text("Book not found", modifier = Modifier.padding(4.dp))
//            }
//        } ?: run {
//            Text("No data available", modifier = Modifier.padding(4.dp))
//        }
//    }
//}

@Composable
fun ShowBookUpdate(
    bookInfo: DataOrException<List<MBook>, Boolean, Exception>,
    bookItemId: String
) {

    Row() {
        Spacer(modifier = Modifier.width(43.dp))
        if (bookInfo.data != null) {
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center
            ) {

                CardListItem(book = bookInfo.data!!.first { mBook ->
                    mBook.googleBookId == bookItemId
                }, onPressDetails = {})
            }
        }
    }
}

@Composable
fun CardListItem(
    book: MBook,
    onPressDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(
                start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { },
        // elevation = 8.dp
    ) {
        Row(horizontalArrangement = Arrangement.Start) {
            Image(
                painter = rememberImagePainter(data = book.photoUrl.toString()),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 90.dp,
                            topEnd = 20.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp
                        )
                    )
            )
            Column {
                Text(
                    text = book.title.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.author.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 2.dp,
                        bottom = 0.dp
                    )
                )

                Text(
                    text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 0.dp,
                        bottom = 8.dp
                    )
                )

            }

        }


    }

}

//@Composable
//fun CardListItem(book: MBook, onPressDetails: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp)
//            .clip(
//                RoundedCornerShape(20.dp)
//            )
//            .clickable { },
//        elevation = CardDefaults.elevatedCardElevation(8.dp)
//    ) {
//
//        Image(
//            painter = rememberImagePainter(data = book.photoUrl.toString()),
//            contentDescription = null,
//            modifier = Modifier
//                .height(100.dp)
//                .width(120.dp)
//                .padding(4.dp)
//                .clip(
//                    RoundedCornerShape(
//                        topEnd = 20.dp,
//                        bottomEnd = 0.dp,
//                        bottomStart = 0.dp,
//                        topStart = 120.dp
//                    )
//                )
//        )
//        Column {
//            Text(
//                text = book.title.toString(),
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier
//                    .padding(start = 8.dp, end = 8.dp)
//                    .width(120.dp),
//                fontWeight = FontWeight.SemiBold,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis
//            )
//            Text(text = book.author.toString(),
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier.padding(start = 8.dp,
//                    end = 8.dp,
//                    top = 2.dp,
//                    bottom = 0.dp))
//            Text(
//                text = book.publishedDate.toString(),
//                style = MaterialTheme.typography.titleSmall,
//                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 1.dp, bottom = 8.dp)
//            )
//        }
//
//    }
//
//
//}





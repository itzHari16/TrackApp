package com.example.readerapp.screen.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.readerapp.components.ListCard
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.components.TitleSection
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreen
import com.google.firebase.auth.FirebaseAuth

//@Preview
@Composable
fun ReaderHomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            ReaderAppBar(title = "H. Reader", navController = navController, showProfile = false)
        },
        floatingActionButton = {
            FABcontent { navController.navigate(ReaderScreen.SearchScreen.name) }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            HomeContent(navController, viewModel)
        }
    }

}

@Composable
fun HomeContent(navController: NavController, viewModel: HomeScreenViewModel) {

//    val listOfBooks= listOf(
//        MBook(id = "asdf", title = "Hello Again", author = "All of us", notes = null),
//        MBook(id = "asdf", title = "Hello Again", author = "All of us", notes = null),
//        MBook(id = "asdf", title = "Hello Again", author = "All of us", notes = null),
//        MBook(id = "asdf", title = "Hello Again", author = "All of us", notes = null)
//    )

    var listofBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (!viewModel.data.value.data.isNullOrEmpty()) {
        listofBooks = viewModel.data.value.data?.toList()!!.filter { mBook ->
            mBook.userId == currentUser?.uid.toString()
        }
        Log.d("Bookd", "HomeContent: $listofBooks")

    }
    //  val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = if (!FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
        FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0)
    } else {
        "N/A"
    }


    Column(modifier = Modifier.padding(2.dp), verticalArrangement = Arrangement.Top) {
        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
            TitleSection(label = "You reading \n" + "activity right now")
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            Column {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ReaderScreen.ReaderStatsScreen.name)
                        }
                        .size(45.dp),
                    tint = MaterialTheme.colorScheme.secondaryContainer
                )
                Text(
                    text = currentUserName!!,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
                Divider()

            }
        }
        ReadingRightNow(listofbooks = listofBooks, navController = navController)

        TitleSection(label = "Reading List")

        BookListArea(listofbooks = listofBooks, navController = navController)
    }

}

@Composable
fun BookListArea(listofbooks: List<MBook>, navController: NavController) {

    val addedBooks = listofbooks.filter { mBook ->
        mBook.startedReading == null && mBook.finishedReading == null
    }
    HorizontalScrollableComponent(addedBooks) {
        navController.navigate(ReaderScreen.UpdateScreen.name + "/$it")
    }
}

@Composable
fun HorizontalScrollableComponent(
    listofbooks: List<MBook>,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onCardPressed: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState)
    ) {
        if (viewModel.data.value.loading == true) {
            LinearProgressIndicator()
        } else {
            if (listofbooks.isNullOrEmpty()) {
                Surface(modifier = Modifier.padding(23.dp)) {
                    Text(
                        text = "No books Found . Add a book", style = TextStyle(
                            color = Color.Red.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                }
            } else {
                for (book in listofbooks) {
                    ListCard(book) {
                        onCardPressed(book.googleBookId.toString())


                    }
                }

            }
        }
    }

}


@Composable
fun ReadingRightNow(listofbooks: List<MBook>, navController: NavController) {
    val readingNowlist = listofbooks.filter { mBook ->
        mBook.startedReading!= null && mBook.finishedReading == null
    }
    HorizontalScrollableComponent(readingNowlist) {
        navController.navigate(ReaderScreen.UpdateScreen.name + "/$it")
    }
    //ListCard(MBook())
}

@Composable
fun FABcontent(onTap: () -> Unit) {
    FloatingActionButton(onClick = {
        onTap()

    }, shape = RoundedCornerShape(50.dp), containerColor = Color(0xFFF369C0)) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add a BOOK",
            // tint = MaterialTheme.colorScheme.onSecondary
        )
    }
}



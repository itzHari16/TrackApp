package com.example.readerapp.screen.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.readerapp.components.InputField
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.model.Item
import com.example.readerapp.navigation.ReaderScreen


@Composable
fun SearchScreen(navController: NavController,viewModel: BookSearchViewmodel= hiltViewModel()) {
    Scaffold(topBar = {
        ReaderAppBar(
            title = "Search Books",
            icon = Icons.Default.ArrowBack,
            navController = navController,
            showProfile = false
        ) {
            navController.navigate(ReaderScreen.ReaderHomeScreen.name)
            // or we can use navController.popBackStack()
        }
    }) { innerPadding ->

        Surface(modifier = Modifier.padding(innerPadding)) {
            Column {
                SearchForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ){ searchQuery->
                    viewModel.searchBooks(query = searchQuery)
                }
                Spacer(modifier = Modifier.height(20.dp))
                BookList(navController=navController, viewModel = hiltViewModel())

            }

        }
    }
}


@Composable
fun BookList(navController: NavController,viewModel: BookSearchViewmodel= hiltViewModel()) {

    val listOfBooks = viewModel.list
    if (viewModel.isLoading) {
        Row (horizontalArrangement = Arrangement.SpaceBetween){
            LinearProgressIndicator()
            Text(text = "Loading...")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(), contentPadding = PaddingValues(12.dp)
        ) {
            items(listOfBooks) { book ->
                BookRow(book = book, navController = navController)

            }
        }
    }
}

//    val listOfBooks = listOf(
////        MBook(id = "asdf", title = "Hello Again", author = "All of us", notes = null),
////        MBook(id = "asdf", title = "Hello Again", author = "All of us", notes = null),
////        MBook(id = "asdf", title = "Hello Again", author = "All of us", notes = null),
////        MBook(id = "asdf", title = "Hello Again", author = "All of us", notes = null)
//    )



@Composable
fun BookRow(book: Item, navController: NavController) {
    Card(
        modifier =
        Modifier
            .clickable {
                navController.navigate(ReaderScreen.DetailScreen.name+"/${book.id}")
            }
            .fillMaxWidth()
            .padding(3.dp)
            .height(100.dp),
        shape = RectangleShape, elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.Top) {

            val imageUrl:String=if(book.volumeInfo.imageLinks.smallThumbnail.isEmpty()) {
                "http://books.google.com/books/content?id=ZthJlG4o-2wC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
            }else
            {
                book.volumeInfo.imageLinks.smallThumbnail
            }
            Image(
                painter = rememberImagePainter(data = imageUrl),
                contentDescription = "book image", modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
            )
            Column {

                Text(text = book.volumeInfo.title, overflow = TextOverflow.Ellipsis)
                Text(
                    text = "Author: ${book.volumeInfo.authors}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Date: ${book.volumeInfo.publishedDate}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${book.volumeInfo.categories}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )


            }
        }

    }

}


@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
   // viewmodel: BookSearchViewmodel,
    loading: Boolean = false,
    hint: String = "Search",
    onSearch: (String) -> Unit = {}
) {
    Column() {
        val searchQueryState = rememberSaveable {
            mutableStateOf("")
        }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQueryState.value) {
            searchQueryState.value.trim().isNotEmpty()
        }
        InputField(
            valueState = searchQueryState,
            labelId = "Search",
            enabled = true,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(searchQueryState.value.trim())
                searchQueryState.value = ""
                keyboardController?.hide()
            })
    }

}

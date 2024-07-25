package com.example.readerapp.screen.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readerapp.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.readerapp.model.Item
import com.example.readerapp.repository.BookRepository

@HiltViewModel
class BookSearchViewmodel @Inject constructor(private val repository: BookRepository) :
    ViewModel() {

    var list: List<Item> by mutableStateOf(listOf())
    var isLoading: Boolean by mutableStateOf(true)


    init {
        loadBooks()
    }

    private fun loadBooks() {
        searchBooks("flutter")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            isLoading = true // use this or use dispatchers.default
            if (query.isEmpty()) {
                return@launch
            }
            try {
                when (val response = repository.getBooks(query)) {
                    is Resource.Success -> {

                        list = response.data!!
                        Log.d("Book", "searchBooks: $list")
                        if (list.isNotEmpty()) isLoading = false
                    }

                    is Resource.Error -> {
                        isLoading = false
                        Log.e("Network", "searchBooks:Failed to get books ")
                    }

                    else -> {
                        isLoading = false
                    }
                }
            } catch (exception: Exception) {
                isLoading = false
                Log.d("NetWork", "searchBooks: ${exception.message.toString()}")
            }
        }

    }
}


//
//@HiltViewModel
//class BookSearchViewmodel @Inject constructor(private val repository: BookRepository):ViewModel() {
//
//    val listOfBooks: MutableState<DataOrException<List<Item>, Boolean, Exception>> =
//        mutableStateOf(DataOrException(null, true, Exception("")))
//
//    init {
//        searchBooks("android")
//    }
//
//     fun searchBooks(query:String){
//        viewModelScope.launch{
//            if (query.isEmpty()) {
//                return@launch
//            }
//             listOfBooks.value.loading=true
//            listOfBooks.value = repository.getBooks(query)
//            if(listOfBooks.value.data.toString().isNotEmpty())
//            {
//                listOfBooks.value.loading=false
//            }
//        }
//    }
//}
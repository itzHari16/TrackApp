package com.example.readerapp.screen.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readerapp.data.DataOrException
import com.example.readerapp.model.MBook
import com.example.readerapp.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: FireRepository): ViewModel(){

    val data: MutableState<DataOrException<List<MBook>,Boolean,Exception>>
    = mutableStateOf(DataOrException(listOf(),true,Exception("")))

    init {
        getAllBooksFromDatabse()
    }

    private fun getAllBooksFromDatabse() {
        viewModelScope.launch {
            data.value.loading=true
            data.value=repository.getAllbooksFromDatabase()
            if (!data.value.data?.isNotEmpty()!!) data.value.loading=false
        }
        Log.d("GET", "getAllBooksFromDatabse: ${data.value.data?.toList().toString()}")
    }

}
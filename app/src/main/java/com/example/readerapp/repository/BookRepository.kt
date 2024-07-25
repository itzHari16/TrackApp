package com.example.readerapp.repository


import com.example.readerapp.data.Resource
import com.example.readerapp.model.Item
import com.example.readerapp.network.BookApi
import javax.inject.Inject

class BookRepository @Inject constructor(private val api: BookApi) {

    suspend fun getBooks(searchQuery: String): Resource<List<Item>> {
        return try {
            Resource.loading(data = true)
            val itemList = api.getAllBooks(searchQuery).items
            if (itemList.isNotEmpty()) Resource.loading(data = false)
            Resource.Success(data = itemList)
        } catch (exception: Exception) {
            Resource.Error(message = exception.message.toString())
        }


    }

    suspend fun getBookInfo(bookId: String): Resource<Item> {
        val response = try {
            Resource.loading(data = true)
            api.getBookInfo(bookId=bookId)
        } catch (exception: Exception) {
            return Resource.Error(message = "An error occured ${exception.message.toString()}")
        }
        Resource.loading(data = false)
        return Resource.Success(data = response)
    }
}
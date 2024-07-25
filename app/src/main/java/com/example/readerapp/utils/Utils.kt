package com.example.readerapp.utils

import android.icu.text.DateFormat
import com.google.firebase.Timestamp


fun formatDate(timeStamp: Timestamp): String {
    //val date =
      return DateFormat.getDateInstance().format(timeStamp.toDate()).toString().split(",")[0]
    //return date
}
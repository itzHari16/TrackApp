package com.example.readerapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.readerapp.navigation.ReaderNavigation
import com.example.readerapp.ui.theme.ReaderAppTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReaderAppTheme {
            ReaderApp()

            }
        }
    }
}

@OptIn(ExperimentalComposeApi::class)
@Composable
fun ReaderApp() {
    Surface(
       // color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
         //   .padding(top = 46.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
    ReaderNavigation()
        }

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ReaderAppTheme {

    }
}
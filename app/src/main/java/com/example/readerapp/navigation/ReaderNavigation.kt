package com.example.readerapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.readerapp.screen.ReaderSplashScreen
import com.example.readerapp.screen.details.BookDetailsScreen
import com.example.readerapp.screen.home.HomeScreenViewModel
import com.example.readerapp.screen.home.ReaderHomeScreen
import com.example.readerapp.screen.login.ReaderLoginScreen
import com.example.readerapp.screen.search.BookSearchViewmodel
import com.example.readerapp.screen.search.SearchScreen
import com.example.readerapp.screen.stats.ReaderStatsScreeen
import com.example.readerapp.screen.update.BookUpdateScreen

@ExperimentalComposeApi
@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ReaderScreen.SplashScreen.name
    ) {
        composable(ReaderScreen.SplashScreen.name) {
            ReaderSplashScreen(navController = navController)
        }

        composable(ReaderScreen.LoginScreen.name) {
            ReaderLoginScreen(navController = navController)
        }

        composable(ReaderScreen.ReaderHomeScreen.name) {
            val homeViewModel = hiltViewModel<HomeScreenViewModel>()
            ReaderHomeScreen(navController = navController, viewModel = homeViewModel)
        }

        composable(ReaderScreen.ReaderStatsScreen.name) {
            val homeViewModel = hiltViewModel<HomeScreenViewModel>()
            ReaderStatsScreeen(navController = navController, viewModel=homeViewModel )
        }
        composable(ReaderScreen.SearchScreen.name) {
            val searchViewmodel = hiltViewModel<BookSearchViewmodel>()
            SearchScreen(navController = navController, viewModel = searchViewmodel)
        }

        val detailName = ReaderScreen.DetailScreen.name
        composable("$detailName/{bookId}", arguments = listOf(navArgument("bookId") {
            type = NavType.StringType
        })) { backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let {
                BookDetailsScreen(navController = navController, bookId = it.toString())
            }

        }
        val updateName = ReaderScreen.UpdateScreen.name
        composable("$updateName/{bookItemId}", arguments = listOf(navArgument("bookItemId") {
            type = NavType.StringType
        })) { navbackStackEntry ->
            navbackStackEntry.arguments?.getString("bookItemId").let {
                BookUpdateScreen(navController = navController, bookItemId = it.toString())
            }
        }

    }
}
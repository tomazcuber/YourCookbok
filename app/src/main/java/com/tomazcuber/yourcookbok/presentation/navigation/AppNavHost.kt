package com.tomazcuber.yourcookbok.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tomazcuber.yourcookbok.search.screen.SearchRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Search,
        modifier = modifier
    ) {
        composable<AppDestination.Search> {
            SearchRoute()
        }
        composable<AppDestination.Saved> {
            // Placeholder for the Saved Recipes Screen
            Text("Saved Recipes Screen")
        }
    }
}
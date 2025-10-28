package com.tomazcuber.yourcookbok.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tomazcuber.yourcookbok.detail.screen.RecipeDetailRoute
import com.tomazcuber.yourcookbok.saved.screen.SavedRecipesRoute
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
            SearchRoute(
                onNavigateToDetail = { recipeId ->
                    navController.navigate(AppDestination.RecipeDetail(recipeId))
                }
            )
        }

        composable<AppDestination.Saved> {
            SavedRecipesRoute(
                onNavigateToDetail = { recipeId ->
                    navController.navigate(AppDestination.RecipeDetail(recipeId))
                }
            )
        }

        composable<AppDestination.RecipeDetail> {
            RecipeDetailRoute(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
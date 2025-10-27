package com.tomazcuber.yourcookbok

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.tomazcuber.yourcookbok.presentation.navigation.AppBottomNavigationBar
import com.tomazcuber.yourcookbok.presentation.navigation.AppNavHost
import com.tomazcuber.yourcookbok.presentation.theme.YourCookbokTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YourCookbokTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    YourCookbokTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { AppBottomNavigationBar(navController = navController) }
        ) { innerPadding ->
            // Preview placeholder
            Text("Screen Content", modifier = Modifier.padding(innerPadding))
        }
    }
}
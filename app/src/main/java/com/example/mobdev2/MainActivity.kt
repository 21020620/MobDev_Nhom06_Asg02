package com.example.mobdev2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobdev2.ui.screens.auth.LoginView
import com.example.mobdev2.ui.screens.auth.PasswordResetView
import com.example.mobdev2.ui.screens.auth.SignUpView
import com.example.mobdev2.ui.theme.MobDev2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobDev2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CoreApp()
                }
            }
        }
    }
}

@Composable
fun CoreApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login", content = { LoginView(navController = navController)})
        composable("sign_up", content = { SignUpView(navController = navController)})
        composable("password_reset", content = { PasswordResetView(navController = navController) })
    })
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MobDev2Theme {
        Greeting("Android")
    }
}
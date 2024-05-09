package com.example.mobdev2.ui.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mobdev2.R
import com.example.mobdev2.ui.components.auth.EmailField
import com.example.mobdev2.ui.components.auth.LoginButtonGroup
import com.example.mobdev2.ui.components.auth.PasswordField
import com.example.mobdev2.ui.components.auth.PromptRow
import com.example.mobdev2.ui.components.auth.Title
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.screens.destinations.BookHomeScreenDestination
import com.example.mobdev2.ui.screens.destinations.LoginScreenDestination
import com.example.mobdev2.ui.screens.destinations.ResetPasswordScreenDestination
import com.example.mobdev2.ui.screens.destinations.SignUpScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@RootNavGraph(start = true)
@Destination
@BookNavGraph
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = koinViewModel(parameters = {
        parametersOf(navigator)
    })
//    shareModel: ShareModel
) {
    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val emailError = viewModel.emailError.collectAsState()
    val passwordError = viewModel.passwordError.collectAsState()
//    val navigateToLogin by shareModel.navigateToLogin.observeAsState(initial = false)
    LaunchedEffect(Unit) {
        if(viewModel.hasUser()) navigator.navigate(BookHomeScreenDestination)
    }
//    Observe the navigateToLogin state
//    if (navigateToLogin) {
//        // Navigate to LoginScreen
//        navigator.navigate(LoginScreenDestination)
//        // Reset the state
//        shareModel.navigateToLogin.value = false
//    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) viewModel.signIn(it.data)
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize()
                    .weight(0.5f),
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    modifier = Modifier.requiredSize(180.dp),
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "LOGO"
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
            ) {
                Title(
                    title = "Hi, Welcome Back! ",
                    subtitle = "Hello again, we missed you <3"
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                EmailField(
                    onValueChange = viewModel::setEmail,
                    errorText = emailError.value,
                    isError = emailError.value.isNotEmpty(),
                    input = email.value
                )

                PasswordField(
                    onValueChange = viewModel::setPassword,
                    errorText = passwordError.value,
                    isError = passwordError.value.isNotEmpty(),
                    input = password.value
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    PromptRow(
                        modifier = Modifier,
                        normalText = "Forgot Password?",
                        highlightedText = "Reset",
                        highlightColor = MaterialTheme.colorScheme.error,
                        onClick = {
                            navigator.navigate(ResetPasswordScreenDestination)
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginButtonGroup(
                    loginWithPassword = viewModel::loginWithEmailPassword,
                    loginWithGoogle = {viewModel.loginWithGoogle(launcher)},
                    signUp = viewModel::signUp
                )
            }
        }
    }
}

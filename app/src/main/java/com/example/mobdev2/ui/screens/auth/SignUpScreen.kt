package com.example.mobdev2.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mobdev2.R
import com.example.mobdev2.ui.components.auth.EmailField
import com.example.mobdev2.ui.components.auth.PasswordField
import com.example.mobdev2.ui.components.auth.SignUpButtonGroup
import com.example.mobdev2.ui.components.auth.Title
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@RootNavGraph
@Destination
@Composable
fun SignUpScreen(
    navigator: DestinationsNavigator,
    viewModel: SignUpViewModel = koinViewModel(parameters = {
        parametersOf(navigator)
    })
) {

    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val confirmPassword = viewModel.confirmPassword.collectAsState()
    val emailError = viewModel.emailError.collectAsState()
    val passwordError = viewModel.passwordError.collectAsState()
    val confirmPasswordError = viewModel.confirmPasswordError.collectAsState()

    Surface {
        Column (
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
                    .weight(0.2f)
            ) {
                Title(
                    title = "Create an account ",
                    subtitle = "Join us today !"
                )
            }

            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                EmailField(
                    input = email.value,
                    onValueChange = viewModel::setEmail,
                    errorText = emailError.value,
                    isError = emailError.value.isNotEmpty()
                )

                PasswordField(
                    input = password.value,
                    onValueChange = viewModel::setPassword,
                    isError = passwordError.value.isNotEmpty(),
                    errorText = passwordError.value
                )

                PasswordField(
                    input = confirmPassword.value,
                    isNormal = false,
                    onValueChange = viewModel::setConfirmPassword,
                    errorText = confirmPasswordError.value,
                    isError = confirmPasswordError.value.isNotEmpty()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.55f)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.5f))

                Surface(modifier = Modifier.weight(1f)) {
                    SignUpButtonGroup(
                        signUp = viewModel::signUp,
                        login = viewModel::login
                    )
                }
            }
        }
    }
}
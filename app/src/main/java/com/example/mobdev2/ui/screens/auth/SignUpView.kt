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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobdev2.R
import com.example.mobdev2.ui.components.auth.EmailField
import com.example.mobdev2.ui.components.auth.PasswordField
import com.example.mobdev2.ui.components.auth.SignUpButtonGroup
import com.example.mobdev2.ui.components.auth.Title
import com.example.mobdev2.ui.navigation.Screens
import com.example.mobdev2.ui.theme.MobDev2Theme


@Composable
fun SignUpView(navController: NavController? = null) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }


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
                    onValueChange = { email = it },
                    errorText = emailError,
                    isError = emailError.isNotEmpty()
                )

                PasswordField(
                    onValueChange = { password = it },
                    errorText = passwordError)

                PasswordField(
                    isNormal = false,
                    onValueChange = { confirmPassword = it },
                    errorText = confirmPasswordError,
                    isError = confirmPasswordError.isNotEmpty()
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
                        signUp = { navController?.navigate(Screens.ChooseBookGenres.route)},
                        login = { navController?.popBackStack() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpPreview() {
    MobDev2Theme { SignUpView() }
}
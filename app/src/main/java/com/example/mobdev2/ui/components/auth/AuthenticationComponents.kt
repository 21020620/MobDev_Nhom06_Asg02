package com.example.mobdev2.ui.components.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdev2.R
import com.example.mobdev2.ui.components.CustomButton
import com.example.mobdev2.ui.components.ExpandableTextField
import com.example.mobdev2.ui.theme.Purple40

@Composable
fun LoginButtonGroup(
    loginWithPassword: () -> Unit = {},
    loginWithGoogle: () -> Unit = {},
    signUp: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomButton(
            surfaceModifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            onClick = loginWithPassword,
            text = "Sign In",
            fontSize = 15.sp,
            shape = RoundedCornerShape(24)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .weight(0.7f)
                    .background(Purple40)
            )

            Text(
                modifier = Modifier.weight(0.7f),
                text = "Or With",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Purple40
                ),
            )

            Box(
                modifier = Modifier
                    .height(2.dp)
                    .weight(0.7f)
                    .background(Purple40)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            CustomButton(
                modifier = Modifier.fillMaxWidth(),
                surfaceModifier = Modifier.height(45.dp),
                onClick = loginWithGoogle,
                isFilled = false,
                text = "Google",
                fontSize = 15.sp,
                shape = RoundedCornerShape(24),
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
            )
        }

        PromptRow(
            normalText = "Don't have an account?",
            highlightedText = "Sign Up",
            highlightColor = Purple40,
            onClick = signUp
        )
    }
}

@Composable
fun SignUpButtonGroup(
    signUp: () -> Unit = {},
    login: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            onClick = signUp,
            text = "Sign Up",
            fontSize = 15.sp,
            shape = RoundedCornerShape(24)
        )

        Spacer(modifier = Modifier.height(20.dp))

        PromptRow(
            normalText = "Already have an account?",
            highlightedText = "Login",
            highlightColor = Purple40,
            onClick = login
        )
    }
}

@Composable
fun EmailField(
    onValueChange: ((String) -> Unit)? = null,
    errorText: String,
    isError: Boolean = false,
    input: String = ""
) {
    ExpandableTextField(
        modifier = Modifier.fillMaxWidth(),
        onValueChange = onValueChange,
        label = "Email",
        placeholder = "Enter Your Email",
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.email),
                contentDescription = null)
        },
        value = input,
        isError = isError,
        errorText = errorText,
    )
}

@Composable
fun PasswordField(
    isNormal: Boolean = true,
    onValueChange: ((String) -> Unit)? = null,
    errorText: String,
    isError: Boolean = false,
    input: String = ""
) {
    ExpandableTextField(
        isPassword = true,
        onValueChange = onValueChange,
        label = "Password",
        placeholder = "${if (isNormal) "Enter" else "Confirm"} Your Password",
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                painter = painterResource(id = if (isNormal) R.drawable.key else R.drawable.lock),
                contentDescription = null,
            )
        },
        isError = isError,
        errorText = errorText,
        value = input,
    )
}
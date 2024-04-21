package com.example.mobdev2.ui.components.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.mobdev2.R
import com.example.mobdev2.ui.components.ExpandableTextField

@Composable
fun PasswordField(
    isNormal: Boolean = true,
    onValueChange: ((String) -> Unit)? = null,
    errorText: String,
    isError: Boolean = false,
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
    )
}
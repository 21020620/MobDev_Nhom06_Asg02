package com.example.mobdev2.ui.components.auth

import android.app.VoiceInteractor.Prompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdev2.ui.components.CustomButton
import com.example.mobdev2.ui.theme.Purple40

@Composable
fun SignUpButtonGroup(
    signUp: () -> Unit = {},
    login: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.SpaceBetween,
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
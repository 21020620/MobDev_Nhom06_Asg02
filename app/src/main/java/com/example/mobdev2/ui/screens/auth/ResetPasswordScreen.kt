package com.example.mobdev2.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobdev2.R
import com.example.mobdev2.ui.components.auth.innerShadow
import com.example.mobdev2.ui.theme.Purple20
import com.example.mobdev2.ui.theme.Purple40
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import org.koin.androidx.compose.koinViewModel

@RootNavGraph
@Destination
@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel = koinViewModel()
) {
    val email = viewModel.email.collectAsState()

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(35.dp),
        ) {
            Box(
                modifier = Modifier.weight(1.25f),
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    painter = painterResource(id = R.drawable.forgotpassword),
                    contentDescription = null
                )
            }


            Column(
                modifier = Modifier.weight(1.5f),
                verticalArrangement = Arrangement.spacedBy(35.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Forgot password?",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple20
                    )

                    Box {
                        Text(
                            text = "Don’t worry ! It happens." +
                                    " Please enter your email address, we wil send " +
                                    "your the link to reset your password.",
                            fontSize = 13.sp,
                            color = Color(0xFF5B5858)
                        )
                    }

                }

                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email.value,
                    onValueChange = viewModel::setEmail,
                    textStyle = TextStyle(
                        color = Color(0xFF746983),
                        fontSize = 14.sp
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            Modifier
                                .background(
                                    color = Color(0xFFEAEAEA),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .innerShadow(
                                    blur = 4.dp,
                                    color = Color(0xFFdbdad7),
                                    spread = 8.dp,
                                    cornersRadius = 8.dp
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.MailOutline,
                                contentDescription = null,
                                tint = Color(0xFF6B6678)
                            )

                            Spacer(Modifier.width(16.dp))

                            if (email.value.isEmpty())
                                Text(
                                    text = "Enter Your Email",
                                    color = Color(0xFF746983)
                                )
                            else
                                innerTextField()
                        }
                    }
                )

                Button(
                    modifier = Modifier
                        .width(140.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(40),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                    onClick = { }
                ) {
                    Text(
                        text = "Submit",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
package com.example.mobdev2.ui.screens.book

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VolumeDown
import androidx.compose.material.icons.outlined.VolumeMute
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobdev2.R
import com.example.mobdev2.ui.screens.book.main.BookNavGraph
import com.example.mobdev2.ui.theme.figeronaFont
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.text.style.TextOverflow.Companion as TextOverflow1


@OptIn(ExperimentalMaterial3Api::class)
@BookNavGraph
@Composable
@Destination
fun SettingsScreen(
    navController: NavController? = null,
    navigator: DestinationsNavigator,
    viewModel: SettingsScreenViewModel = koinViewModel()
) {

    val snackBarHostState = remember { SnackbarHostState() }
    val isSigningOut = viewModel.isSigningOut.collectAsState()

    if(isSigningOut.value) {
        CircularProgressIndicator()
        navigator.clearBackStack("book_graph")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text(
                        stringResource(id = R.string.book_settings_screen),
                        maxLines = 1,
                        overflow = TextOverflow1.Ellipsis,
                        modifier = Modifier.padding(bottom = 2.dp),
                        fontSize = 22.sp,
                        fontFamily = figeronaFont,
                        fontStyle = MaterialTheme.typography.headlineMedium.fontStyle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController?.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Setting",
                        modifier = Modifier.padding(end = 10.dp)
                    )
                },

                )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ProfileCard()
                HorizontalDivider()
                Text(
                    text = "Display",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight(700)),
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp)
                )
                ThemeSetting()
                HorizontalDivider()
                Text(
                    text = "Sound",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight(700)),
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp)
                )
                SoundSetting()
                HorizontalDivider()
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight(700)),
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp)
                )
                NotificationSetting()
                HorizontalDivider()
                Logout(viewModel)
            }
        }
    }
}


@Composable
@Preview
fun ProfileCard() {
    val user = FirebaseAuth.getInstance().currentUser
    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_picture), // Replace with your actual resource
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = user?.displayName ?: "Reader",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight(700))
                )
                Text(text = user?.email ?: "No email", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Profile",
                    modifier = Modifier.padding(end = 5.dp)
                )
            }
        }
    }
}

@Composable
@Preview
fun ThemeSetting() {
    var currentTheme by remember { mutableStateOf(0) }
    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                // Change theme here
            },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (currentTheme == 0) {
                Icon(
                    imageVector = Icons.Outlined.LightMode,
                    contentDescription = "Change Theme",
                    modifier = Modifier.padding(end = 10.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.NightsStay,
                    contentDescription = "Change Theme",
                    modifier = Modifier.padding(end = 10.dp)
                )
            }
            Column {
                Text(
                    text = "Change Theme",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight(700))
                )
            }
        }
    }
}

@Composable
@Preview
fun SoundSetting() {
    val context = LocalContext.current
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    var sliderPosition by remember { mutableFloatStateOf(currentVolume.toFloat() / maxVolume) }
    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (sliderPosition > 0.5f) {
                Icon(
                    imageVector = Icons.Outlined.VolumeUp,
                    contentDescription = "volume up",
                    modifier = Modifier.padding(end = 10.dp)
                )
            } else if (sliderPosition == 0f) {
                Icon(
                    imageVector = Icons.Outlined.VolumeMute,
                    contentDescription = "mute",
                    modifier = Modifier.padding(end = 10.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.VolumeDown,
                    contentDescription = "volume down",
                    modifier = Modifier.padding(end = 10.dp)
                )
            }

            Column {
                Text(
                    text = "Audio Volume",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight(700))
                )
                Slider(value = sliderPosition, onValueChange = {
                    sliderPosition = it
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        (it * maxVolume).toInt(),
                        0
                    )
                })
            }
        }
    }
}

@Composable
@Preview
fun NotificationSetting() {
    var boolean by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { /* handle notification here */ },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (boolean) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "turn on notifications",
                    modifier = Modifier.padding(end = 10.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.NotificationsOff,
                    contentDescription = "turn off notifications",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(end = 10.dp)
                )

            }
            Column {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight(700))
                )
                if (boolean) {
                    Text(
                        text = "Turn off notifications",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Turn on notifications",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

        }
    }
}
@Composable
fun Logout(
    viewModel: SettingsScreenViewModel
) {
    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                viewModel.setIsLoading()
                Firebase.auth.signOut()
            },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBackIosNew,
                contentDescription = "logout",
                modifier = Modifier.padding(end = 10.dp)
            )
            Column {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight(700))
                )

            }
        }
    }
}
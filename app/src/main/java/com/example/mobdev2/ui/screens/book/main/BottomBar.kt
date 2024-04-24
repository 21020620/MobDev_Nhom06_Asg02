package com.example.mobdev2.ui.screens.book.main

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.example.mobdev2.R
import com.example.mobdev2.ui.screens.NavGraphs
import com.example.mobdev2.ui.screens.appCurrentDestinationAsState
import com.example.mobdev2.ui.screens.destinations.AllBookScreenDestination
import com.example.mobdev2.ui.screens.destinations.BookDetailScreenDestination
import com.example.mobdev2.ui.screens.destinations.BookForumScreenDestination
import com.example.mobdev2.ui.screens.destinations.Destination
import com.example.mobdev2.ui.screens.destinations.LibraryScreenDestination
import com.example.mobdev2.ui.screens.destinations.SettingsScreenDestination
import com.example.mobdev2.ui.screens.startAppDestination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

@Composable
fun BottomBar(
    navController: NavController
) {
    val currentDestination: Destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.bookGraph.startAppDestination

    NavigationBar {
        BottomBarDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.navigate(destination.direction, fun NavOptionsBuilder.() {
                        launchSingleTop = true
                    })
                },
                icon = { Icon(destination.icon, contentDescription = stringResource(destination.label))},
                label = { Text(stringResource(destination.label)) },
            )
        }
    }
}

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    AllBook(AllBookScreenDestination, Icons.Default.Home, R.string.book_all_screen),
    BookLibrary(LibraryScreenDestination, Icons.Default.Search, R.string.book_library),
    BookForum(BookForumScreenDestination, Icons.Default.Chair, R.string.book_forum_screen),
    Settings(SettingsScreenDestination, Icons.Default.Settings, R.string.book_settings_screen)
}

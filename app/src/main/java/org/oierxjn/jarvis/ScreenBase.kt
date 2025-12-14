package org.oierxjn.jarvis

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import org.oierxjn.jarvis.ui.theme.JARVISTheme

@Composable
fun ScreenBase(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackBarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
){
    JARVISTheme {
        Scaffold(
            modifier = modifier,
            topBar = topBar,
            bottomBar = bottomBar,
            snackbarHost = snackBarHost,
        ){ innerPadding ->
            content(innerPadding)
        }
    }
}
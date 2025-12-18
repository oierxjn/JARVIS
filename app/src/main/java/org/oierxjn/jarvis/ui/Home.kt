package org.oierxjn.jarvis.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.navigation.compose.composable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.oierxjn.jarvis.R
import org.oierxjn.jarvis.ScreenBase
import org.oierxjn.jarvis.model.AppDataStore
import org.oierxjn.jarvis.model.DataModel
import org.oierxjn.jarvis.model.LoadedFlag
import org.oierxjn.jarvis.model.SettingData
import org.oierxjn.jarvis.model.StatCard
import org.oierxjn.jarvis.model.StatItem
import org.oierxjn.jarvis.netapi.RemoteApi
import org.oierxjn.jarvis.ui.components.M3CustomGrid
import org.oierxjn.jarvis.ui.components.SemiModalSheet
import org.oierxjn.jarvis.ui.components.ToastUtil.showLong
import org.oierxjn.jarvis.ui.components.ToastUtil.showShort
import androidx.compose.foundation.lazy.items
import org.oierxjn.jarvis.model.ChatType
import org.oierxjn.jarvis.ui.pages.HomePage
import org.oierxjn.jarvis.ui.pages.SettingsPage
import org.oierxjn.jarvis.ui.pages.ChatsPage

enum class Destination(
    val route: String,
    val label: String,
    val iconResourceId: Int,
    val contentDescription: String
){
    Home("home", "Home", R.drawable.home_24dp, "Home"),
    Chats("chats", "Chats", R.drawable.chat_bubble_24dp, "Chats"),
    Settings("settings", "Settings", R.drawable.settings_24dp, "Settings"),
}


@Composable
fun MainHome(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    suspend fun localLoad(){
        DataModel.getLocalSetting(context)
    }

    suspend fun reloadInternet(){
        try {
            RemoteApi.getRemoteSetting()
        } catch (e: Exception){
            showShort(context, "远程设置请求错误：${e.message}")
            Log.e("MainHome", e.message ?: "未知错误")
        }
        try {
            RemoteApi.getMonitoredChatList()
        } catch (e: Exception){
            showShort(context, "监控列表请求错误：${e.message}")
            Log.e("MainHome", e.message ?: "未知错误")
        }
    }

    LaunchedEffect(Unit) {
        localLoad()
        reloadInternet()
    }

    Scaffold(
        modifier = modifier,
        bottomBar = { BottomToolBar(navController) }
    ){ innerPadding ->
        HomeNavHost(
            navController,
            startDestination = Destination.Home,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HomeNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
){
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
    ){
        Destination.entries.forEach { destination ->
            composable(destination.route){
                when(destination){
                    Destination.Home -> HomePage(modifier)
                    Destination.Settings -> SettingsPage(modifier)
                    Destination.Chats -> ChatsPage(modifier)
                }
            }
        }
    }
}

@Composable
fun BottomToolBar(
    navController: NavController,
){
    val startDestination = Destination.Home
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    NavigationBar(
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        Destination.entries.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = selectedDestination == index,
                onClick = {
                    navController.navigate(destination.route)
                    selectedDestination = index
                },
                icon = {
                    Icon(
                        painter = painterResource(destination.iconResourceId),
                        contentDescription = destination.contentDescription,
                    )
                },
                label = { Text(destination.label) }
            )
        }
    }
}


/**
 * 用于测试
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
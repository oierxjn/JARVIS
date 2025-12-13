package org.oierxjn.jarvis.ui


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.navigation.compose.composable
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.oierxjn.jarvis.R
import org.oierxjn.jarvis.ScreenBase
import org.oierxjn.jarvis.model.AppDataStore
import org.oierxjn.jarvis.model.DataModel
import org.oierxjn.jarvis.model.SettingData
import org.oierxjn.jarvis.netapi.RemoteApi

enum class Destination(
    val route: String,
    val label: String,
    val iconResourceId: Int,
    val contentDescription: String
){
    Home("home", "Home", R.drawable.home_24dp, "Home"),
    Settings("settings", "Settings", R.drawable.settings_24dp, "Settings")
}


@Composable
fun MainHome(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

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
fun Home(
    modifier: Modifier = Modifier
) {
    ScreenBase(
        modifier,
    ){
        Greeting("Home")
    }
}

@Composable
fun InputBox(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
){
    OutlinedTextField(
        value = value,
        label = { Text(label)},
        onValueChange = { onValueChange(it) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun Settings(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }

    var isRemoteLoaded by rememberSaveable { mutableStateOf(false) }
    // 向远程的设置
    var remoteHost by remember { mutableStateOf(DataModel.remoteHost) }
    var remotePort by remember { mutableIntStateOf(DataModel.remotePort) }

    // 远程设置
    var aiApiEndPoint by remember { mutableStateOf(DataModel.settingData.ai_endpoint) }
    var aiApiKey by remember { mutableStateOf(DataModel.settingData.ai_api_key) }
    var aiModel by remember { mutableStateOf(DataModel.settingData.ai_model) }
    var fetchDays by remember { mutableIntStateOf(DataModel.settingData.fetch_days) }
    var napcatHost by remember { mutableStateOf(DataModel.settingData.napcat_host) }
    var napcatPort by remember { mutableIntStateOf(DataModel.settingData.napcat_port) }

    LaunchedEffect(Unit){
        if(!isRemoteLoaded){
            val remoteHostTask = launch {
                remoteHost = AppDataStore.getStringFlow(context, AppDataStore.REMOTE_HOST_KEY, remoteHost).first()
            }
            val remotePortTask = launch {
                remotePort = AppDataStore.getIntFlow(context, AppDataStore.REMOTE_PORT_KEY, remotePort).first()
            }
            listOf(remoteHostTask, remotePortTask).joinAll()
            isRemoteLoaded = true
        }
    }
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            RemoteApi.getRemoteSetting({
                aiApiEndPoint = DataModel.settingData.ai_endpoint
                aiApiKey = DataModel.settingData.ai_api_key
                aiModel = DataModel.settingData.ai_model
                fetchDays = DataModel.settingData.fetch_days
                napcatHost = DataModel.settingData.napcat_host
                napcatPort = DataModel.settingData.napcat_port
                isLoading = false
            })
        } catch (e: Exception) {
            Log.e("Settings", "[JARVIS] load setting error:${e.message}", e)
            isLoading = false
        }
    }

    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope

    ScreenBase(
        modifier,
    ) {
        Column(
            Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
            val innerModifier = Modifier.padding(10.dp).fillMaxWidth()

            Row {
                Button({
                    lifecycleScope.launch {
                        val remoteHostTask = launch {
                            AppDataStore.saveString(context, AppDataStore.REMOTE_HOST_KEY, remoteHost)
                        }
                        val remotePortTask = launch {
                            AppDataStore.saveInt(context, AppDataStore.REMOTE_PORT_KEY, remotePort)
                        }
                        listOf(remoteHostTask, remotePortTask).joinAll()
                        Toast.makeText(
                            context.applicationContext,
                            "保存成功",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }){
                    Text("保存")
                }
            }
            Card {
                Column (innerModifier){
                    Text("远程主机配置")
                    InputBox(remoteHost, "主机地址", { remoteHost = it })
                    InputBox(remotePort.toString(), "端口", { remotePort = it.toInt() })
                }
            }
            if(isLoading){
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            Card {
                Column (innerModifier){
                    Text("AI配置")
                    InputBox(aiApiEndPoint, "AI API 端点", { aiApiEndPoint = it })
                    InputBox(aiApiKey, "API Key", { aiApiKey = it })
                    InputBox(aiModel, "模型名称", { aiModel = it })
                }
            }
            Card {
                Column (innerModifier){
                    Text("默认配置")
                    InputBox(fetchDays.toString(), "默认获取天数", { fetchDays = it.toInt() })
                }
            }
            Card {
                Column (innerModifier){
                    Text("远端主机上的NapCat-QCE配置")
                    InputBox(napcatHost, "主机地址", { napcatHost = it })
                    InputBox(napcatPort.toString(), "端口", { napcatPort = it.toInt() })
                }
            }
        }
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
                    Destination.Home -> Home(modifier)
                    Destination.Settings -> Settings(modifier)
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
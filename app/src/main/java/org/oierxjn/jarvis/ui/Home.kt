package org.oierxjn.jarvis.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.oierxjn.jarvis.R
import org.oierxjn.jarvis.ScreenBase
import org.oierxjn.jarvis.model.AppDataStore
import org.oierxjn.jarvis.model.DataModel
import org.oierxjn.jarvis.model.SettingData
import org.oierxjn.jarvis.model.StatCard
import org.oierxjn.jarvis.model.StatItem
import org.oierxjn.jarvis.netapi.RemoteApi
import org.oierxjn.jarvis.ui.components.M3CustomGrid
import org.oierxjn.jarvis.ui.components.ToastUtil.showLong
import org.oierxjn.jarvis.ui.components.ToastUtil.showShort

enum class Destination(
    val route: String,
    val label: String,
    val iconResourceId: Int,
    val contentDescription: String
){
    Home("home", "Home", R.drawable.home_24dp, "Home"),
    Settings("settings", "Settings", R.drawable.settings_24dp, "Settings"),
    Chats("chats", "Chats", R.drawable.chat_bubble_24dp, "Chats")
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
fun Home(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    suspend fun reloadInternet(){
        try {
            RemoteApi.getDashBoardStat()
            DataModel.dashBoardViewModel.syncFromDataModel()
            snackBarHostState.showSnackbar(
                "仪表盘刷新成功",
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
        } catch (e: Exception){
            showShort(context, "设置仪表盘错误：${e.message}")
            Log.e("MainHome", e.message ?: "未知错误")
        }
    }
    LaunchedEffect(Unit) {
        reloadInternet()
    }
    ScreenBase(
        modifier,
        snackBarHost = { SnackbarHost(snackBarHostState)},
        topBar = {
            Row (
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    "仪表盘",
                    modifier = Modifier.padding(16.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                )
                IconButton(
                    {
                        coroutineScope.launch {
                            reloadInternet()
                        }
                    },
                    Modifier.padding(end = 0.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.refresh_24dp),
                        contentDescription = "刷新"
                    )
                }
            }
        }
    ){ innerPadding ->

        val chatCount by DataModel.dashBoardViewModel.chatsCount.collectAsState()
        val messageCount by DataModel.dashBoardViewModel.messagesCount.collectAsState()
        val todoCount by DataModel.dashBoardViewModel.pendingTasksCount.collectAsState()
        val aiCount by DataModel.dashBoardViewModel.summariesCount.collectAsState()

        val statItems = listOf(
            StatItem(R.drawable.chat_bubble_24dp, chatCount, "监控聊天", Color(0xFF4A90E2)),
            StatItem(R.drawable.chat_24dp, messageCount, "消息总数", Color(0xFF4CAF50)),
            StatItem(R.drawable.task_alt_24dp, todoCount, "待办任务", Color(0xFFFFB74D)),
            StatItem(R.drawable.article_24dp, aiCount, "AI总结", Color(0xFF9C27B0))
        )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            M3CustomGrid(
                columns = 2,
                items = statItems,
                verticalSpacing = 10.dp,
                horizontalSpacing = 8.dp,
            ) {
                StatCard(it)
            }
        }
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

    val snackBarHostState = remember { SnackbarHostState() }

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

    fun syncLocalSettings(){
        remoteHost = DataModel.remoteHost
        remotePort = DataModel.remotePort
    }

    fun syncRemoteSettings(){
        aiApiEndPoint = DataModel.settingData.ai_endpoint
        aiApiKey = DataModel.settingData.ai_api_key
        aiModel = DataModel.settingData.ai_model
        fetchDays = DataModel.settingData.fetch_days
        napcatHost = DataModel.settingData.napcat_host
        napcatPort = DataModel.settingData.napcat_port
    }

    suspend fun saveSettings(context: Context){
        DataModel.saveLocalSetting(context)
        val newSettingData = SettingData(
            ai_endpoint = aiApiEndPoint,
            ai_api_key = aiApiKey,
            ai_model = aiModel,
            fetch_days = fetchDays,
            napcat_host = napcatHost,
            napcat_port = napcatPort
        )
        if(newSettingData != DataModel.settingData){
            DataModel.settingData = newSettingData
            RemoteApi.updateRemoteSetting()
        }
    }

    LaunchedEffect(Unit){
        syncLocalSettings()
        isLoading = true
        syncRemoteSettings()
        isLoading = false
    }

    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope
    val coroutineScope = rememberCoroutineScope()

    ScreenBase(
        modifier,
        snackBarHost = { SnackbarHost(snackBarHostState)},
        topBar = {
            Row (
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    "设置",
                    modifier = Modifier.padding(16.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                )
                IconButton({
                    lifecycleScope.launch {
                        try {
                            saveSettings(context)
                            snackBarHostState.showSnackbar(
                                message = "保存成功",
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        } catch (e: Exception){
                            showShort(context, "保存失败：${e.message}")
                        }
                    }}
                ) {
                    Icon(
                        painter = painterResource(R.drawable.news_24dp),
                        contentDescription = "保存",
                    )
                }
                IconButton({
                    coroutineScope.launch {
                        syncRemoteSettings()
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.refresh_24dp),
                        contentDescription = "刷新",
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(horizontal = 16.dp) // 水平方向16dp内边距
                .padding(top = innerPadding.calculateTopPadding()) // 顶部内边距，避免与TopBar重叠
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val innerModifier = Modifier.padding(10.dp).fillMaxWidth()
            Card {
                Column (innerModifier){
                    Text("远程主机配置")
                    InputBox(remoteHost, "主机地址", { remoteHost = it })
                    InputBox(remotePort.toString(), "端口", { remotePort = it.toIntOrNull() ?: 0 })
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
                    InputBox(fetchDays.toString(), "默认获取天数", { fetchDays = it.toIntOrNull()?:0 })
                }
            }
            Card {
                Column (innerModifier){
                    Text("远端主机上的NapCat-QCE配置")
                    InputBox(napcatHost, "主机地址", { napcatHost = it })
                    InputBox(napcatPort.toString(), "端口", { napcatPort = it.toIntOrNull()?: 0 })
                }
            }
        }
    }
}


@Composable
fun Chats(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    ScreenBase(
        modifier,
        snackBarHost = { SnackbarHost(snackBarHostState)},
        topBar = {
            Row (
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    "聊天",
                    modifier = Modifier.padding(16.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                )
                IconButton(
                    {
                        // TODO: 实现重新加载功能
                    },
                    Modifier.padding(end = 0.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.refresh_24dp),
                        contentDescription = "刷新"
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn {

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
                    Destination.Chats -> Chats(modifier)
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
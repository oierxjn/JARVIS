package org.oierxjn.jarvis.ui.pages

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.oierxjn.jarvis.R
import org.oierxjn.jarvis.ScreenBase
import org.oierxjn.jarvis.model.DataModel
import org.oierxjn.jarvis.model.SettingData
import org.oierxjn.jarvis.netapi.RemoteApi
import org.oierxjn.jarvis.ui.components.ToastUtil.showShort

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
fun SettingsPage(
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
        snackBarHost = { SnackbarHostState()},
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
package org.oierxjn.jarvis.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.oierxjn.jarvis.R
import org.oierxjn.jarvis.ScreenBase
import org.oierxjn.jarvis.model.DataModel
import org.oierxjn.jarvis.model.LoadedFlag
import org.oierxjn.jarvis.model.StatCard
import org.oierxjn.jarvis.model.StatItem
import org.oierxjn.jarvis.netapi.RemoteApi
import org.oierxjn.jarvis.ui.components.M3CustomGrid
import org.oierxjn.jarvis.ui.components.ToastUtil.showShort

@Composable
fun HomePage(
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
        } catch (_: CancellationException){
            // 大部分情况下取消的是SnackBar
        } catch (e: Exception){
            showShort(context, "设置仪表盘错误：${e.message}")
            Log.e("Home", "${e.message}\n类型${e.javaClass}")
        }
    }
    LaunchedEffect(Unit) {
        if(!LoadedFlag.isHomeLoaded){
            reloadInternet()
            LoadedFlag.isHomeLoaded = true
        }
    }
    ScreenBase(
        modifier,
        snackBarHost = { SnackbarHostState()},
        topBar = {
            Row (
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    "首页",
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
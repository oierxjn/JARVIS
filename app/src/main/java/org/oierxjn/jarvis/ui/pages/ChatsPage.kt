package org.oierxjn.jarvis.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.oierxjn.jarvis.R
import org.oierxjn.jarvis.ScreenBase
import org.oierxjn.jarvis.model.ChatType
import org.oierxjn.jarvis.model.DataModel
import org.oierxjn.jarvis.model.LoadedFlag
import org.oierxjn.jarvis.ui.components.ToastUtil.showShort

@Composable
fun ChatsPage(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }


    suspend fun reloadInternet(){
        try {

        } catch (e: Exception){

        }
    }

    LaunchedEffect(Unit) {
        if(!LoadedFlag.isChatsLoaded){
            // TODO
            LoadedFlag.isChatsLoaded = true
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
                    "聊天",
                    modifier = Modifier.padding(16.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                )
                IconButton(
                    {},
                    Modifier.padding(end = 0.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_24dp),
                        contentDescription = "添加监控"
                    )
                }
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
        LazyColumn(
            Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp) // 水平方向16dp内边距
        ) {
            items(DataModel.chatListModel.chats){ chat ->
                Card(
                    Modifier
                        .padding(vertical = 5.dp)
                        .fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            ,
                    ){
                        Column (
                            Modifier
                        ){
                            Text(chat.name)
                            Text(ChatType[chat.chat_type] ?: "未知类型")
                        }
                    }
                }
            }
        }
    }
}
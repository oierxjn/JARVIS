package org.oierxjn.jarvis.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 仪表盘统计数据（对应JSON结构）
 * @property chatsCount 监控聊天数 (JSON: chats_count)
 * @property messagesCount 消息总数 (JSON: messages_count)
 * @property pendingTasksCount 待办任务数 (JSON: pending_tasks_count)
 * @property summariesCount AI总结数 (JSON: summaries_count)
 */
@Serializable // 核心注解：标记为可序列化
data class DashboardStats(
    @SerialName("chats_count") // 映射JSON的下划线字段
    val chatsCount: Int,

    @SerialName("messages_count")
    val messagesCount: Int,

    @SerialName("pending_tasks_count")
    val pendingTasksCount: Int,

    @SerialName("summaries_count")
    val summariesCount: Int
)

data class StatItem(
    val icon: Int,
    val count: String,
    val label: String,
    val iconTint: Color
)

@Composable
fun StatCard(item: StatItem){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = item.label,
                tint = item.iconTint,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = item.count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

class DashboardViewModel: ViewModel(){
    private val _chatsCount = MutableStateFlow("0")    // 监控聊天
    private val _messagesCount = MutableStateFlow("0") // 消息总数
    private val _pendingTasksCount = MutableStateFlow("0")     // 待办任务
    private val _summariesCount = MutableStateFlow("0")       // AI总结

    val chatsCount: StateFlow<String> = _chatsCount.asStateFlow()
    val messagesCount: StateFlow<String> = _messagesCount.asStateFlow()
    val pendingTasksCount: StateFlow<String> = _pendingTasksCount.asStateFlow()
    val summariesCount: StateFlow<String> = _summariesCount.asStateFlow()

    fun updateChatCount(count: String){ _chatsCount.value = count }
    fun updateMessageCount(count: String){ _messagesCount.value = count }
    fun updateTodoCount(count: String){ _pendingTasksCount.value = count }
    fun updateAiCount(count: String){ _summariesCount.value = count }

    fun syncFromDataModel(){
        this.updateChatCount(DataModel.dashBoardStats.chatsCount.toString())
        this.updateMessageCount(DataModel.dashBoardStats.messagesCount.toString())
        this.updateTodoCount(DataModel.dashBoardStats.pendingTasksCount.toString())
        this.updateAiCount(DataModel.dashBoardStats.summariesCount.toString())
    }
}
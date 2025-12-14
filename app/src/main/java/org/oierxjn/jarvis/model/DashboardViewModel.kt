package org.oierxjn.jarvis.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import org.oierxjn.jarvis.R

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
    private val _chatCount = MutableStateFlow("0")    // 监控聊天
    private val _messageCount = MutableStateFlow("0") // 消息总数
    private val _todoCount = MutableStateFlow("0")     // 待办任务
    private val _aiCount = MutableStateFlow("0")       // AI总结

    val chatCount: StateFlow<String> = _chatCount.asStateFlow()
    val messageCount: StateFlow<String> = _messageCount.asStateFlow()
    val todoCount: StateFlow<String> = _todoCount.asStateFlow()
    val aiCount: StateFlow<String> = _aiCount.asStateFlow()

    fun updateChatCount(count: String){ _chatCount.value = count }
    fun updateMessageCount(count: String){ _messageCount.value = count }
    fun updateTodoCount(count: String){ _todoCount.value = count }
    fun updateAiCount(count: String){ _aiCount.value = count }
}
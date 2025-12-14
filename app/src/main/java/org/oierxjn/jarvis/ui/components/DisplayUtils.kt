package org.oierxjn.jarvis.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * M3风格自定义网格组件（非Lazy，适合少量固定子项）
 * @param columns 列数（比如2列）
 * @param horizontalSpacing 列之间的水平间距
 * @param verticalSpacing 行之间的垂直间距
 * @param items 子项列表（任意类型）
 * @param modifier 组件修饰符（支持wrapContentHeight等）
 * @param itemContent 子项的Compose布局（接收单个item，返回UI）
 */
@Composable
fun <T> M3CustomGrid(
    columns: Int,
    items: List<T>,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 0.dp,
    verticalSpacing: Dp = 0.dp,
    itemContent: @Composable (T) -> Unit
) {
    val rows = if (items.isEmpty()) 0 else (items.size + columns - 1) / columns

    Column(
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        modifier = modifier
    ) {
        // 遍历每一行（用forEach实现行的循环）
        (0 until rows).forEach { rowIndex ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 遍历当前行的每一列（用forEach实现列的循环）
                (0 until columns).forEach { columnIndex ->
                    // 计算当前item的索引：行号×列数 + 列号
                    val itemIndex = rowIndex * columns + columnIndex
                    if (itemIndex < items.size) {
                        // 有item时，渲染子项，占满列宽（weight=1）
                        Box(modifier = Modifier.weight(1f)) {
                            itemContent(items[itemIndex])
                        }
                    } else {
                        // 无item时，填充空白（占满列宽，保持布局对齐）
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
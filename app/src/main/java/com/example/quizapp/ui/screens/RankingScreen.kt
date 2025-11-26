package com.example.quizapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.data.model.RankingItem
import com.example.quizapp.viewmodel.RankingViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 랭킹 화면.
 *
 * @param onBack 뒤로가기 버튼 클릭 시 호출되는 콜백
 * @param viewModel 랭킹 데이터와 필터 상태를 관리하는 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    onBack: () -> Unit,
    viewModel: RankingViewModel = viewModel()
) {
    val rankingList by viewModel.filteredRankingList.collectAsState()
    val selectedTopic by viewModel.selectedTopic.collectAsState()
    val topics = viewModel.topicList

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("랭킹") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 주제 필터 칩 영역
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(topics) { topicName ->
                    FilterChip(
                        selected = (topicName == selectedTopic),
                        onClick = { viewModel.selectTopic(topicName) },
                        label = { Text(topicName) }
                    )
                }
            }

            // 랭킹 리스트 영역
            if (rankingList.isEmpty()) {
                Text("해당 주제의 랭킹 기록이 없습니다.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(rankingList) { index, item ->
                        // 아이템 등장 애니메이션
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(item) {
                            delay(index * 50L)
                            visible = true
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(300)) +
                                    slideInHorizontally(
                                        animationSpec = tween(300),
                                        initialOffsetX = { it / 2 }
                                    )
                        ) {
                            RankingItemCard(rank = index + 1, item = item)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 하나의 랭킹 항목을 카드로 표시합니다.
 *
 * @param rank 등수 (1부터 시작)
 * @param item 표시할 랭킹 데이터
 */
@Composable
fun RankingItemCard(
    rank: Int,
    item: RankingItem
) {
    // 1~3위에 대한 특별 색상
    val medalColor = when (rank) {
        1 -> Color(0xFFFFD700) // 금색
        2 -> Color(0xFFC0C0C0) // 은색
        3 -> Color(0xFFCD7F32) // 동색
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val isTopThree = rank <= 3

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isTopThree) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isTopThree) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 등수 (메달 아이콘 포함)
            Box(
                modifier = Modifier.width(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isTopThree) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "$rank 위",
                        tint = medalColor,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Text(
                        text = "$rank",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 점수 / 주제 / 날짜
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "점수: ${item.score} / ${item.totalQuestions}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.topicName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatDate(item.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 정답률 표시
            val percentage = if (item.totalQuestions > 0) {
                (item.score * 100 / item.totalQuestions)
            } else 0

            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    percentage >= 80 -> Color(0xFF4CAF50)
                    percentage >= 60 -> Color(0xFFFFA726)
                    else -> Color(0xFFE53935)
                }
            )
        }
    }
}

/**
 * 타임스탬프를 사람이 읽을 수 있는 날짜 문자열로 변환합니다.
 *
 * @param timestamp 밀리초 단위 시간 값
 * @return yyyy.MM.dd HH:mm 형식의 문자열
 */
private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

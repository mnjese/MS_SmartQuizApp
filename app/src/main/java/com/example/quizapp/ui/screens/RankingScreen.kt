package com.example.quizapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.data.model.RankingItem
import com.example.quizapp.viewmodel.RankingViewModel
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
                        RankingItemCard(rank = index + 1, item = item)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 등수
            Text(
                text = "$rank.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 점수 / 주제 / 날짜
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "점수: ${item.score} / ${item.totalQuestions}",
                    style = MaterialTheme.typography.bodyLarge
                )
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

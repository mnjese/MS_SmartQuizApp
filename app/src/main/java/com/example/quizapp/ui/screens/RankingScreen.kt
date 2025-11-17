package com.example.quizapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

@OptIn(ExperimentalMaterial3Api::class) // 2. FilterChip을 위해 추가
@Composable
fun RankingScreen(
    viewModel: RankingViewModel = viewModel()
) {
    val rankingList by viewModel.filteredRankingList.collectAsState()
    val selectedTopic by viewModel.selectedTopic.collectAsState()
    val topics = viewModel.topicList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp), // 상단 패딩만
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "랭킹",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 주제 필터 버튼 (가로 스크롤)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp), // 좌우 패딩
            horizontalArrangement = Arrangement.spacedBy(8.dp) // 버튼 사이 간격
        ) {
            items(topics) { topicName ->
                FilterChip(
                    selected = (topicName == selectedTopic),
                    onClick = { viewModel.selectTopic(topicName) },
                    label = { Text(topicName) }
                )
            }
        }

        // 5. [수정] 랭킹 목록 (padding 수정)
        if (rankingList.isEmpty()) {
            Text("해당 주제의 랭킹 기록이 없습니다.")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp), // 좌우 패딩만
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

// 랭킹 아이템을 표시할 카드 Composable
@Composable
fun RankingItemCard(rank: Int, item: RankingItem) {
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
            // 등수 (예: "1.")
            Text(
                text = "$rank.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 점수 및 날짜
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "점수: ${item.score} / ${item.totalQuestions}",
                    style = MaterialTheme.typography.bodyLarge
                )
                //주제 이름 표시
                Text(
                    text = item.topicName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = formatDate(item.timestamp), // 4. 날짜 포맷팅
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Long(timestamp)를 날짜 문자열(String)로 변환
private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
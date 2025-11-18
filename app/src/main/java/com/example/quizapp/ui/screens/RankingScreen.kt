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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

/**
 * 저장된 퀴즈 기록을 주제별로 필터링하여 랭킹 형태로 보여주는 화면.
 *
 * @param viewModel 랭킹 데이터와 필터 상태를 관리하는 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class) // FilterChip을 사용하기 위해 필요
@Composable
fun RankingScreen(
    onBack: () -> Unit,
    viewModel: RankingViewModel = viewModel()
) {
    // ViewModel로부터 필터링된 랭킹 리스트를 State로 구독합니다.
    val rankingList by viewModel.filteredRankingList.collectAsState()
    // ViewModel로부터 현재 선택된 주제 필터를 State로 구독합니다.
    val selectedTopic by viewModel.selectedTopic.collectAsState()
    // ViewModel로부터 표시할 전체 주제 리스트("전체" 포함)를 가져옵니다.
    val topics = viewModel.topicList

    // 화면 전체 레이아웃 (세로 배치)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp), // 화면 상단에만 패딩을 줍니다.
        horizontalAlignment = Alignment.CenterHorizontally // 가로 중앙 정렬
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
        }
        // 화면 타이틀
        Text(
            text = "랭킹",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 주제 필터링 칩 (가로 스크롤 가능)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp), // 랭킹 목록과의 간격
            contentPadding = PaddingValues(horizontal = 16.dp), // 좌우 여백
            horizontalArrangement = Arrangement.spacedBy(8.dp) // 칩 사이의 간격
        ) {
            // topics (List<String>) 리스트를 순회하며 FilterChip을 생성합니다.
            items(topics) { topicName ->
                FilterChip(
                    // 현재 칩의 주제(topicName)가 선택된 주제(selectedTopic)와 같은지 여부
                    selected = (topicName == selectedTopic),
                    // 칩 클릭 시 ViewModel에 주제 변경을 알립니다.
                    onClick = { viewModel.selectTopic(topicName) },
                    // 칩에 표시될 텍스트
                    label = { Text(topicName) }
                )
            }
        }

        // 랭킹 목록 표시 영역
        if (rankingList.isEmpty()) {
            // 필터링된 결과가 없을 경우 메시지 표시
            Text("해당 주제의 랭킹 기록이 없습니다.")
        } else {
            // 랭킹 목록 (세로 스크롤)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp), // 좌우 여백
                contentPadding = PaddingValues(vertical = 8.dp), // 상하 여백
                verticalArrangement = Arrangement.spacedBy(8.dp) // 아이템 사이 간격
            ) {
                // rankingList를 순회하며 인덱스(등수)와 아이템을 받아 카드를 생성합니다.
                itemsIndexed(rankingList) { index, item ->
                    RankingItemCard(rank = index + 1, item = item)
                }
            }
        }
    }
}


/**
 * 하나의 랭킹 항목을 카드 형태로 표시합니다.
 *
 * @param rank 등수 (1부터 시작)
 * @param item 표시할 랭킹 데이터
 */
@Composable
fun RankingItemCard(rank: Int, item: RankingItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // 카드 내용물 (가로 배치)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically, // 세로 중앙 정렬
            horizontalArrangement = Arrangement.SpaceBetween // 양쪽 끝으로 분산
        ) {
            // 등수 표시 (예: "1.")
            Text(
                text = "$rank.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // 등수와 정보 사이 간격
            Spacer(modifier = Modifier.width(16.dp))

            // 점수, 주제, 날짜 (세로 배치)
            // .weight(1f)로 등수를 제외한 나머지 가로 공간을 모두 차지합니다.
            Column(modifier = Modifier.weight(1f)) {
                // 점수 (예: "점수: 8 / 10")
                Text(
                    text = "점수: ${item.score} / ${item.totalQuestions}",
                    style = MaterialTheme.typography.bodyLarge
                )
                // 퀴즈 주제 이름 (강조)
                Text(
                    text = item.topicName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                // 퀴즈 푼 날짜
                Text(
                    text = formatDate(item.timestamp), // Long -> String 변환
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Long 타입의 타임스탬프를 "yyyy.MM.dd HH:mm" 형식의 문자열로 변환하는 헬퍼 함수.
 *
 * @param timestamp 변환할 Long 값.
 * @return 포맷팅된 날짜 문자열.
 */
private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
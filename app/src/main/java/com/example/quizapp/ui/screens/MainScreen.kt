@file:Suppress("AssignedValueIsNeverRead")

package com.example.quizapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizapp.data.DummyData
import com.example.quizapp.data.local.AppDatabase
import com.example.quizapp.data.model.QuizTopic
import com.example.quizapp.ui.theme.GradientEnd
import com.example.quizapp.ui.theme.GradientStart

/**
 * 앱의 메인 화면.
 * - 퀴즈 주제를 선택하고 퀴즈/랭킹/오답 노트 화면으로 이동합니다.
 *
 * @param onNavigateToQuiz 선택된 주제 ID와 함께 퀴즈 화면으로 이동하는 콜백
 * @param onNavigateToRanking 랭킹 화면으로 이동하는 콜백
 * @param onNavigateToWrongAnswer 오답 노트 화면으로 이동하는 콜백
 * @param onExit 앱을 종료하는 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToQuiz: (Int) -> Unit,
    onNavigateToRanking: () -> Unit,
    onNavigateToWrongAnswer: () -> Unit,
    onExit: () -> Unit
) {
    var showExitDialog: Boolean by remember { mutableStateOf(false) }
    var menuExpanded: Boolean by remember { mutableStateOf(false) }

    // DB에서 통계 데이터 가져오기
    val context = LocalContext.current
    val quizDao = remember { AppDatabase.getDatabase(context).quizDao() }
    val rankings by quizDao.getAllRankings().collectAsState(initial = emptyList())
    val wrongAnswers by quizDao.getAllWrongAnswers().collectAsState(initial = emptyList())

    // 통계 계산
    val totalGames = rankings.size
    val averageScore = if (rankings.isNotEmpty()) {
        rankings.map { it.score * 100 / it.totalQuestions }.average().toInt()
    } else 0

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("앱 종료") },
            text = { Text("정말 앱을 종료하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onExit()
                    }
                ) { Text("종료") }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    val topics = DummyData.topics
    var selectedTopicId by remember { mutableStateOf<Int?>(null) }
    val scrollState = rememberScrollState()

    // 애니메이션
    val infiniteTransition = rememberInfiniteTransition(label = "main")

    // 상단 앱바 + 내용 영역을 위한 Scaffold
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                actions = {
                    // 우측 ⋮ 메뉴
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "메뉴"
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("앱 종료") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                showExitDialog = true
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 배경 장식 원들
            FloatingCircles(infiniteTransition)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // 헤더 섹션
                HeaderSection(infiniteTransition)

                Spacer(modifier = Modifier.height(24.dp))

                // 통계 카드 섹션
                StatisticsSection(
                    totalGames = totalGames,
                    averageScore = averageScore,
                    wrongCount = wrongAnswers.size
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 주제 선택 섹션
                Text(
                    text = "📚 주제를 선택하세요",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                topics.forEach { topic ->
                    val isSelected = (topic.id == selectedTopicId)
                    TopicCard(
                        topic = topic,
                        isSelected = isSelected,
                        onClick = {
                            selectedTopicId = if (selectedTopicId == topic.id) null else topic.id
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 퀴즈 시작 버튼
                AnimatedVisibility(
                    visible = (selectedTopicId != null),
                    enter = fadeIn() + expandVertically() + scaleIn(),
                    exit = fadeOut() + shrinkVertically() + scaleOut()
                ) {
                    Button(
                        onClick = {
                            selectedTopicId?.let { id ->
                                onNavigateToQuiz(id)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "퀴즈 시작하기",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                if (selectedTopicId != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 하단 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToRanking,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("랭킹")
                    }

                    OutlinedButton(
                        onClick = onNavigateToWrongAnswer,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("오답노트")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * 배경에 떠다니는 장식 원들
 */
@Composable
fun FloatingCircles(infiniteTransition: InfiniteTransition) {
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 큰 원 1
        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = (100 + offset1).dp)
                .size(120.dp)
                .alpha(0.1f)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                )
        )

        // 큰 원 2
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (200 + offset2).dp)
                .size(80.dp)
                .alpha(0.08f)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(GradientEnd, GradientStart)
                    )
                )
        )

        // 작은 원
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 60.dp, y = (-100 + offset1).dp)
                .size(60.dp)
                .alpha(0.06f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

/**
 * 헤더 섹션 (타이틀 + 서브타이틀)
 */
@Composable
fun HeaderSection(infiniteTransition: InfiniteTransition) {
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 아이콘 + 텍스트 로고 영역
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 아이콘
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Quiz,
                    contentDescription = null,
                    modifier = Modifier.size(34.dp),
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // 텍스트 로고
            Column {
                Text(
                    text = "스마트 퀴즈",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            offset = Offset(0f, 4f),
                            blurRadius = 10f
                        )
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.scale(titleScale)
                )

                // 보조 영문 타이틀
                Text(
                    text = "Smart Quiz",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "지식을 테스트하고 실력을 향상시키세요!",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/**
 * 통계 카드 섹션
 */
@Composable
fun StatisticsSection(
    totalGames: Int,
    averageScore: Int,
    wrongCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Quiz,
            value = "$totalGames",
            label = "총 게임"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Star,
            value = "$averageScore%",
            label = "평균 점수"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Psychology,
            value = "$wrongCount",
            label = "오답"
        )
    }
}

/**
 * 개별 통계 카드
 */
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String
) {
    Card(
        modifier = modifier.shadow(
            elevation = 2.dp,
            shape = RoundedCornerShape(16.dp),
            clip = false
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 퀴즈 주제를 표시하는 카드.
 *
 * @param topic 표시할 주제 정보
 * @param isSelected 현재 선택된 카드인지 여부
 * @param onClick 카드 클릭 시 실행되는 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicCard(
    topic: QuizTopic,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // 선택 시 스케일 애니메이션
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    // 배경색 애니메이션
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(durationMillis = 300),
        label = "containerColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 300),
        label = "contentColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            ),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = topic.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

/**
 * Android Studio 프리뷰에서 MainScreen을 확인하기 위한 Composable
 */
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // 프리뷰에서는 실제 탐색 로직이 필요 없으므로 빈 람다({})를 전달합니다.
    MainScreen({}, {}, {}, {})
}
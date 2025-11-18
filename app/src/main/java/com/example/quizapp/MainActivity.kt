package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizapp.ui.screens.MainScreen
import com.example.quizapp.ui.screens.QuizScreen
import com.example.quizapp.ui.screens.RankingScreen
import com.example.quizapp.ui.screens.ResultScreen
import com.example.quizapp.ui.screens.WrongAnswerScreen
import com.example.quizapp.ui.theme.QuizAppTheme

/**
 * 앱의 메인 Activity.
 *
 * - Jetpack Compose 기반 UI를 구성합니다.
 * - Navigation Host를 Activity 내부에서 관리합니다.
 * - 추후 Navigation 전용 파일로 분리하기 쉽게 구조화되어 있습니다.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QuizAppTheme {

                // 화면 전체 스캐폴드(TopBar/BottomBar 추가 가능)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    // 네비게이션 컨트롤러 생성
                    val navController = rememberNavController()

                    /**
                     * NavHost: 앱의 네비게이션 그래프
                     *
                     * startDestination: 앱 최초 진입 화면 ("main")
                     * modifier: Scaffold의 innerPadding 적용
                     */
                    NavHost(
                        navController = navController,
                        startDestination = "main",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        /**
                         * -----------------------------
                         * 메인 화면
                         * -----------------------------
                         * route = "main"
                         * - 퀴즈 주제 선택
                         * - 랭킹/오답 노트 이동
                         */
                        composable(route = "main") {
                            MainScreen(
                                onNavigateToQuiz = { topicId ->
                                    // topicId를 함께 전달 → quiz/{topicId}
                                    navController.navigate("quiz/$topicId")
                                },
                                onNavigateToRanking = {
                                    navController.navigate("ranking")
                                },
                                onNavigateToWrongAnswer = {
                                    navController.navigate("wrong_answer")
                                }
                            )
                        }

                        /**
                         * -----------------------------
                         * 퀴즈 화면
                         * -----------------------------
                         * route = "quiz/{topicId}"
                         *
                         * @argument topicId: 선택된 퀴즈 주제 ID
                         */
                        composable(
                            route = "quiz/{topicId}",
                            arguments = listOf(
                                navArgument("topicId") { type = NavType.IntType }
                            )
                        ) {
                            QuizScreen(
                                onNavigateToResult = { score, totalQuestions ->
                                    /**
                                     * 퀴즈 종료 → 결과 화면으로 이동
                                     * route = result/{score}/{totalQuestions}
                                     *
                                     * popUpTo("main"):
                                     * - 뒤로가기 시 퀴즈 화면으로 돌아가는 것을 방지
                                     */
                                    navController.navigate("result/$score/$totalQuestions") {
                                        popUpTo("main")
                                    }
                                }
                            )
                        }

                        /**
                         * -----------------------------
                         * 결과 화면
                         * -----------------------------
                         * route = "result/{score}/{totalQuestions}"
                         *
                         * @argument score: 사용자 점수
                         * @argument totalQuestions: 총 문제 수
                         */
                        composable(
                            route = "result/{score}/{totalQuestions}",
                            arguments = listOf(
                                navArgument("score") { type = NavType.IntType },
                                navArgument("totalQuestions") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->

                            // 전달받은 인자 꺼내기
                            val score = backStackEntry.arguments?.getInt("score") ?: 0
                            val totalQuestions =
                                backStackEntry.arguments?.getInt("totalQuestions") ?: 0

                            ResultScreen(
                                score = score,
                                totalQuestions = totalQuestions,
                                onNavigateToMain = {
                                    // 메인 화면으로 이동 (기존 스택 정리)
                                    navController.navigate("main") {
                                        popUpTo("main")
                                    }
                                }
                            )
                        }

                        /**
                         * -----------------------------
                         * 랭킹 화면
                         * -----------------------------
                         * route = "ranking"
                         */
                        composable(route = "ranking") {
                            RankingScreen()
                        }

                        /**
                         * -----------------------------
                         * 오답 노트 화면
                         * -----------------------------
                         * route = "wrong_answer"
                         */
                        composable(route = "wrong_answer") {
                            WrongAnswerScreen()
                        }
                    }
                }
            }
        }
    }
}

package com.example.quizapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.quizapp.ui.screens.MainScreen
import com.example.quizapp.ui.screens.QuizScreen
import com.example.quizapp.ui.screens.RankingScreen
import com.example.quizapp.ui.screens.ResultScreen
import com.example.quizapp.ui.screens.WrongAnswerScreen

/**
 * AppNavigation
 * -------------------------------
 * 분리된 네비게이션 그래프를 관리하는 파일.
 * MainActivity는 NavHostController만 전달하고,
 * 실제 라우팅 구조는 모두 이 파일에서 담당합니다.
 *
 * @param navController 네비게이션 컨트롤러
 * @param innerPadding Scaffold로부터 전달되는 패딩 값
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = Modifier.padding(innerPadding)
    ) {
        // 메인 화면
        composable("main") {
            MainScreen(
                onNavigateToQuiz = { topicId ->
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

        // 퀴즈 화면
        composable(
            route = "quiz/{topicId}",
            arguments = listOf(navArgument("topicId") { type = NavType.IntType })
        ) {
            QuizScreen(
                onNavigateToResult = { score, totalQuestions ->
                    navController.navigate("result/$score/$totalQuestions") {
                        popUpTo("main")
                    }
                }
            )
        }

        // 결과 화면
        composable(
            route = "result/{score}/{totalQuestions}",
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("totalQuestions") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val totalQuestions = backStackEntry.arguments?.getInt("totalQuestions") ?: 0

            ResultScreen(
                score = score,
                totalQuestions = totalQuestions,
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("main")
                    }
                }
            )
        }

        // 랭킹 화면
        composable("ranking") {
            RankingScreen()
        }

        // 오답 노트 화면
        composable("wrong_answer") {
            WrongAnswerScreen()
        }
    }
}

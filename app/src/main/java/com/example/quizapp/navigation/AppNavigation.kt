package com.example.quizapp.navigation

import androidx.activity.compose.LocalActivity
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
 * 앱의 라우팅 경로를 타입 안전하게 관리하기 위한 Screen 정의.
 */
sealed class Screen(val route: String) {
    object Main : Screen("main")

    object Quiz : Screen("quiz/{topicId}") {
        fun createRoute(topicId: Int) = "quiz/$topicId"
    }

    object Result : Screen("result/{score}/{totalQuestions}") {
        fun createRoute(score: Int, total: Int) = "result/$score/$total"
    }

    object Ranking : Screen("ranking")
    object WrongAnswer : Screen("wrong_answer")
}

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
        startDestination = Screen.Main.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        // 메인 화면
        composable(Screen.Main.route) {
            val activity = LocalActivity.current

            MainScreen(
                onNavigateToQuiz = { topicId ->
                    navController.navigate(Screen.Quiz.createRoute(topicId))
                },
                onNavigateToRanking = {
                    navController.navigate(Screen.Ranking.route)
                },
                onNavigateToWrongAnswer = {
                    navController.navigate(Screen.WrongAnswer.route)
                },
                onExit = {
                    activity?.finish()
                }
            )
        }

        // 퀴즈 화면
        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument("topicId") { type = NavType.IntType })
        ) {
            QuizScreen(
                onNavigateToResult = { score, totalQuestions ->
                    navController.navigate(Screen.Result.createRoute(score, totalQuestions)) {
                        popUpTo(Screen.Main.route)
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route)
                    }
                }
            )
        }

        // 결과 화면
        composable(
            route = Screen.Result.route,
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
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route)
                    }
                }
            )
        }

        // 랭킹 화면
        composable(Screen.Ranking.route) {
            RankingScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 오답 노트 화면
        composable(Screen.WrongAnswer.route) {
            WrongAnswerScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
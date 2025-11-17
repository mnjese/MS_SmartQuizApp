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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "main", // 시작 화면을 "main"으로 설정
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 각 화면을 "경로(route)"와 Composable 함수로 연결

                        composable(route = "main") {
                            MainScreen(
                                onNavigateToQuiz = { topicId ->
                                    // "quiz/1", "quiz/2" 와 같은 경로로 이동
                                    navController.navigate("quiz/$topicId")
                                },
                                onNavigateToRanking = { navController.navigate("ranking") },
                                onNavigateToWrongAnswer = { navController.navigate("wrong_answer") }
                            )
                        }

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

                        composable(route = "ranking") {
                            RankingScreen()
                        }

                        composable(route = "wrong_answer") {
                            WrongAnswerScreen()
                        }
                    }
                }
            }
        }
    }
}


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

                    // 1. Navigation Controller 생성
                    val navController = rememberNavController()

                    // 2. Navigation Host 설정
                    NavHost(
                        navController = navController,
                        startDestination = "main", // 시작 화면을 "main"으로 설정
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 3. 각 화면을 "경로(route)"와 Composable 함수로 연결

                        composable(route = "main") {
                            MainScreen(
                                // 람다가 Int(topicId)를 받도록 수정
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
                        ) { backStackEntry ->
                            // 전달받은 topicId를 Int로 추출
                            val topicId = backStackEntry.arguments?.getInt("topicId") ?: 1 // 기본값 1
                            QuizScreen(
                                topicId = topicId, // QuizScreen에 전달
                                onNavigateToResult = {
                                    navController.navigate("result") {
                                        popUpTo("main")
                                    }
                                }
                            )
                        }

                        composable(route = "result") {
                            ResultScreen(
                                onNavigateToMain = {
                                    navController.navigate("main") {
                                        // 결과 화면도 뒤로가기 스택에서 제거
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


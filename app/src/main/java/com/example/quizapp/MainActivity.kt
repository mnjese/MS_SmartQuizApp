package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                                // MainScreen이 요청한 람다를 실제 탐색 코드로 구현
                                onNavigateToQuiz = { navController.navigate("quiz") },
                                onNavigateToRanking = { navController.navigate("ranking") },
                                onNavigateToWrongAnswer = { navController.navigate("wrong_answer") }
                            )
                        }

                        composable(route = "quiz") {
                            QuizScreen(
                                onNavigateToResult = {
                                    navController.navigate("result") {
                                        // 퀴즈 화면은 뒤로가기 스택에서 제거
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


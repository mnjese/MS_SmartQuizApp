package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.navigation.AppNavigation
import com.example.quizapp.ui.theme.QuizAppTheme

/**
 * 앱의 메인 Activity.
 * - Jetpack Compose 기반 UI를 구성합니다.
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

                    // AppNavigation: 분리된 네비게이션 그래프를 적용하여 화면 전환을 관리
                    AppNavigation(
                        navController = navController,
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}

# MS_SmartQuizApp

동국대학교 모바일소프트웨어 과목의 기말 프로젝트인 스마트 퀴즈 앱 레포지토리입니다.

---

## 주요 기능

- 퀴즈 주제 선택 (일반 상식 / 과학 / 영화)
- 4지선다 퀴즈 풀이
- 정답/오답 시 사운드 재생
- 점수 계산 및 정답/오답 표시
- 결과 화면에서 정답/오답 상세 확인
- 랭킹 저장 및 조회 (Room DB)
- 오답 노트 저장, 조회 및 삭제 기능
- Compose Navigation으로 화면 전환
- 뒤로가기 UX (종료 확인, 단순 뒤로가기)
---

## 아키텍처 개요

- **UI 레이어**  
  MainScreen, QuizScreen, ResultScreen, RankingScreen, WrongAnswerScreen

- **네비게이션**  
  AppNavigation + sealed class 기반 Screen 라우팅

- **ViewModel 레이어**  
  QuizViewModel, ResultViewModel, RankingViewModel, WrongAnswerViewModel

- **데이터 레이어**  
  Room Database (RankingItem, WrongAnswer)  
  QuizDao, Converters  
  DummyData(문제 데이터), QuizResultHolder(임시 저장)

---

## 프로젝트 구조

```
app/
 ├─ data/
 │   ├─ local/ (Room Database)
 │   ├─ model/ (Entity 및 Model 클래스)
 │   ├─ DummyData.kt
 │   └─ QuizResultHolder.kt
 ├─ navigation/
 │   └─ AppNavigation.kt
 ├─ ui/screens/
 ├─ viewmodel/
 └─ MainActivity.kt
```

---

## Todo
- 디자인 마무리

---

## Todo-Optional
- 기타 개선사항
- Repository 레이어 추가해 ViewModel-DB 구조 더 깔끔하게 만들기
- QuestionDataSource 인터페이스로 DummyData 추상화
- 단위 테스트(ViewModel 중심) 도입
- UI 컴포넌트 컴포즈 Preview 강화

---
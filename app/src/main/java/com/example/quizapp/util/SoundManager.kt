package com.example.quizapp.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator

/**
 * 퀴즈 효과음을 관리하는 싱글톤 클래스.
 *
 * - 정답/오답 효과음을 SoundPool로 재생합니다.
 * - 앱 전체에서 하나의 인스턴스만 사용합니다.
 */
class SoundManager private constructor(context: Context) {

    private val soundPool: SoundPool
    private var correctSoundId: Int = 0
    private var wrongSoundId: Int = 0
    private var isLoaded: Boolean = false
    private var useToneGenerator: Boolean = false
    private var toneGenerator: ToneGenerator? = null

    init {
        // SoundPool 초기화
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        // 사운드 로드 완료 리스너
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isLoaded = true
            }
        }

        // 효과음 파일 로드 시도 (리소스가 있는 경우에만)
        val correctResId = context.resources.getIdentifier("correct", "raw", context.packageName)
        val wrongResId = context.resources.getIdentifier("wrong", "raw", context.packageName)

        if (correctResId != 0 && wrongResId != 0) {
            try {
                correctSoundId = soundPool.load(context, correctResId, 1)
                wrongSoundId = soundPool.load(context, wrongResId, 1)
            } catch (e: Exception) {
                useToneGenerator = true
            }
        } else {
            // 효과음 파일이 없는 경우 ToneGenerator 사용
            useToneGenerator = true
        }

        // ToneGenerator 초기화
        if (useToneGenerator) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 정답 효과음을 재생합니다.
     */
    fun playCorrectSound() {
        if (useToneGenerator) {
            // ToneGenerator로 높은 톤 재생 (정답)
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        } else if (isLoaded && correctSoundId != 0) {
            soundPool.play(correctSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    /**
     * 오답 효과음을 재생합니다.
     */
    fun playWrongSound() {
        if (useToneGenerator) {
            // ToneGenerator로 낮은 톤 재생 (오답)
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 150)
        } else if (isLoaded && wrongSoundId != 0) {
            soundPool.play(wrongSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    /**
     * 리소스를 해제합니다.
     */
    fun release() {
        soundPool.release()
        toneGenerator?.release()
        toneGenerator = null
        instance = null
    }

    companion object {
        @Volatile
        private var instance: SoundManager? = null

        /**
         * SoundManager 싱글톤 인스턴스를 가져옵니다.
         *
         * @param context Application Context
         * @return SoundManager 인스턴스
         */
        fun getInstance(context: Context): SoundManager {
            return instance ?: synchronized(this) {
                instance ?: SoundManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}

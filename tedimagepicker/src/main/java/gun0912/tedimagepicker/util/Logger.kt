package gun0912.tedimagepicker.util

import android.annotation.SuppressLint
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date

/**
 * 앱의 로깅을 담당하는 유틸리티 클래스
 *
 * 디버그 모드에서 다양한 레벨의 로그를 출력하고, 필요시 파일로 저장
 * 스택 트레이스 정보를 포함하여 로그 발생 위치를 추적 시 필요
 */
class Logger {

    companion object {

        // 기본 스택 번호 - 일반적인 로그 출력 시 사용하는 기본 인덱스
        private const val defaultStackNumber = 2

        // 로그 활성화 여부 - true: 로그 출력 활성화, false: 로그 출력 비활성화
        @JvmStatic
        var isEnabled: Boolean = false

        // 로그 파일 저장 경로 - null이 아닌 경우 로그를 파일로 저장
        @JvmStatic
        var fileTarget: String? = null

        // 파일 로깅을 위한 FileWriter 객체
        private var fileWriter: FileWriter? = null

        /**
         * 디버그 레벨 로그를 출력하는 메서드
         *
         * @param message 출력할 메시지 (기본값: null)
         * @param stackNumber 스택 트레이스에서 사용할 인덱스 (기본값: defaultStackNumber)
         */
        @JvmStatic
        fun verbose(message: Any? = null, stackNumber: Int = defaultStackNumber) {
            val stack = Throwable().stackTrace[stackNumber]
            printLog("\uD83D\uDD2C VERBOSE", stack, message.toString())
        }

        /**
         * 디버그 레벨 로그를 출력하는 메서드
         *
         * @param message 출력할 메시지 (기본값: null)
         * @param stackNumber 스택 트레이스에서 사용할 인덱스 (기본값: defaultStackNumber)
         */
        @JvmStatic
        fun debug(message: Any? = null, stackNumber: Int = defaultStackNumber) {
            val stack = Throwable().stackTrace[stackNumber]
            printLog("🐛 DEBUG", stack, message.toString())
        }

        /**
         * 정보 레벨 로그를 출력하는 메서드
         *
         * @param message 출력할 메시지 (기본값: "")
         * @param stackNumber 스택 트레이스에서 사용할 인덱스 (기본값: defaultStackNumber)
         */
        @JvmStatic
        fun info(message: String? = "", stackNumber: Int = defaultStackNumber) {
            val stack = Throwable().stackTrace[stackNumber]
            printLog("ℹ️ INFO", stack, message)
        }

        /**
         * 경고 레벨 로그를 출력하는 메서드
         *
         * @param message 출력할 메시지 (기본값: "")
         * @param stackNumber 스택 트레이스에서 사용할 인덱스 (기본값: defaultStackNumber)
         */
        @JvmStatic
        fun warning(message: String? = "", stackNumber: Int = defaultStackNumber) {
            val stack = Throwable().stackTrace[stackNumber]
            printLog("⚠️ WARNING", stack, message)
        }

        /**
         * 에러 레벨 로그를 출력하는 메서드
         *
         * @param message 출력할 메시지 (기본값: "")
         * @param stackNumber 스택 트레이스에서 사용할 인덱스 (기본값: defaultStackNumber)
         */
        @JvmStatic
        fun error(message: String? = "", stackNumber: Int = defaultStackNumber) {
            val stack = Throwable().stackTrace[stackNumber]
            printLog("\uD83D\uDEAB ERROR", stack, message)

        }

        /**
         * 로그를 콘솔에 출력하는 메서드
         * 로그 레벨, 타임스탬프, 클래스명, 메서드명, 라인 번호, 메시지를 포함
         *
         * @param level 로그 레벨 (이모지 포함)
         * @param stack 스택 트레이스 요소
         * @param message 출력할 메시지
         */
        private fun printLog(level: String, stack: StackTraceElement, message: String?) {
            println("$level ${timeStamp()} ${stack.className.substringAfterLast(".")}.${stack.methodName}(${stack.lineNumber}) $message")
        }

        /**
         * 현재 시간을 타임스탬프 형식으로 반환하는 메서드
         *
         * @return "yyyy-MM-dd HH:mm:ss" 형식의 타임스탬프 문자열
         */
        @SuppressLint("SimpleDateFormat")
        private fun timeStamp(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        }
    }
}
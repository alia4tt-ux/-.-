package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CategoryCultural
import com.example.ui.theme.CategoryReligious
import com.example.ui.theme.CategoryScientific
import com.example.ui.theme.CategoryStories

enum class BookCategory(val titleArabic: String, val subtitleArabic: String) {
    ALL("الكل", "جميع الأقسام"),
    RELIGIOUS("كتب دينية", "علوم القرآن والحديث والسيرة"),
    SCIENTIFIC("كتب علمية", "الفلك والفيزياء والتقنية والأحياء"),
    CULTURAL("كتب ثقافية", "الحضارات والفكر وتطوير الذات"),
    STORIES("كتب قصص", "نوادر التراث وروايات الأدب العالمي");

    fun getColor(): Color {
        return when (this) {
            ALL -> Color(0xFF135D54)
            RELIGIOUS -> CategoryReligious
            SCIENTIFIC -> CategoryScientific
            CULTURAL -> CategoryCultural
            STORIES -> CategoryStories
        }
    }
}

data class BookChapter(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val content: String
)

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val category: BookCategory,
    val description: String,
    val pagesCount: Int,
    val readTimeMinutes: Int,
    val rating: Float,
    val publishYear: String,
    val accentColorHex: Long,
    val coverSymbol: String,
    val chapters: List<BookChapter>
)

enum class ReaderThemeMode(val titleArabic: String) {
    SEPIA("ورق كلاسيكي"),
    PAPER("نهاري ناصع"),
    DARK("ليلي مريح")
}

data class ReaderSettings(
    val fontSizeSp: Float = 19f,
    val themeMode: ReaderThemeMode = ReaderThemeMode.SEPIA,
    val lineSpacingMultiplier: Float = 1.65f
)

data class ReadingSession(
    val bookId: String,
    val chapterIndex: Int = 0,
    val scrollPercent: Float = 0f,
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    val isFinished: Boolean = false
)

object DeveloperConstants {
    const val DEVELOPER_NAME = "علي خالد الكندي"
    const val DEVELOPER_TITLE = "مطور نظم وبرمجيات"
    const val APP_NAME = "مكتبتي"
    const val APP_VERSION = "الإصدار 1.0.0"
    const val DEVELOPER_BIO = "تم تصميم وبرمجة وتطوير تطبيق «مكتبتي» بجهد وإشراف المطور علي خالد الكندي، ليكون منصة عربية معرفية راقية تتيح للقراء الاستمتاع بكنوز المعرفة في شتى الحقول: الدينية، العلمية، الثقافية، والقصصية بأحدث تقنيات Jetpack Compose وتصميم واجهات متناغم."
    const val DEVELOPER_EMAIL = "contact@maktabati.app"
    const val DEDICATION = "إهداء إلى كل شغوف بالقراءة والبحث العلمي والمعرفة الإنسانية."
}

package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Book
import com.example.data.model.BookCategory
import com.example.data.model.ReaderSettings
import com.example.data.model.ReaderThemeMode
import com.example.data.model.ReadingSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LibraryRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("maktabati_prefs", Context.MODE_PRIVATE)

    private val allBooksList: List<Book> =
        ReligiousBooksData.books +
        ScientificBooksData.books +
        CulturalBooksData.books +
        StoriesBooksData.books

    private val _favoriteIds = MutableStateFlow<Set<String>>(loadFavorites())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _readerSettings = MutableStateFlow(loadReaderSettings())
    val readerSettings: StateFlow<ReaderSettings> = _readerSettings.asStateFlow()

    private val _lastSession = MutableStateFlow(loadLastSession())
    val lastSession: StateFlow<ReadingSession?> = _lastSession.asStateFlow()

    fun getAllBooks(): List<Book> = allBooksList

    fun getBooksByCategory(category: BookCategory): List<Book> {
        return if (category == BookCategory.ALL) {
            allBooksList
        } else {
            allBooksList.filter { it.category == category }
        }
    }

    fun getBookById(id: String): Book? {
        return allBooksList.find { it.id == id }
    }

    fun searchBooks(query: String, category: BookCategory = BookCategory.ALL): List<Book> {
        val trimmed = query.trim()
        val baseList = getBooksByCategory(category)
        if (trimmed.isEmpty()) return baseList

        return baseList.filter { book ->
            book.title.contains(trimmed, ignoreCase = true) ||
            book.author.contains(trimmed, ignoreCase = true) ||
            book.description.contains(trimmed, ignoreCase = true) ||
            book.chapters.any {
                it.title.contains(trimmed, ignoreCase = true) ||
                it.content.contains(trimmed, ignoreCase = true)
            }
        }
    }

    fun toggleFavorite(bookId: String) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(bookId)) {
            current.remove(bookId)
        } else {
            current.add(bookId)
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        _favoriteIds.value = current
    }

    fun isFavorite(bookId: String): Boolean = _favoriteIds.value.contains(bookId)

    fun saveReadingProgress(bookId: String, chapterIndex: Int, percent: Float = 0f, isFinished: Boolean = false) {
        val session = ReadingSession(
            bookId = bookId,
            chapterIndex = chapterIndex,
            scrollPercent = percent,
            lastReadTimestamp = System.currentTimeMillis(),
            isFinished = isFinished
        )
        prefs.edit()
            .putString(KEY_LAST_BOOK_ID, bookId)
            .putInt(KEY_LAST_CHAPTER_IDX, chapterIndex)
            .putFloat(KEY_LAST_SCROLL_PERCENT, percent)
            .putLong(KEY_LAST_TIMESTAMP, session.lastReadTimestamp)
            .putBoolean(KEY_LAST_FINISHED, isFinished)
            .apply()
        _lastSession.value = session
    }

    fun updateReaderSettings(settings: ReaderSettings) {
        prefs.edit()
            .putFloat(KEY_FONT_SIZE, settings.fontSizeSp)
            .putString(KEY_THEME_MODE, settings.themeMode.name)
            .putFloat(KEY_LINE_SPACING, settings.lineSpacingMultiplier)
            .apply()
        _readerSettings.value = settings
    }

    private fun loadFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    private fun loadReaderSettings(): ReaderSettings {
        val fontSize = prefs.getFloat(KEY_FONT_SIZE, 19f)
        val themeName = prefs.getString(KEY_THEME_MODE, ReaderThemeMode.SEPIA.name)
        val lineSpacing = prefs.getFloat(KEY_LINE_SPACING, 1.65f)
        val mode = try {
            ReaderThemeMode.valueOf(themeName ?: ReaderThemeMode.SEPIA.name)
        } catch (_: Exception) {
            ReaderThemeMode.SEPIA
        }
        return ReaderSettings(
            fontSizeSp = fontSize,
            themeMode = mode,
            lineSpacingMultiplier = lineSpacing
        )
    }

    private fun loadLastSession(): ReadingSession? {
        val bookId = prefs.getString(KEY_LAST_BOOK_ID, null) ?: return null
        val chapterIdx = prefs.getInt(KEY_LAST_CHAPTER_IDX, 0)
        val percent = prefs.getFloat(KEY_LAST_SCROLL_PERCENT, 0f)
        val timestamp = prefs.getLong(KEY_LAST_TIMESTAMP, System.currentTimeMillis())
        val isFinished = prefs.getBoolean(KEY_LAST_FINISHED, false)
        return ReadingSession(bookId, chapterIdx, percent, timestamp, isFinished)
    }

    companion object {
        private const val KEY_FAVORITES = "fav_book_ids"
        private const val KEY_LAST_BOOK_ID = "last_book_id"
        private const val KEY_LAST_CHAPTER_IDX = "last_chapter_idx"
        private const val KEY_LAST_SCROLL_PERCENT = "last_scroll_percent"
        private const val KEY_LAST_TIMESTAMP = "last_timestamp"
        private const val KEY_LAST_FINISHED = "last_finished"
        private const val KEY_FONT_SIZE = "reader_font_size"
        private const val KEY_THEME_MODE = "reader_theme_mode"
        private const val KEY_LINE_SPACING = "reader_line_spacing"
    }
}

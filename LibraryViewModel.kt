package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Book
import com.example.data.model.BookCategory
import com.example.data.model.ReaderSettings
import com.example.data.model.ReaderThemeMode
import com.example.data.model.ReadingSession
import com.example.data.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class ScreenRoute {
    HOME,
    BOOK_DETAIL,
    READER,
    FAVORITES,
    DEVELOPER_PROFILE
}

data class LibraryUiState(
    val selectedCategory: BookCategory = BookCategory.ALL,
    val searchQuery: String = "",
    val activeScreen: ScreenRoute = ScreenRoute.HOME,
    val selectedBookId: String? = null,
    val currentChapterIndex: Int = 0,
    val showDeveloperDialog: Boolean = false
)

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LibraryRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = repository.favoriteIds
    val readerSettings: StateFlow<ReaderSettings> = repository.readerSettings
    val lastSession: StateFlow<ReadingSession?> = repository.lastSession

    // Filtered books stream based on category and search query
    val displayedBooks: StateFlow<List<Book>> = combine(
        _uiState
    ) { stateArray ->
        val state = stateArray[0]
        repository.searchBooks(state.searchQuery, state.selectedCategory)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        repository.getAllBooks()
    )

    fun onCategorySelected(category: BookCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun openBookDetail(bookId: String) {
        _uiState.value = _uiState.value.copy(
            selectedBookId = bookId,
            activeScreen = ScreenRoute.BOOK_DETAIL
        )
    }

    fun openReader(bookId: String, chapterIndex: Int = 0) {
        val book = repository.getBookById(bookId)
        val safeIndex = if (book != null && chapterIndex in book.chapters.indices) chapterIndex else 0
        repository.saveReadingProgress(bookId, safeIndex)
        _uiState.value = _uiState.value.copy(
            selectedBookId = bookId,
            currentChapterIndex = safeIndex,
            activeScreen = ScreenRoute.READER
        )
    }

    fun nextChapter() {
        val currentBook = getCurrentBook() ?: return
        val currentIdx = _uiState.value.currentChapterIndex
        if (currentIdx < currentBook.chapters.lastIndex) {
            val newIdx = currentIdx + 1
            repository.saveReadingProgress(currentBook.id, newIdx)
            _uiState.value = _uiState.value.copy(currentChapterIndex = newIdx)
        }
    }

    fun previousChapter() {
        val currentBook = getCurrentBook() ?: return
        val currentIdx = _uiState.value.currentChapterIndex
        if (currentIdx > 0) {
            val newIdx = currentIdx - 1
            repository.saveReadingProgress(currentBook.id, newIdx)
            _uiState.value = _uiState.value.copy(currentChapterIndex = newIdx)
        }
    }

    fun selectChapter(index: Int) {
        val currentBook = getCurrentBook() ?: return
        if (index in currentBook.chapters.indices) {
            repository.saveReadingProgress(currentBook.id, index)
            _uiState.value = _uiState.value.copy(currentChapterIndex = index)
        }
    }

    fun toggleFavorite(bookId: String) {
        repository.toggleFavorite(bookId)
    }

    fun isFavorite(bookId: String): Boolean {
        return repository.isFavorite(bookId)
    }

    fun navigateTo(screen: ScreenRoute) {
        _uiState.value = _uiState.value.copy(activeScreen = screen)
    }

    fun navigateBack(): Boolean {
        val current = _uiState.value.activeScreen
        return when (current) {
            ScreenRoute.READER -> {
                // If we came to reader from detail or home
                _uiState.value = _uiState.value.copy(activeScreen = ScreenRoute.BOOK_DETAIL)
                true
            }
            ScreenRoute.BOOK_DETAIL -> {
                _uiState.value = _uiState.value.copy(activeScreen = ScreenRoute.HOME)
                true
            }
            ScreenRoute.FAVORITES, ScreenRoute.DEVELOPER_PROFILE -> {
                _uiState.value = _uiState.value.copy(activeScreen = ScreenRoute.HOME)
                true
            }
            ScreenRoute.HOME -> false
        }
    }

    fun toggleDeveloperDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showDeveloperDialog = show)
    }

    fun updateFontSize(newSizeSp: Float) {
        val current = readerSettings.value
        repository.updateReaderSettings(current.copy(fontSizeSp = newSizeSp.coerceIn(14f, 32f)))
    }

    fun updateReaderTheme(mode: ReaderThemeMode) {
        val current = readerSettings.value
        repository.updateReaderSettings(current.copy(themeMode = mode))
    }

    fun getCurrentBook(): Book? {
        val id = _uiState.value.selectedBookId ?: return null
        return repository.getBookById(id)
    }

    fun getFavoriteBooks(): List<Book> {
        val favs = favoriteIds.value
        return repository.getAllBooks().filter { favs.contains(it.id) }
    }

    fun getBooksCountByCategory(category: BookCategory): Int {
        return repository.getBooksByCategory(category).size
    }

    fun getTotalBooksCount(): Int = repository.getAllBooks().size
}

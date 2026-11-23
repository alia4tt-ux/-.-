package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.BookCategory
import com.example.ui.components.DeveloperDialog
import com.example.ui.screens.BookDetailScreen
import com.example.ui.screens.BookReaderScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LibraryViewModel
import com.example.ui.viewmodel.ScreenRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Arabic RTL Layout Direction for natural reading and typography
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        LibraryApp()
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryApp(
    viewModel: LibraryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val displayedBooks by viewModel.displayedBooks.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val readerSettings by viewModel.readerSettings.collectAsStateWithLifecycle()
    val lastSession by viewModel.lastSession.collectAsStateWithLifecycle()

    val categoryCounts = rememberCategoryCounts(viewModel)

    when (uiState.activeScreen) {
        ScreenRoute.HOME, ScreenRoute.DEVELOPER_PROFILE -> {
            HomeScreen(
                books = displayedBooks,
                allBooksCount = viewModel.getTotalBooksCount(),
                selectedCategory = uiState.selectedCategory,
                searchQuery = uiState.searchQuery,
                favoriteIds = favoriteIds,
                lastSession = lastSession,
                onCategorySelected = { viewModel.onCategorySelected(it) },
                onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                onBookClick = { viewModel.openBookDetail(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onOpenFavorites = { viewModel.navigateTo(ScreenRoute.FAVORITES) },
                onOpenDeveloperInfo = { viewModel.toggleDeveloperDialog(true) },
                onContinueReading = { bookId, chapterIdx -> viewModel.openReader(bookId, chapterIdx) },
                categoryCounts = categoryCounts
            )
        }
        ScreenRoute.BOOK_DETAIL -> {
            val currentBook = viewModel.getCurrentBook()
            if (currentBook != null) {
                BookDetailScreen(
                    book = currentBook,
                    isFavorite = viewModel.isFavorite(currentBook.id),
                    onBack = { viewModel.navigateBack() },
                    onStartReading = { chapterIdx -> viewModel.openReader(currentBook.id, chapterIdx) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) }
                )
            } else {
                viewModel.navigateTo(ScreenRoute.HOME)
            }
        }
        ScreenRoute.READER -> {
            val currentBook = viewModel.getCurrentBook()
            if (currentBook != null) {
                BookReaderScreen(
                    book = currentBook,
                    currentChapterIndex = uiState.currentChapterIndex,
                    readerSettings = readerSettings,
                    onBack = { viewModel.navigateBack() },
                    onNextChapter = { viewModel.nextChapter() },
                    onPrevChapter = { viewModel.previousChapter() },
                    onSelectChapter = { viewModel.selectChapter(it) },
                    onUpdateFontSize = { viewModel.updateFontSize(it) },
                    onUpdateReaderTheme = { viewModel.updateReaderTheme(it) }
                )
            } else {
                viewModel.navigateTo(ScreenRoute.HOME)
            }
        }
        ScreenRoute.FAVORITES -> {
            FavoritesScreen(
                favoriteBooks = viewModel.getFavoriteBooks(),
                favoriteIds = favoriteIds,
                onBack = { viewModel.navigateBack() },
                onBookClick = { viewModel.openBookDetail(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) }
            )
        }
    }

    // Developer Profile Dialog ("المطور علي خالد الكندي")
    if (uiState.showDeveloperDialog) {
        DeveloperDialog(
            onDismiss = { viewModel.toggleDeveloperDialog(false) }
        )
    }
}

@Composable
private fun rememberCategoryCounts(viewModel: LibraryViewModel): Map<BookCategory, Int> {
    return BookCategory.values().associateWith { cat ->
        if (cat == BookCategory.ALL) {
            viewModel.getTotalBooksCount()
        } else {
            viewModel.getBooksCountByCategory(cat)
        }
    }
}

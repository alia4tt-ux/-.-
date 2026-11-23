package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Book
import com.example.data.model.BookCategory
import com.example.data.model.DeveloperConstants
import com.example.data.model.ReadingSession
import com.example.ui.components.BookItemCard
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.ContinueReadingCard
import com.example.ui.components.DeveloperCard
import com.example.ui.components.DeveloperHeader
import com.example.ui.theme.LibraryEmeraldDark
import com.example.ui.theme.LibraryGoldLight

@Composable
fun HomeScreen(
    books: List<Book>,
    allBooksCount: Int,
    selectedCategory: BookCategory,
    searchQuery: String,
    favoriteIds: Set<String>,
    lastSession: ReadingSession?,
    onCategorySelected: (BookCategory) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenDeveloperInfo: () -> Unit,
    onContinueReading: (String, Int) -> Unit,
    categoryCounts: Map<BookCategory, Int>,
    modifier: Modifier = Modifier
) {
    val lastReadBook = remember(lastSession, books) {
        lastSession?.let { session -> books.find { it.id == session.bookId } }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DeveloperHeader(
                onOpenFavorites = onOpenFavorites,
                onOpenDeveloperInfo = onOpenDeveloperInfo,
                favoritesCount = favoriteIds.size
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner with generated library visual asset
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_library_hero),
                            contentDescription = "مكتبة المعرفة العربية",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Elegant gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            LibraryEmeraldDark.copy(alpha = 0.88f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "مرحباً بك في «${DeveloperConstants.APP_NAME}»",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "واحة المعرفة الدينية، العلمية، الثقافية، والقصصية",
                                style = MaterialTheme.typography.bodySmall,
                                color = LibraryGoldLight
                            )
                        }
                    }
                }
            }

            // Developer Showcase Banner in System Interface
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    DeveloperCard(onOpenDeveloperInfo = onOpenDeveloperInfo)
                }
            }

            // Search Bar
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_search_books"),
                        placeholder = {
                            Text(
                                text = "ابحث عن كتاب، مؤلف، أو موضوع علمي/ديني...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "مسح البحث"
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            // Continue Reading section (if available and no active search)
            if (lastReadBook != null && searchQuery.isEmpty() && lastSession != null) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ContinueReadingCard(
                            book = lastReadBook,
                            chapterIndex = lastSession.chapterIndex,
                            onContinueClick = onContinueReading
                        )
                    }
                }
            }

            // Category Filter Chips
            item {
                Column {
                    Text(
                        text = "تصنيفات الكتب",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CategoryFilterRow(
                        selectedCategory = selectedCategory,
                        onCategorySelected = onCategorySelected,
                        categoryCounts = categoryCounts
                    )
                }
            }

            // Books Header / Count
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory == BookCategory.ALL) "مختارات المكتبة" else selectedCategory.titleArabic,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${books.size} كتاب",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Empty State
            if (books.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لم يتم العثور على نتائج للبحث «$searchQuery»",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(books, key = { it.id }) { book ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        BookItemCard(
                            book = book,
                            isFavorite = favoriteIds.contains(book.id),
                            onBookClick = onBookClick,
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }
            }
        }
    }
}

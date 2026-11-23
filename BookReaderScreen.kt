package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Book
import com.example.data.model.ReaderSettings
import com.example.data.model.ReaderThemeMode
import com.example.ui.theme.LibraryEmeraldPrimary
import com.example.ui.theme.LibraryGoldSecondary
import com.example.ui.theme.ReaderDarkBg
import com.example.ui.theme.ReaderDarkText
import com.example.ui.theme.ReaderPaperBg
import com.example.ui.theme.ReaderPaperText
import com.example.ui.theme.ReaderSepiaBg
import com.example.ui.theme.ReaderSepiaText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReaderScreen(
    book: Book,
    currentChapterIndex: Int,
    readerSettings: ReaderSettings,
    onBack: () -> Unit,
    onNextChapter: () -> Unit,
    onPrevChapter: () -> Unit,
    onSelectChapter: (Int) -> Unit,
    onUpdateFontSize: (Float) -> Unit,
    onUpdateReaderTheme: (ReaderThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var showSettingsSheet by remember { mutableStateOf(false) }
    var showChapterSheet by remember { mutableStateOf(false) }

    val safeIndex = currentChapterIndex.coerceIn(0, book.chapters.lastIndex)
    val chapter = book.chapters[safeIndex]

    // Background & Text Colors based on Reader Theme
    val targetBg = when (readerSettings.themeMode) {
        ReaderThemeMode.SEPIA -> ReaderSepiaBg
        ReaderThemeMode.PAPER -> ReaderPaperBg
        ReaderThemeMode.DARK -> ReaderDarkBg
    }
    val targetText = when (readerSettings.themeMode) {
        ReaderThemeMode.SEPIA -> ReaderSepiaText
        ReaderThemeMode.PAPER -> ReaderPaperText
        ReaderThemeMode.DARK -> ReaderDarkText
    }

    val animatedBg by animateColorAsState(targetValue = targetBg, label = "readerBg")
    val animatedText by animateColorAsState(targetValue = targetText, label = "readerText")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = chapter.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_reader_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                actions = {
                    // Chapters List Button
                    IconButton(
                        onClick = { showChapterSheet = true },
                        modifier = Modifier.testTag("btn_reader_toc")
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "قائمة الفصول"
                        )
                    }

                    // Appearance & Font Settings Button
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier.testTag("btn_reader_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "إعدادات القراءة"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = animatedBg,
                    titleContentColor = animatedText,
                    navigationIconContentColor = animatedText,
                    actionIconContentColor = animatedText
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = animatedBg,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Chapter
                    FilledTonalButton(
                        onClick = onPrevChapter,
                        enabled = safeIndex > 0,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_prev_chapter")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("السابق", fontSize = 13.sp)
                    }

                    // Chapter Progress indicator
                    Text(
                        text = "الفصل ${safeIndex + 1} من ${book.chapters.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = animatedText.copy(alpha = 0.8f)
                    )

                    // Next Chapter
                    FilledTonalButton(
                        onClick = onNextChapter,
                        enabled = safeIndex < book.chapters.lastIndex,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_next_chapter")
                    ) {
                        Text("التالي", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBg)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 18.dp)
            ) {
                // Chapter Header
                Text(
                    text = chapter.title,
                    fontSize = (readerSettings.fontSizeSp + 6).sp,
                    fontWeight = FontWeight.Bold,
                    color = animatedText,
                    lineHeight = (readerSettings.fontSizeSp + 14).sp,
                    textAlign = TextAlign.Start
                )

                if (chapter.subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = chapter.subtitle,
                        fontSize = (readerSettings.fontSizeSp - 2).sp,
                        color = animatedText.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                Divider(color = animatedText.copy(alpha = 0.15f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(18.dp))

                // Chapter Reading Content
                Text(
                    text = chapter.content,
                    fontSize = readerSettings.fontSizeSp.sp,
                    lineHeight = (readerSettings.fontSizeSp * readerSettings.lineSpacingMultiplier).sp,
                    color = animatedText,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    // Settings Bottom Sheet
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "تخصيص تجربة القراءة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Font Size Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "حجم الخط: ${readerSettings.fontSizeSp.toInt()} نقطة",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Row {
                        IconButton(
                            onClick = { onUpdateFontSize(readerSettings.fontSizeSp - 2f) },
                            enabled = readerSettings.fontSizeSp > 14f
                        ) {
                            Icon(Icons.Default.TextDecrease, contentDescription = "تصغير الخط")
                        }
                        IconButton(
                            onClick = { onUpdateFontSize(readerSettings.fontSizeSp + 2f) },
                            enabled = readerSettings.fontSizeSp < 32f
                        ) {
                            Icon(Icons.Default.TextIncrease, contentDescription = "تكبير الخط")
                        }
                    }
                }

                Slider(
                    value = readerSettings.fontSizeSp,
                    onValueChange = { onUpdateFontSize(it) },
                    valueRange = 14f..32f,
                    steps = 8
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Reader Themes (Sepia, Paper, Dark)
                Text(
                    text = "نمط الصفحة والإضاءة",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReaderThemeButton(
                        title = "ورق دافئ",
                        bgColor = ReaderSepiaBg,
                        textColor = ReaderSepiaText,
                        isSelected = readerSettings.themeMode == ReaderThemeMode.SEPIA,
                        onClick = { onUpdateReaderTheme(ReaderThemeMode.SEPIA) },
                        modifier = Modifier.weight(1f)
                    )
                    ReaderThemeButton(
                        title = "نهاري ناصع",
                        bgColor = ReaderPaperBg,
                        textColor = ReaderPaperText,
                        isSelected = readerSettings.themeMode == ReaderThemeMode.PAPER,
                        onClick = { onUpdateReaderTheme(ReaderThemeMode.PAPER) },
                        modifier = Modifier.weight(1f)
                    )
                    ReaderThemeButton(
                        title = "ليلي هادئ",
                        bgColor = ReaderDarkBg,
                        textColor = ReaderDarkText,
                        isSelected = readerSettings.themeMode == ReaderThemeMode.DARK,
                        onClick = { onUpdateReaderTheme(ReaderThemeMode.DARK) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    // Chapter Table of Contents Sheet
    if (showChapterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChapterSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "فصول كتاب: ${book.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                book.chapters.forEachIndexed { idx, ch ->
                    val isCurrent = idx == safeIndex
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onSelectChapter(idx)
                                showChapterSheet = false
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${idx + 1}.",
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = ch.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ReaderThemeButton(
    title: String,
    bgColor: Color,
    textColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, LibraryGoldSecondary) else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

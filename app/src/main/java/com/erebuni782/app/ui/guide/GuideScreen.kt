package com.erebuni782.app.ui.guide

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.graphics.BitmapFactory
import com.erebuni782.app.AppGraph
import com.erebuni782.app.R
import com.erebuni782.app.data.Sections
import com.erebuni782.app.data.WikiArticleUi
import com.erebuni782.app.ui.theme.OrnamentalDivider
import com.erebuni782.app.ui.theme.StyledCard

/** Гид: карточки крепостей текущего раздела + вход в таймлайн. */
@Composable
fun GuideScreen(onOpenArticle: (String) -> Unit, onOpenTimeline: () -> Unit = {}) {
    val section by AppGraph.settings.section.collectAsState(initial = Sections.URARTU)
    val articles by produceState(
        initialValue = emptyList<WikiArticleUi>(),
        key1 = "fortresses", key2 = section
    ) {
        value = AppGraph.wiki.articles(java.util.Locale.getDefault().toLanguageTag())
            .filter { it.category == "fortresses" && it.section == section }
    }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("guide_list"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.guide_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                OrnamentalDivider(Modifier.padding(top = 8.dp))
            }
            // ── вход в таймлайн 860 до н.э. → XX век ──
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onOpenTimeline() }
                        .testTag("open_timeline")
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(R.string.timeline_title),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = stringResource(R.string.timeline_subtitle),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            items(articles, key = { it.id }) { article ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onOpenArticle(article.id) }) {
                    Column {
                        // реальное фото крепости (jpg — приоритет, png — fallback); у каждой карточки — своё
                        val imageName = when (article.id) {
                            "erebuni" -> "erebuni_fortress"
                            "teishebaini" -> "teishebaini_ruins"
                            "tushpa" -> "king_rusa1_van"          // Ванская скала — она же Тушпа
                            "argishtikhinili" -> "argishtikhinili"
                            // ── Древняя Армения ──
                            "garni" -> "anc_garni"
                            "geghard" -> "anc_geghard"
                            "zvartnots" -> "anc_zvartnots"
                            "etchmiadzin" -> "anc_etchmiadzin"
                            "amberd" -> "anc_amberd"
                            "ani" -> "anc_ani"
                            else -> null
                        }
                        val bitmap = imageName?.let { name ->
                            remember(article.id) {
                                runCatching {
                                    BitmapFactory.decodeStream(
                                        AppGraph.appContext.assets.open("wiki_images/$name.jpg")
                                    )
                                }.recoverCatching {
                                    BitmapFactory.decodeStream(
                                        AppGraph.appContext.assets.open("wiki_images/$name.png")
                                    )
                                }.getOrNull()
                            }
                        }
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = article.title,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            StyledCard(Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(article.title, style = MaterialTheme.typography.titleLarge)
                                    Text(
                                        text = article.body,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 3
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

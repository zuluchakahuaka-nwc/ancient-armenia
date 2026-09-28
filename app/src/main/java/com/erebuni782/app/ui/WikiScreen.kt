package com.erebuni782.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.graphics.BitmapFactory
import com.erebuni782.app.AppGraph
import com.erebuni782.app.R
import com.erebuni782.app.data.WikiArticleUi
import com.erebuni782.app.ui.theme.OrnamentalDivider

/** Вики: мини-энциклопедия (10 сид-статей P2). */
@Composable
fun WikiScreen(onOpenArticle: (String) -> Unit) {
    val articles by produceState(initialValue = emptyList<WikiArticleUi>()) {
        value = AppGraph.wiki.articles(java.util.Locale.getDefault().toLanguageTag())
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.wiki_title),
                style = MaterialTheme.typography.headlineMedium
            )
            OrnamentalDivider(Modifier.padding(top = 8.dp))
        }
        items(articles, key = { it.id }) { article ->
            Card(modifier = Modifier.fillMaxWidth().clickable { onOpenArticle(article.id) }) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(article.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = article.body,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun WikiArticleScreen(articleId: String) {
    val article by produceState(initialValue = null as WikiArticleUi?, key1 = articleId) {
        value = AppGraph.wiki.article(articleId, java.util.Locale.getDefault().toLanguageTag())
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ── иллюстрация статьи ──
        item {
            val imageName = when (articleId) {
                "erebuni" -> "erebuni_fortress"
                "teishebaini" -> "teishebaini_ruins"
                "haldi" -> "god_haldi"
                "teisheba" -> "god_teisheba"
                "shivini" -> "god_shivini"
                "argishti1", "sarduri2", "rusa2" -> "king_throne"
                "cuneiform" -> "cuneiform_tablet"
                "daily_life" -> "daily_life"
                else -> null
            }
            imageName?.let { name ->
                val bitmap = remember(articleId) {
                    runCatching {
                        BitmapFactory.decodeStream(
                            AppGraph.appContext.assets.open("wiki_images/$name.png")
                        )
                    }.getOrNull()
                }
                bitmap?.let {
                    Card(Modifier.fillMaxWidth()) {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = article?.title.orEmpty(),
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = article?.title.orEmpty(),
                style = MaterialTheme.typography.headlineSmall
            )
            OrnamentalDivider(Modifier.padding(top = 8.dp))
        }
        item {
            Text(
                text = article?.body.orEmpty(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

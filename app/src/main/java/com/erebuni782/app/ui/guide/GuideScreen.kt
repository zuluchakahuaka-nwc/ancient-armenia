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

/** Гид: две крепости (сид-статьи категории fortresses). */
@Composable
fun GuideScreen(onOpenArticle: (String) -> Unit) {
    val articles by produceState(initialValue = emptyList<WikiArticleUi>(), key1 = "fortresses") {
        value = AppGraph.wiki.articles(java.util.Locale.getDefault().toLanguageTag())
            .filter { it.category == "fortresses" }
    }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
            items(articles, key = { it.id }) { article ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onOpenArticle(article.id) }) {
                    Column {
                        // иллюстрация крепости
                        val imageName = if (article.id == "erebuni") "erebuni_fortress" else "teishebaini_ruins"
                        val bitmap = remember(article.id) {
                            runCatching {
                                BitmapFactory.decodeStream(
                                    AppGraph.appContext.assets.open("wiki_images/$imageName.png")
                                )
                            }.getOrNull()
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

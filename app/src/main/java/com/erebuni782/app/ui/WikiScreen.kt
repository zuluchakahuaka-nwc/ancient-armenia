package com.erebuni782.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.graphics.BitmapFactory
import com.erebuni782.app.AppGraph
import com.erebuni782.app.R
import com.erebuni782.app.data.WikiArticleUi
import com.erebuni782.app.ui.theme.OrnamentalDivider
import com.erebuni782.app.ui.theme.StyledCard

/** Категории фильтра (ключи = category в сид-контенте). */
private val wikiCategories = listOf(
    "all" to R.string.cat_all,
    "fortresses" to R.string.cat_fortresses,
    "kings" to R.string.cat_kings,
    "gods" to R.string.cat_gods,
    "life" to R.string.cat_life,
    "culture" to R.string.cat_culture,
    "history" to R.string.cat_history
)

/** Вики: мини-энциклопедия с фильтром по категориям (цари/боги/крепости/быт…). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WikiScreen(onOpenArticle: (String) -> Unit) {
    val articles by produceState(initialValue = emptyList<WikiArticleUi>()) {
        value = AppGraph.wiki.articles(java.util.Locale.getDefault().toLanguageTag())
    }
    var selectedCategory by rememberSaveable { mutableStateOf("all") }
    val visible = if (selectedCategory == "all") articles
        else articles.filter { it.category == selectedCategory }

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
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                wikiCategories.forEach { (cat, labelRes) ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(stringResource(labelRes)) },
                        modifier = Modifier.testTag("wiki_cat_$cat")
                    )
                }
            }
        }
        items(visible, key = { it.id }) { article ->
            Card(modifier = Modifier.fillMaxWidth().clickable { onOpenArticle(article.id) }) {
                StyledCard(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
        // ── реальное фото статьи (все — с Wikipedia/Commons) ──
        item {
            val imageName = when (articleId) {
                "erebuni" -> "erebuni_fortress"           // фото крепости
                "teishebaini" -> "teishebaini_ruins"       // фото Кармир-Блура
                "argishti1" -> "king_argishti1_real"       // статуя Аргишти I
                "ishpuini" -> "king_ishpuini_real"         // рельеф Ишпуини
                "sarduri1" -> "king_sarduri1_fort"         // крепость Сардури I
                "rusa1", "tushpa" -> "king_rusa1_van"      // Ванская скала (столица)
                "rusa2" -> "king_rusa2_cuneiform"          // клинопись Русы II
                "sarduri2", "argishti2", "rusa3", "menua" -> "king_argishti1_real" // династия
                "haldi" -> "god_haldi"                     // fresco of god on lion
                "teisheba" -> "god_teisheba"               // god on bull
                "shivini" -> "god_shivini"                 // god on horse
                "pantheon" -> "mural_apadana"              // роспись из храма
                "cuneiform", "urartu_assyria" -> "king_rusa2_cuneiform" // клинописная надпись
                "daily_life", "agriculture" -> "fresco_animals" // фреска быта
                "murals", "excavations" -> "murals_real"   // росписи Эребуни
                "metallurgy", "army" -> "king_sarduri1_fort" // крепость/оружие
                "fall_urartu" -> "teishebaini_ruins"       // гибель Тейшебаини
                "argishtikhinili" -> "erebuni_fortress"    // крепость-аналог
                else -> null
            }
            imageName?.let { name ->
                val bitmap = remember(articleId) {
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
            StyledCard(Modifier.fillMaxWidth()) {
                Text(
                    text = article?.body.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

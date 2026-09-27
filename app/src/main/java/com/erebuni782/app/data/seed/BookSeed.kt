package com.erebuni782.app.data.seed

/**
 * D1: точного списка книг нет — каркас + 1 тестовая книга.
 * licenseNote обязателен для каждой книги (спека §1).
 */
data class BookSeed(
    val id: String,
    val titleEn: String, val titleRu: String, val titleHy: String,
    val authorEn: String, val authorRu: String, val authorHy: String,
    val licenseNote: String,
    val chapters: List<ChapterSeed>
)

data class ChapterSeed(
    val index: Int,
    val titleEn: String, val titleRu: String, val titleHy: String,
    val bodyEn: String, val bodyRu: String, val bodyHy: String
)

val BOOK_SEED: BookSeed = BookSeed(
    id = "sample_erebuni",
    titleEn = "Erebuni: a fortress on Arin-Berd",
    titleRu = "Эребуни: крепость на Арин-Берде",
    titleHy = "Էրեբունի. ամրոց Արին-Բերդում",
    authorEn = "Erebuni 782 editorial team",
    authorRu = "Редакция «Эребуни 782»",
    authorHy = "«Էրեբունի 782» խմբագրություն",
    licenseNote = "CC0 1.0 (original demo text, P6 will add licensed scholarship)",
    chapters = listOf(
        ChapterSeed(
            index = 0,
            titleEn = "The hill and the inscription", titleRu = "Холм и надпись", titleHy = "Բլուրը և արձանագրությունը",
            bodyEn = "In spring 782 BC workmen levelled the top of Arin-Berd and laid the first basalt blocks of a new fortress. A clay tablet with cuneiform was buried in the wall, naming Argishti I, son of Menua, as founder.",
            bodyRu = "Весной 782 г. до н. э. рабочие выровняли вершину Арин-Берда и заложили первые базальтовые блоки новой крепости. В стену замуровали глиняную табличку с клинописью, называющую Аргишти I, сына Менуа, основателем.",
            bodyHy = "Մ.թ.ա. 782 թվականի գարնանը բանվորները հարթեցրին Արին-Բերդի գագաթը և դրեցին նոր ամրոցի առաջին բազալտե բլոկները։ Պատի մեջ ամփոփվեց կավե սեպագիր տախտակ, որը հիմնադիր էր ճանաչում Մենուայի որդի Արգիշտի Ա-ին։"
        ),
        ChapterSeed(
            index = 1,
            titleEn = "Walls and palace", titleRu = "Стены и дворец", titleHy = "Պարիսպներ և պալատ",
            bodyEn = "The citadel combined a fortified periphery with a palace quarter on the highest terrace. Storerooms, a large pillared hall and a temple complex made Erebuni an administrative heart of the northern kingdom.",
            bodyRu = "Цитадель соединяла укреплённую периферию с дворцовым кварталом на верхней террасе. Кладовые, просторный колонный зал и храмовый комплекс делали Эребуни административным сердцем северного царства.",
            bodyHy = "Ամրոցը միավորում էր ամրացված ծայրամասը վերին պատշգամբի պալատական թաղամասի հետ։ Պահեստները, սյունազարդ մեծ դահլիճը և տաճարային համալիրը Էրեբունին դարձնում էին հյուսիսային թագավորության վարչական կենտրոնը։"
        ),
        ChapterSeed(
            index = 2,
            titleEn = "The wall paintings", titleRu = "Росписи", titleHy = "Որմնանկարներ",
            bodyEn = "Fragments of painted plaster — geometric borders, processions and divine symbols — survived in the palace. Their ornamental motifs inspired the visual language of this application's Urartu skin.",
            bodyRu = "Во дворце сохранились фрагменты расписной штукатурки — геометрические бордюры, процессии и божественные символы. Их орнаменты легли в основу визуального языка скина «Урарту» этого приложения.",
            bodyHy = "Պալատում պահպանվել են նկարազարդ ծեփի բեկորներ՝ երկրաչափական եզրազարդեր, թափառաշրջիկ շքերթներ և աստվածային խորհրդանիշեր։ Դրանց զարդանախշերը հիմք դարձան այս հավելվածի «Ուրարտու» ոճի տեսողական լեզվի համար։"
        ),
        ChapterSeed(
            index = 3,
            titleEn = "The excavations", titleRu = "Раскопки", titleHy = "Պեղումներ",
            bodyEn = "Systematic excavation of Arin-Berd began in 1950 and revealed the fortress identified as Erebuni. Today the site with its museum is part of Yerevan's historical heritage and gives the capital its founding year.",
            bodyRu = "Систематические раскопки Арин-Берда начались в 1950 году и открыли крепость, отождествлённую с Эребуни. Сегодня памятник с музеем — часть исторического наследия Еревана, дающая столице год основания.",
            bodyHy = "Արին-Բերդի համակարգված պեղումները սկսվեցին 1950 թվականին և բացահայտեցին Էրեբունի համարվող ամրոցը։ Այսօր թանգարանով հուշարձանը Երևանի պատմական ժառանգության մասն է և մայրաքաղաքին տալիս է հիմնադրման տարեթիվը։"
        )
    )
)

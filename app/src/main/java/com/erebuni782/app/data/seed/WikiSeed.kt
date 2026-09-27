package com.erebuni782.app.data.seed

/**
 * 10 сид-статей вики ×3 локали (P2). Контент — данные (не UI-строки),
 * живёт в Room; локализация — полями. P6: контент-добор и вычитка.
 */
data class WikiSeedArticle(
    val id: String,
    val category: String,
    val sortOrder: Int,
    val titleEn: String, val titleRu: String, val titleHy: String,
    val bodyEn: String, val bodyRu: String, val bodyHy: String
)

val WIKI_SEED: List<WikiSeedArticle> = listOf(
    WikiSeedArticle(
        id = "erebuni", category = "fortresses", sortOrder = 1,
        titleEn = "Erebuni fortress", titleRu = "Крепость Эребуни", titleHy = "Էրեբունի ամրոց",
        bodyEn = "Erebuni was founded in 782 BC by king Argishti I on the Arin-Berd hill, at the edge of modern Yerevan. The citadel held a palace with wall paintings, storerooms and a garrison; a cuneiform foundation inscription found in excavations names the founding king and year.",
        bodyRu = "Эребуни основан в 782 г. до н. э. царём Аргишти I на холме Арин-Берд, на окраине современного Еревана. Цитадель включала дворец с росписями, кладовые и гарнизон; найденная при раскопках клинописная надпись называет царя и год основания.",
        bodyHy = "Էրեբունին հիմնադրվել է մ.թ.ա. 782 թվականին Արգիշտի Ա թագավորի կողմից Արին-Բերդ բլրի վրա՝ ժամանակակից Երևանի եզրին։ Ամրոցում եղել են որմնանկարներով պալատ, պահեստներ և կայազոր. պեղումների ժամանակ գտնված սեպագիր արձանագրությունը նշում է թագավորին և հիմնադրման տարեթիվը։"
    ),
    WikiSeedArticle(
        id = "teishebaini", category = "fortresses", sortOrder = 2,
        titleEn = "Teishebaini (Karmir Blur)", titleRu = "Тейшебаини (Кармир-Блур)", titleHy = "Թեյշեբաինի (Կարմիր բլուր)",
        bodyEn = "Teishebaini on the Karmir Blur hill was built by king Rusa II in the 7th century BC and became the last major centre of Urartu. Around 590 BC the city perished in a great fire; excavations led by Boris Piotrovsky uncovered storerooms full of grain, wine and bronze.",
        bodyRu = "Тейшебаини на холме Кармир-Блур построен царём Русой II в VII в. до н. э. и стал последним крупным центром Урарту. Около 590 г. до н. э. город погиб в сильном пожаре; раскопки под руководством Бориса Пиотровского открыли кладовые с зерном, вином и бронзой.",
        bodyHy = "Կարմիր բլուր բլրի վրա գտնվող Թեյշեբաինին կառուցել է Ռուսա Բ թագավորը մ.թ.ա. 7-րդ դարում, և այն դարձավ Ուրարտուի վերջին խոշոր կենտրոնը։ Մոտ մ.թ.ա. 590 թվականին քաղաքը ոչնչացավ հրդեհից. Բորիս Պիոտրովսկու ղեկավարած պեղումները բացահայտեցին հացահատիկով, գինով և բրոնզով լցված պահեստներ։"
    ),
    WikiSeedArticle(
        id = "argishti1", category = "kings", sortOrder = 3,
        titleEn = "Argishti I", titleRu = "Аргишти I", titleHy = "Արգիշտի Ա",
        bodyEn = "Argishti I, son of Menua, ruled Urartu in the 8th century BC and greatly expanded the kingdom northward. In 782 BC he founded Erebuni to control the Ararat plain — the event conventionally taken as the founding of Yerevan.",
        bodyRu = "Аргишти I, сын Менуа, правил Урарту в VIII в. до н. э. и значительно расширил царство к северу. В 782 г. до н. э. он основал Эребуни для контроля Араратской равнины — событие, условно принимаемое за основание Еревана.",
        bodyHy = "Արգիշտի Ա-ն՝ Մենուայի որդին, Ուրարտուն կառավարել է մ.թ.ա. 8-րդ դարում և զգալիորեն ընդլայնել թագավորությունը դեպի հյուսիս։ Մ.թ.ա. 782 թվականին նա հիմնեց Էրեբունին՝ Արարատյան դաշտը վերահսկելու համար. այդ իրադարձությունը պայմանականորեն համարվում է Երևանի հիմնադրումը։"
    ),
    WikiSeedArticle(
        id = "sarduri2", category = "kings", sortOrder = 4,
        titleEn = "Sarduri II", titleRu = "Сардури II", titleHy = "Սարդուրի Բ",
        bodyEn = "Under Sarduri II Urartu reached the peak of its power in the mid-8th century BC. He strengthened fortresses and campaigned widely, but in 735 BC was defeated by the Assyrian king Tiglath-Pileser III, beginning the kingdom's long decline.",
        bodyRu = "При Сардури II в середине VIII в. до н. э. Урарту достигло вершины могущества. Он укреплял крепости и вёл широкие походы, но в 735 г. до н. э. потерпел поражение от ассирийского царя Тиглатпаласара III, что стало началом долгого заката царства.",
        bodyHy = "Սարդուրի Բ-ի օրոք մ.թ.ա. 8-րդ դարի կեսերին Ուրարտուն հասավ իր հզորության գագաթնակետին։ Նա ամրացնում էր բերդերը և ընդարձակ արշավանքներ էր ձեռնարկում, սակայն մ.թ.ա. 735 թվականին պարտություն կրեց ասորի թագավոր Տիգլաթպալասար Գ-ից, ինչը թագավորության երկար անկման սկիզբը դարձավ։"
    ),
    WikiSeedArticle(
        id = "rusa2", category = "kings", sortOrder = 5,
        titleEn = "Rusa II", titleRu = "Руса II", titleHy = "Ռուսա Բ",
        bodyEn = "Rusa II (early 7th century BC) was the last great builder of Urartu: he erected Teishebaini on Karmir Blur and other strongholds. His reign was a short renaissance amid growing pressure from Cimmerians, Scythians and Media.",
        bodyRu = "Руса II (начало VII в. до н. э.) — последний великий строитель Урарту: он воздвиг Тейшебаини на Кармир-Блуре и другие твердыни. Его правление стало коротким ренессансом на фоне нараставшего давления киммерийцев, скифов и Мидии.",
        bodyHy = "Ռուսա Բ-ն (մ.թ.ա. 7-րդ դարի սկիզբ) Ուրարտուի վերջին մեծ շինարարն էր. նա Կարմիր բլուրում կառուցեց Թեյշեբաինին և այլ ամրոցներ։ Նրա թագավորությունը կարճ վերածնունդ էր կիմերացիների, սկյութների և Մարաստանի աճող ճնշման պայմաններում։"
    ),
    WikiSeedArticle(
        id = "haldi", category = "gods", sortOrder = 6,
        titleEn = "Haldi", titleRu = "Халди", titleHy = "Խալդի",
        bodyEn = "Haldi was the supreme god of the Urartian pantheon, a god of war and victory. Kings marched to battle 'with the spear of Haldi'; his chief temple stood in the city of Musasir, famously plundered by Sargon II in 714 BC.",
        bodyRu = "Халди — верховный бог урартского пантеона, бог войны и победы. Цари шли в бой «копьём Халди»; его главный храм стоял в городе Мусасир, прославленно разграбленном Саргоном II в 714 г. до н. э.",
        bodyHy = "Խալդին ուրարտական պանթեոնի գերագույն աստվածն էր՝ պատերազմի և հաղթանակի աստվածը։ Թագավորները մարտի էին գնում «Խալդիի նիզակով». նրա գլխավոր տաճարը գտնվում էր Մուսասիր քաղաքում, որը հայտնի կերպով թալանեց Սարգոն Բ-ն մ.թ.ա. 714 թվականին։"
    ),
    WikiSeedArticle(
        id = "teisheba", category = "gods", sortOrder = 7,
        titleEn = "Teisheba", titleRu = "Тейшеба", titleHy = "Թեյշեբա",
        bodyEn = "Teisheba, the storm and thunder god, stood second in the Urartian triad after Haldi. The fortress Teishebaini — 'city of Teisheba' — was named in his honour, showing his importance to the kingdom's defence.",
        bodyRu = "Тейшеба — бог грозы и грома, второй в урартской триаде после Халди. В его честь была названа крепость Тейшебаини — «город Тейшебы», что говорит о его значении для обороны царства.",
        bodyHy = "Թեյշեբան՝ ամպրոպի և որոտի աստվածը, երկրորդն էր ուրարտական եռյակում Խալդիից հետո։ Նրա պատվին անվանվեց Թեյշեբաինի ամրոցը՝ «Թեյշեբայի քաղաքը», ինչը վկայում է նրա կարևորությանը թագավորության պաշտպանության համար։"
    ),
    WikiSeedArticle(
        id = "shivini", category = "gods", sortOrder = 8,
        titleEn = "Shivini", titleRu = "Шивини", titleHy = "Շիվինի",
        bodyEn = "Shivini, the sun god, completed the great Urartian triad and was invoked for justice. Together Haldi, Teisheba and Shivini watched over kingdom, storm and sky in inscriptions and on seals.",
        bodyRu = "Шивини — бог солнца, завершавший великую урартскую триаду; к нему обращались с молитвами о справедливости. Халди, Тейшеба и Шивини в надписях и на печатях охраняли царство, бурю и небо.",
        bodyHy = "Շիվինին՝ արևի աստվածը, ամբողջացնում էր ուրարտական մեծ եռյակը. նրան դիմում էին արդարության համար։ Խալդին, Թեյշեբան և Շիվինին արձանագրություններում և կնիքների վրա պահպանում էին թագավորությունը, փոթորիկը և երկինքը։"
    ),
    WikiSeedArticle(
        id = "daily_life", category = "life", sortOrder = 9,
        titleEn = "Daily life in Urartu", titleRu = "Повседневная жизнь Урарту", titleHy = "Ուրարտուի առօրյան",
        bodyEn = "Urartian farmers dug long irrigation canals, grew wheat and barley and cultivated famous vineyards; kingly wine cellars held thousands of litres. Craftsmen excelled in bronze, and the kingdom was renowned for its horses.",
        bodyRu = "Урартские земледельцы копали длинные оросительные каналы, выращивали пшеницу и ячмень и разводили знаменитые виноградники; царские винные кладовые вмещали тысячи литров. Мастера славились бронзой, а царство — конями.",
        bodyHy = "Ուրարտացի երկրագործները փորում էին երկար ոռոգման ջրանցքներ, աճեցնում ցորեն ու գարի և մշակում հայտնի խաղողի այգիներ. թագավորական գինու մառաններում պահվում էին հազարավոր լիտրեր։ Վարպետները հմուտ էին բրոնզի գործի, իսկ թագավորությունը հայտնի էր իր ձիերով։"
    ),
    WikiSeedArticle(
        id = "cuneiform", category = "culture", sortOrder = 10,
        titleEn = "Urartian cuneiform", titleRu = "Урартская клинопись", titleHy = "Ուրարտական սեպագիր",
        bodyEn = "Urartian scribes adapted Assyrian cuneiform to their own language, writing mostly on stone walls and foundation tablets. The Erebuni foundation inscription of Argishti I is the most famous example and the key document for the city's date.",
        bodyRu = "Урартские писцы приспособили ассирийскую клинопись к своему языку, записывая тексты главным образом на каменных стенах и закладных табличках. Фундаментная надпись Аргишти I из Эребуни — самый известный образец и ключевой документ для датировки города.",
        bodyHy = "Ուրարտացի գրիչները ասորական սեպագիրը հարմարեցրին իրենց լեզվին՝ գրելով հիմնականում քարե պատերի և հիմնադրման տախտակների վրա։ Արգիշտի Ա-ի Էրեբունիի հիմնադրման արձանագրությունը ամենահայտնի օրինակն է և քաղաքի թվագրման գլխավոր փաստաթուղթը։"
    )
)

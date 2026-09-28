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
        bodyHy = "Ուրարտացի գրիչները ասորական սեպագիրը հարմարեցրին իրենց լեզվին՝ գրելով հիմնականում քարե պատերի և հիմնադրման տախտակների վրա։ Արգիշտի Ա-ի Էրեբունիի հիմնադրման արձանագրությունը ամենահայտնի օրինակն է։"
    ),
    // ── НОВЫЕ СТАТЬИ (P6.5: расширение контента) ──
    WikiSeedArticle(
        id = "menua", category = "kings", sortOrder = 11,
        titleEn = "King Menua", titleRu = "Царь Менуа", titleHy = "Մենուա թագավոր",
        bodyEn = "Menua (c. 810–786 BC) was the greatest builder-king of Urartu. He constructed the Menua Canal (Semiramis Canal), still carrying water today, 50 km of irrigation channels, and founded numerous fortresses. His son Argishti I founded Erebuni in 782 BC.",
        bodyRu = "Менуа (ок. 810–786 до н. э.) — величайший царь-строитель Урарту. Построил канал Менуа (канал Семирамиды), по которому вода идёт и сегодня, 50 км ирригационных каналов, основал множество крепостей. Его сын Аргишти I основал Эребуни в 782 г. до н. э.",
        bodyHy = "Մենուան (մոտ մ.թ.ա. 810–786) Ուրարտուի ամենամեծ շինարար թագավորն էր։ Նա կառուցեց Մենուայի ջրանցքը (Սեմիրամիդայի ջրանցք), որով ջուրը հոսում է մինչ օրս, 50 կմ ոռոգման ջրանցքներ։"
    ),
    WikiSeedArticle(
        id = "ishpuini", category = "kings", sortOrder = 12,
        titleEn = "King Ishpuini", titleRu = "Царь Ишпуини", titleHy = "Իշպուինի թագավոր",
        bodyEn = "Ishpuini (c. 830–810 BC) established the Urartian kingdom as a major power. He captured Musasir, the sacred city of the god Haldi, and strengthened Van as the capital. His inscriptions at Van rock are among the longest Urartian texts.",
        bodyRu = "Ишпуини (ок. 830–810 до н. э.) превратил Урарту в крупную державу. Захватил Мусасир — священный город бога Халди — и укрепил Ван как столицу. Его надписи на Ванской скале — среди самых длинных урартских текстов.",
        bodyHy = "Իշպուինին (մոտ մ.թ.ա. 830–810) Ուրարտուն վերածեց խոշոր տերության։ Գրավեց Մուսասիրը՝ Խալդի աստծո սուրբ քաղաքը և ամրացրեց Վանը որպես մայրաքաղաք։"
    ),
    WikiSeedArticle(
        id = "sarduri1", category = "kings", sortOrder = 13,
        titleEn = "King Sarduri I", titleRu = "Царь Сардури I", titleHy = "Սարդուրի Ա թագավոր",
        bodyEn = "Sarduri I (c. 840–830 BC) was the founder of the Urartian dynasty. He established the capital at Tushpa (Van) and left the earliest known Urartian inscription: 'Sarduri, son of Lutipri, king of the great king, king of Nairi'.",
        bodyRu = "Сардури I (ок. 840–830 до н. э.) — основатель урартской династии. Основал столицу в Тушпе (Ван) и оставил древнейшую известную урартскую надпись: «Сардури, сын Лутипри, царь великого царя, царь Наири».",
        bodyHy = "Սարդուրի Ա-ն (մոտ մ.թ.ա. 840–830) ուրարտական արքայատոհմի հիմնադիրն էր։ Մայրաքաղաքը հիմնեց Տուշպայում (Վան)։"
    ),
    WikiSeedArticle(
        id = "argishti2", category = "kings", sortOrder = 14,
        titleEn = "King Argishti II", titleRu = "Царь Аргишти II", titleHy = "Արգիշտի Բ թագավոր",
        bodyEn = "Argishti II (c. 714–680 BC) ruled during the height of Urartian power. He shifted focus to the north and east, expanding into Transcaucasia. His reign saw extensive construction and military campaigns against the Cimmerians.",
        bodyRu = "Аргишти II (ок. 714–680 до н. э.) правил в период наивысшего могущества Урарту. Перенёс внимание на север и восток, расширяясь в Закавказье. Его правление отмечено масштабным строительством и военными походами против киммерийцев.",
        bodyHy = "Արգիշտի Բ-ն (մոտ մ.թ.ա. 714–680) իշխեց Ուրարտուի հզորության գագաթնակետում։ Ուշադրությունը տեղափոխեց դեպի հյուսիս և արևելք։"
    ),
    WikiSeedArticle(
        id = "rusa3", category = "kings", sortOrder = 15,
        titleEn = "King Rusa III — the last king", titleRu = "Царь Руса III — последний царь", titleHy = "Ռուսա Գ — վերջին թագավոր",
        bodyEn = "Rusa III (early 6th century BC) was the last known king of Urartu. Around 590 BC the kingdom fell to the Medes and Scythians. Teishebaini, the last stronghold, perished in a great fire. Thus ended the 300-year kingdom that gave Yerevan its founding date.",
        bodyRu = "Руса III (начало VI в. до н. э.) — последний известный царь Урарту. Около 590 г. до н. э. царство пало под ударами мидян и скифов. Тейшебаини, последняя твердыня, погибла в пожаре. Так закончилось 300-летнее царство, давшее Еревану дату основания.",
        bodyHy = "Ռուսա Գ-ն (մ.թ.ա. 6-րդ դարի սկիզբ) Ուրարտուի վերջին հայտնի թագավորն էր։ Մոտ մ.թ.ա. 590 թ. թագավորությունը ընկավ մարերի և սկյութների հարվածներից։"
    ),
    WikiSeedArticle(
        id = "tushpa", category = "fortresses", sortOrder = 16,
        titleEn = "Tushpa (Van) — the capital", titleRu = "Тушпа (Ван) — столица", titleHy = "Տուշպա (Վան) — մայրաքաղաք",
        bodyEn = "Tushpa, modern Van in eastern Turkey, was the capital of Urartu from the 9th century BC. The citadel occupied a dramatic limestone cliff overlooking Lake Van. Massive walls, rock-cut tombs of the kings, and the famous Van inscription of Ishpuini and Menua survive.",
        bodyRu = "Тушпа, современный Ван в восточной Турции — столица Урарту с IX в. до н. э. Цитадель занимала скалистый известняковый утёс над озером Ван. Сохранились массивные стены, скальные гробницы царей и знаменитая Ванская надпись Ишпуини и Менуа.",
        bodyHy = "Տուշպան՝ ժամանակակից Վանը Թուրքիայի արևելքում, մ.թ.ա. 9-րդ դարից Ուրարտուի մայրաքաղաքն էր։ Բերդը գտնվում էր Վանա լճի վրա ելնող ժայռոտ բլրի վրա։"
    ),
    WikiSeedArticle(
        id = "argishtikhinili", category = "fortresses", sortOrder = 17,
        titleEn = "Argishtikhinili (Armavir)", titleRu = "Аргиштихинили (Армавир)", titleHy = "Արգիշտիխինիլի (Արմավիր)",
        bodyEn = "Argishtikhinili, founded by Argishti I in the Ararat valley near modern Armavir, was a major administrative and agricultural centre. It controlled the fertile Ararat plain and served as a supply base for the northern territories of Urartu.",
        bodyRu = "Аргиштихинили, основанный Аргишти I в Араратской долине возле современного Армавира, был крупным административным и сельскохозяйственным центром. Контролировал плодородную Араратскую равнину и служил базой снабжения северных территорий Урарту.",
        bodyHy = "Արգիշտիխինիլին, հիմնադրված Արգիշտի Ա-ի կողմից Արարատյան դաշտում, կարևոր վարչական և գյուղատնտեսական կենտրոն էր։"
    ),
    WikiSeedArticle(
        id = "fall_urartu", category = "history", sortOrder = 18,
        titleEn = "The fall of Urartu", titleRu = "Падение Урарту", titleHy = "Ուրարտուի անկումը",
        bodyEn = "The decline began in the late 7th century BC under pressure from Scythian and Cimmerian invasions and the rising Median empire. Around 590 BC the last Urartian fortress, Teishebaini, was destroyed by fire. The kingdom that lasted three centuries vanished, leaving its fortresses, canals and name to history.",
        bodyRu = "Упадок начался в конце VII в. до н. э. под давлением скифских и киммерийских вторжений и растущей Мидийской державы. Около 590 г. до н. э. последняя урартская крепость Тейшебаини была уничтожена пожаром. Трёхвековое царство исчезло, оставив крепости, каналы и имя в истории.",
        bodyHy = "Անկումը սկսվեց մ.թ.ա. 7-րդ դարի վերջում սկյութների և կիմերացիների արշավանքների և աճող Մարաստանի ճնշման տակ։ Մոտ մ.թ.ա. 590 թ. վերջին բերդը՝ Թեյշեբաինին, ոչնչացվեց հրդեհից։"
    ),
    WikiSeedArticle(
        id = "urartu_assyria", category = "history", sortOrder = 19,
        titleEn = "Urartu and Assyria", titleRu = "Урарту и Ассирия", titleHy = "Ուրարտուն և Ասորեստանը",
        bodyEn = "Urartu and Assyria were rivals for supremacy in the Near East for three centuries. Assyrian records first mention Urartu in the 13th century BC. Wars, diplomacy and trade alternated. Sargon II's campaign of 714 BC, when he plundered the temple of Haldi at Musasir, is documented in detail.",
        bodyRu = "Урарту и Ассирия три века соперничали за господство на Ближнем Востоке. Ассирийские источники впервые упоминают Урарту в XIII в. до н. э. Войны, дипломатия и торговля чередовались. Поход Саргона II в 714 г. до н. э., когда он разграбил храм Халди в Мусасире, подробно задокументирован.",
        bodyHy = "Ուրարտուն և Ասորեստանը երեք դար մրցակցում էին Առաջավոր Ասիայում գերիշխանության համար։ Պատերազմներ, դիվանագիտություն և առևտուր հերթագայում էին։"
    ),
    WikiSeedArticle(
        id = "excavations", category = "history", sortOrder = 20,
        titleEn = "Excavations of the 20th century", titleRu = "Раскопки XX века", titleHy = "20-րդ դարի պեղումները",
        bodyEn = "Systematic excavations of Arin-Berd (Erebuni) began in 1950 under Konstantine Hovhannisyan. Boris Piotrovsky led the Karmir Blur (Teishebaini) excavations from 1939 to 1971, uncovering the citadel, storehouses and over 2,000 cuneiform tablets. Their work established Urartology as a discipline.",
        bodyRu = "Систематические раскопки Арин-Берда (Эребуни) начались в 1950 году под руководством Константина Оганесяна. Борис Пиотровский возглавлял раскопки Кармир-Блура (Тейшебаини) с 1939 по 1971 год, открыв цитадель, кладовые и более 2000 клинописных табличек. Их труд заложил основу урартологии.",
        bodyHy = "Արին-Բերդի (Էրեբունի) համակարգված պեղումները սկսվեցին 1950 թ. Կոստանդին Հովհաննիսյանի ղեկավարությամբ։ Բորիս Պիոտրովսկին գլխավորեց Կարմիր բլուրի պեղումները 1939-1971 թթ.:"
    ),
    WikiSeedArticle(
        id = "murals", category = "culture", sortOrder = 21,
        titleEn = "Erebuni wall paintings", titleRu = "Росписи Эребуни", titleHy = "Էրեբունիի որմնանկարները",
        bodyEn = "The palace of Erebuni yielded spectacular wall paintings — the earliest known monumental decorative art in the region. Geometric borders, processions of gods and humans, and sacred tree motifs decorated the columned hall. Traces of blue, red, yellow and white pigments survive, revealing a vivid colour palette.",
        bodyRu = "Дворец Эребуни дал потрясающие настенные росписи — древнейший известный монументальный декоративное искусство в регионе. Геометрические бордюры, процессии богов и людей, мотивы священного дерева украшали колонный зал. Сохранились следы синих, красных, жёлтых и белых пигментов.",
        bodyHy = "Էրեբունիի պալատից հայտնաբերվեցին որմնանկարներ՝ տարածաշրջանի ամենավաղ հայտնի մոնումենտալ դեկորատիվ արվեստը։ Երկրաչափական եզրազարդեր, աստվածների և մարդկանց արշավներ։"
    ),
    WikiSeedArticle(
        id = "metallurgy", category = "life", sortOrder = 22,
        titleEn = "Urartian metallurgy", titleRu = "Урартская металлургия", titleHy = "Ուրարտական մետաղագործություն",
        bodyEn = "Urartu was renowned for its bronze and iron work. Smiths produced weapons, tools, ritual cauldrons and elaborate belts decorated with hunting and battle scenes. The Karmir Blur excavations found bronze workshops with crucibles, moulds and finished products of exceptional quality.",
        bodyRu = "Урарту славилось бронзовым и железным делом. Кузнецы производили оружие, инструменты, ритуальные котлы и изысканные пояса с охотничьими и боевыми сценами. При раскопках Кармир-Блура найдены бронзовые мастерские с тиглями, формами и готовыми изделиями исключительного качества.",
        bodyHy = "Ուրարտուն հայտնի էր բրոնզի և երկաթի մշակմամբ։ Դարբինները պատրաստում էին զենքեր, գործիքներ, ծիսական կաթսաներ և ձգված գոտիներ։"
    ),
    WikiSeedArticle(
        id = "agriculture", category = "life", sortOrder = 23,
        titleEn = "Agriculture and vineyards", titleRu = "Земледелие и виноградники", titleHy = "Երկրագործություն և խաղողագործություն",
        bodyEn = "Urartian agriculture was based on irrigation. Kings built vast canal systems — the Menua Canal still works today. Wheat, barley, sesame and flax were cultivated. Vineyards produced wine stored in giant karas jars: at Teishebaini, 400 jars held some 370,000 litres. Horses and cattle were bred extensively.",
        bodyRu = "Урартское земледелие основывалось на орошении. Цари строили обширные каналы — канал Менуа работает и сегодня. Выращивали пшеницу, ячмень, кунжут и лён. Виноградники давали вино, хранившееся в гигантских карасах: в Тейшебаини 400 карасов вмещали около 370,000 литров. Разводили лошадей и крупный рогатый скот.",
        bodyHy = "Ուրարտական երկրագործությունը հիմնված էր ոռոգման վրա։ Թագավորները կառուցում էին ընդարձակ ջրանցքներ։ Մշակում էին ցորեն, գարի, քունջութ և վուշ։"
    ),
    WikiSeedArticle(
        id = "army", category = "life", sortOrder = 24,
        titleEn = "The Urartian army", titleRu = "Урартская армия", titleHy = "Ուրարտական բանակը",
        bodyEn = "The Urartian army combined chariots, cavalry, archers and infantry. Chariots, drawn by two or three horses, were the elite strike force. Fortresses like Erebuni housed permanent garrisons. The army marched 'with the spear of Haldi', as royal inscriptions proclaim.",
        bodyRu = "Урартская армия объединяла колесницы, кавалерию, лучников и пехоту. Колесницы, запряжённые двумя-тремя конями, были элитной ударной силой. Крепости вроде Эребуни содержали постоянные гарнизоны. Армия шла «копьём Халди», как провозглашают царские надписи.",
        bodyHy = "Ուրարտական բանակը միավորում էր մարտակառքեր, հեծելազոր, նետաձիգներ և հետևակ։ Մարտակառքերը էլիտային հարվածային ուժ էին։"
    ),
    WikiSeedArticle(
        id = "pantheon", category = "gods", sortOrder = 25,
        titleEn = "The Urartian pantheon", titleRu = "Урартский пантеон", titleHy = "Ուրարտական պանթեոնը",
        bodyEn = "The Urartian pantheon, led by the triad Haldi–Teisheba–Shivini, numbered at least 79 gods listed in the Mheri Gate inscription. Haldi stood on a lion, Teisheba on a bull, Shivini on a horse. Offerings of cattle, sheep, wine and bread were prescribed for each deity in precise quantities.",
        bodyRu = "Урартский пантеон во главе с триадой Халди–Тейшеба–Шивини насчитывал не менее 79 богов, перечисленных в надписи у Мхерских врат. Халди стоял на льве, Тейшеба на быке, Шивини на коне. Для каждого божества предписывались жертвы скота, овец, вина и хлеба в точных количествах.",
        bodyHy = "Ուրարտական պանթեոնը՝ Խալդի-Թեյշեբա-Շիվինի եռյակի գլխավորությամբ, հաշվում էր առնվազն 79 աստված։ Խալդին կանգնած էր առյուծի վրա, Թեյշեբան՝ ցուլի, Շիվինին՝ ձիու։"
    )
)

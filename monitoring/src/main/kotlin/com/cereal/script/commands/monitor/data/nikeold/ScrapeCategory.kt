package com.cereal.script.commands.monitor.data.nikeold

enum class ScrapeCategory(
    private val text: String,
    val url: String,
) {
    MEN_NEW_RELEASES("Men - New releases", "https://www.nike.com/nl/w/nieuwe-releases-heren-3n82yznik1"),
    MEN_ALL_SHOES("Men - All shoes", "https://www.nike.com/nl/w/heren-schoenen-nik1zy7ok"),
    MEN_ALL_CLOTHS("Men - All cloths", "https://www.nike.com/nl/w/heren-kleding-6ymx6znik1"),
    WOMEN_ALL_SHOES("Women - All shoes", "https://www.nike.com/nl/w/dames-schoenen-5e1x6zy7ok"),
    WOMEN_NEW_RELEASES("Women - New releases", "https://www.nike.com/nl/w/nieuwe-releases-dames-3n82yz5e1x6"),
    WOMEN_ALL_CLOTHS("Women - All cloths", "https://www.nike.com/nl/w/dames-kleding-5e1x6z6ymx6"),
    ;

    override fun toString(): String = text
}

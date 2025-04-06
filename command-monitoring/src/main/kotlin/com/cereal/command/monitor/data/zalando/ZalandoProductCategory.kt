package com.cereal.command.monitor.data.zalando

enum class ZalandoProductCategory(
    private val text: String,
    val paths: Map<ZalandoWebsite, String>,
) {
    MEN_SNEAKERS("Men - Sneakers", mapOf(ZalandoWebsite.UK to "mens-shoes-trainers", ZalandoWebsite.NL to "herenschoenen-sneakers")),
    WOMEN_SNEAKERS("Women - Sneakers", mapOf(ZalandoWebsite.UK to "womens-shoes-trainers", ZalandoWebsite.NL to "damesschoenen-sneakers")),
    ;

    override fun toString(): String = text
}

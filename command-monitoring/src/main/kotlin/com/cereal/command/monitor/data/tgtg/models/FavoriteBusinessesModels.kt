package com.cereal.command.monitor.data.tgtg.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteBusinessesRequest(
    @SerialName("favorites_only")
    val favoritesOnly: Boolean,
    @SerialName("origin")
    val origin: Origin,
    @SerialName("radius")
    val radius: Int
) {
    @Serializable
    data class Origin(
        @SerialName("latitude")
        val latitude: Double,
        @SerialName("longitude")
        val longitude: Double
    )
}

@Serializable
data class FavoriteBusinessesResponse(
    @SerialName("items")
    val items: List<TgtgItem> = emptyList()
)

@Serializable
data class TgtgItem(
    @SerialName("item")
    val item: ItemDetails? = null,
    @SerialName("store")
    val store: Store? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    @SerialName("pickup_interval")
    val pickupInterval: PickupInterval? = null,
    @SerialName("pickup_location")
    val pickupLocation: PickupLocation? = null,
    @SerialName("purchase_end")
    val purchaseEnd: String? = null,
    @SerialName("items_available")
    val itemsAvailable: Int = 0,
    @SerialName("distance")
    val distance: Double = 0.0,
    @SerialName("favorite")
    val favorite: Boolean = false,
    @SerialName("in_sales_window")
    val inSalesWindow: Boolean = false,
    @SerialName("new_item")
    val newItem: Boolean = false
)

@Serializable
data class ItemDetails(
    @SerialName("item_id")
    val itemId: String? = null,
    @SerialName("price")
    val price: Price? = null,
    @SerialName("sales_taxes")
    val salesTaxes: List<SalesTax> = emptyList(),
    @SerialName("tax_amount")
    val taxAmount: Price? = null,
    @SerialName("price_excluding_taxes")
    val priceExcludingTaxes: Price? = null,
    @SerialName("price_including_taxes")
    val priceIncludingTaxes: Price? = null,
    @SerialName("value_excluding_taxes")
    val valueExcludingTaxes: Price? = null,
    @SerialName("value_including_taxes")
    val valueIncludingTaxes: Price? = null,
    @SerialName("taxation_policy")
    val taxationPolicy: String? = null,
    @SerialName("show_sales_taxes")
    val showSalesTaxes: Boolean = false,
    @SerialName("cover_picture")
    val coverPicture: CoverPicture? = null,
    @SerialName("logo_picture")
    val logoPicture: LogoPicture? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("food_handling_instructions")
    val foodHandlingInstructions: String? = null,
    @SerialName("can_user_supply_packaging")
    val canUserSupplyPackaging: Boolean = false,
    @SerialName("packaging_option")
    val packagingOption: String? = null,
    @SerialName("collection_info")
    val collectionInfo: String? = null,
    @SerialName("diet_categories")
    val dietCategories: List<String> = emptyList(),
    @SerialName("item_category")
    val itemCategory: String? = null,
    @SerialName("buffet")
    val buffet: Boolean = false,
    @SerialName("badges")
    val badges: List<Badge> = emptyList(),
    @SerialName("positive_rating_reasons")
    val positiveRatingReasons: List<String> = emptyList(),
    @SerialName("average_overall_rating")
    val averageOverallRating: AverageRating? = null,
    @SerialName("favorite_count")
    val favoriteCount: Int = 0
)

@Serializable
data class Price(
    @SerialName("code")
    val code: String? = null,
    @SerialName("minor_units")
    val minorUnits: Int = 0,
    @SerialName("decimals")
    val decimals: Int = 0
)

@Serializable
data class SalesTax(
    @SerialName("tax_description")
    val taxDescription: String? = null,
    @SerialName("tax_percentage")
    val taxPercentage: Double = 0.0
)

@Serializable
data class CoverPicture(
    @SerialName("picture_id")
    val pictureId: String? = null,
    @SerialName("current_url")
    val currentUrl: String? = null,
    @SerialName("is_automatically_created")
    val isAutomaticallyCreated: Boolean = false
)

@Serializable
data class LogoPicture(
    @SerialName("picture_id")
    val pictureId: String? = null,
    @SerialName("current_url")
    val currentUrl: String? = null,
    @SerialName("is_automatically_created")
    val isAutomaticallyCreated: Boolean = false
)

@Serializable
data class Badge(
    @SerialName("badge_type")
    val badgeType: String? = null,
    @SerialName("rating_group")
    val ratingGroup: String? = null,
    @SerialName("percentage")
    val percentage: Int = 0,
    @SerialName("user_count")
    val userCount: Int = 0,
    @SerialName("month_count")
    val monthCount: Int = 0
)

@Serializable
data class AverageRating(
    @SerialName("average_overall_rating")
    val averageOverallRating: Double = 0.0,
    @SerialName("rating_count")
    val ratingCount: Int = 0,
    @SerialName("month_count")
    val monthCount: Int = 0
)

@Serializable
data class Store(
    @SerialName("store_id")
    val storeId: String? = null,
    @SerialName("store_name")
    val storeName: String? = null,
    @SerialName("branch")
    val branch: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("tax_identifier")
    val taxIdentifier: String? = null,
    @SerialName("website")
    val website: String? = null,
    @SerialName("store_location")
    val storeLocation: StoreLocation? = null,
    @SerialName("logo_picture")
    val logoPicture: LogoPicture? = null,
    @SerialName("store_time_zone")
    val storeTimeZone: String? = null,
    @SerialName("hidden")
    val hidden: Boolean = false,
    @SerialName("favorite_count")
    val favoriteCount: Int = 0,
    @SerialName("we_care")
    val weCare: Boolean = false
)

@Serializable
data class StoreLocation(
    @SerialName("address")
    val address: Address? = null,
    @SerialName("location")
    val location: Location? = null
)

@Serializable
data class Address(
    @SerialName("country")
    val country: Country? = null,
    @SerialName("address_line")
    val addressLine: String? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("postal_code")
    val postalCode: String? = null
)

@Serializable
data class Country(
    @SerialName("iso_code")
    val isoCode: String? = null,
    @SerialName("name")
    val name: String? = null
)

@Serializable
data class Location(
    @SerialName("longitude")
    val longitude: Double = 0.0,
    @SerialName("latitude")
    val latitude: Double = 0.0
)

@Serializable
data class PickupInterval(
    @SerialName("start")
    val start: String? = null,
    @SerialName("end")
    val end: String? = null
)

@Serializable
data class PickupLocation(
    @SerialName("address")
    val address: Address? = null,
    @SerialName("location")
    val location: Location? = null
)

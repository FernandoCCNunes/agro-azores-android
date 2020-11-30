package pt.tetrapi.fgf.agroazores.models

import pt.tetrapi.fgf.agroazores.network.Api

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

data class Stock(
    val id: Int,
    val user: UserProfile,
    val product: Product,
    val minPurchase: Int,
    val minPurchaseString: String,
    val quantity: Int,
    val quantityString: String,
    val quantityLeft: Int,
    val quantityLeftString: String,
    val price: Double,
    val priceString: String,
    val date: String,
    val dateString: String
) {

    fun toJson(): String {
        return Api.gson.toJson(this)
    }

    companion object {

        fun fromJson(string: String): Stock {
            return Api.gson.fromJson(string, Stock::class.java)
        }

    }
}
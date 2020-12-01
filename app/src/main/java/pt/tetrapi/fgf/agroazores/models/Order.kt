package pt.tetrapi.fgf.agroazores.models

import pt.tetrapi.fgf.agroazores.network.Api

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

data class Order(
    val id: Int,
    val stock: Stock,
    val buyer: UserProfile,
    val status: Int,
    val statusString: String,
    val details: List<OrderDetails>,
    val date: String,
    val dateString: String,
    val priceString: String,
    val quantity: Int,
    val quantityString: String
) {

    fun toJson(): String {
        return Api.gson.toJson(this)
    }

    fun isPending() = status == 0
    fun isCompleted() = status == 1

    companion object {
        fun fromJson(string: String): Order = Api.gson.fromJson(string, Order::class.java)
    }

}
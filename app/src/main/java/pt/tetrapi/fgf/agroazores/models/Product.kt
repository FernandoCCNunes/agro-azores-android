package pt.tetrapi.fgf.agroazores.models

import pt.tetrapi.fgf.agroazores.network.Api

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

data class Product(
    val id: Int,
    val name: String,
    val image: String,
    val category: Category,
    val type: Int,
    val typeString: String,
    val color: String,
    val colorLight: String,
    val colorLighter: String,
) {
    fun toJson(): String {
        return Api.gson.toJson(this)
    }

    companion object {

        fun fromJson(string: String): Product {
            return Api.gson.fromJson(string, Product::class.java)
        }

    }

}
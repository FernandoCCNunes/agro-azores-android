package pt.tetrapi.fgf.agroazores.models

import com.google.gson.annotations.SerializedName

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

data class Address(
    val id: Int,
    val name: String,
    val county: String,
    val location: String,
    val number: String,
    val address: String,
    @SerializedName("postal_code")
    val postalCode: String
) {
}
package pt.tetrapi.fgf.agroazores.models

import com.google.gson.annotations.SerializedName

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

data class UserProfile (
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val contact: String,
    val nif: String,
    val company: Company,
    val image: String,

) {
}
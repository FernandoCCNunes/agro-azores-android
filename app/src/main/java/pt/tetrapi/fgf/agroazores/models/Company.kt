package pt.tetrapi.fgf.agroazores.models

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

data class Company(
    val id: Int,
    val name: String,
    val nif: String,
    val contact: String,
    val email: String,
    val image: String,
    val addresses: List<Address>
) {
}
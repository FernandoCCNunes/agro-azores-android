package pt.tetrapi.fgf.agroazores.models

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
)
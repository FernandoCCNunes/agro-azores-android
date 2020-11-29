package pt.tetrapi.fgf.agroazores.models

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

data class OrderDetails(
    val id: Int,
    val price: Double,
    val priceString: String,
    val quantity: Int,
    val quantityString: String,
) {
}
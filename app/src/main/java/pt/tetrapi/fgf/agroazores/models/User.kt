package pt.tetrapi.fgf.agroazores.models

import com.nando.debug.Debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.tetrapi.fgf.agroazores.network.Api

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

data class User(
    val id: Int,
    val email: String,
    val role: UserRole,
    val profile: UserProfile
) {
    var ordersPending: List<Order> = listOf()
    var ordersCompleted: List<Order> = listOf()
    var stockAvailable: List<Stock> = listOf()
    var stockFuture: List<Stock> = listOf()

    suspend fun getOrdersPending() = withContext(Dispatchers.Main) {
        val response = Api.getUserOrdersPending(id)
        val result = Api.getData(response)
        if (response.isSuccessful) {
            ordersPending = Api.gson.fromJson(result , Array<Order>::class.java).toList()
        }
    }

    suspend fun getOrdersCompleted() = withContext(Dispatchers.Main) {
        val response = Api.getUserOrdersCompleted(id)
        val result = Api.getData(response)
        if (response.isSuccessful) {
            ordersCompleted = Api.gson.fromJson(result , Array<Order>::class.java).toList()
        }
    }

    suspend fun getStockAvailable() = withContext(Dispatchers.Main) {
        val response = Api.getUserStockAvailable(id)
        val result = Api.getData(response)
        if (response.isSuccessful) {
            stockAvailable = Api.gson.fromJson(result , Array<Stock>::class.java).toList()
        }
    }

    suspend fun getAllStockAvailable() = withContext(Dispatchers.Main) {
        val response = Api.getUserStockAvailable(id)
        val result = Api.getData(response)
        if (response.isSuccessful) {
            stockAvailable = Api.gson.fromJson(result , Array<Stock>::class.java).toList()
        }
    }

    suspend fun getStockFuture() = withContext(Dispatchers.Main) {
        val response = Api.getUserStockFuture(id)
        val result = Api.getData(response)
        if (response.isSuccessful) {
            stockFuture = Api.gson.fromJson(result , Array<Stock>::class.java).toList()
        }
    }

    fun isProducer(): Boolean = role.id == 1
    fun isRetailer(): Boolean = role.id == 2
    fun isEndUser(): Boolean = role.id == 3

    companion object {
        fun fromJson(string: String): User = Api.gson.fromJson(string, User::class.java).apply {
            ordersPending = listOf()
            ordersCompleted = listOf()
            stockAvailable = listOf()
            stockFuture = listOf()
        }
    }

}
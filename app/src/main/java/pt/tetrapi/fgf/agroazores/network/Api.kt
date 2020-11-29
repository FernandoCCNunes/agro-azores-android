package pt.tetrapi.fgf.agroazores.network

import android.content.Context
import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.nando.debug.Debug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import ru.gildor.coroutines.okhttp.await

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

object Api {

    var client = OkHttpClient()

    val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    const val host = "http://10e24b1ccc99.ngrok.io"

    fun JSONObject.toRequestBody(): RequestBody {
        return toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    suspend fun getData(response: Response): String {
        try {
            if (response.body != null) return withContext(Dispatchers.IO) {response.body!!.string()}
        } catch (ex: Exception) {
            Debug(this, "getData() Exception -> ${ex.message} ").debug()
        }
        return ""
    }

    fun getUrl(suffix: String): String {
        return host + suffix
    }

    suspend fun getUser(id: Int): Response {
        val request = Request
            .Builder()
            .url(getUrl("/api/users/$id"))
            .get()
            .build()

        return withContext(Dispatchers.IO) { client.newCall(request).await() }
    }

    suspend fun getUserOrders(id: Int): Response {
        val request = Request
            .Builder()
            .url(getUrl("/api/users/$id/orders"))
            .get()
            .build()

        return withContext(Dispatchers.IO) { client.newCall(request).await() }
    }

    suspend fun getUserOrdersPending(id: Int): Response {
        val request = Request
            .Builder()
            .url(getUrl("/api/users/$id/orders/pending"))
            .get()
            .build()

        return withContext(Dispatchers.IO) { client.newCall(request).await() }
    }

    suspend fun getUserOrdersCompleted(id: Int): Response {
        val request = Request
            .Builder()
            .url(getUrl("/api/users/$id/orders/completed"))
            .get()
            .build()

        return withContext(Dispatchers.IO) { client.newCall(request).await() }
    }

    suspend fun getUserStock(id: Int): Response {
        val request = Request
            .Builder()
            .url(getUrl("/api/users/$id/stock"))
            .get()
            .build()

        return withContext(Dispatchers.IO) { client.newCall(request).await() }
    }

    suspend fun getUserStockAvailable(id: Int): Response {
        val request = Request
            .Builder()
            .url(getUrl("/api/users/$id/stock/available"))
            .get()
            .build()

        return withContext(Dispatchers.IO) { client.newCall(request).await() }
    }

    suspend fun getUserStockFuture(id: Int): Response {
        val request = Request
            .Builder()
            .url(getUrl("/api/users/$id/stock/future"))
            .get()
            .build()

        return withContext(Dispatchers.IO) { client.newCall(request).await() }
    }

    suspend fun createStock(json: JSONObject): Response {
        val request = Request
            .Builder()
            .url(getUrl("/api/stock"))
            .post(json.toRequestBody())
            .build()

        return withContext(Dispatchers.IO) { client.newCall(request).await() }
    }

    suspend fun getProducts(): Response {
        val request = Request
            .Builder()
            .url(getUrl("/api/products"))
            .get()
            .build()

        return withContext(Dispatchers.IO) { client.newCall(request).await() }
    }



}
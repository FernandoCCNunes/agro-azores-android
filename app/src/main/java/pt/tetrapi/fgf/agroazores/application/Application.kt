package pt.tetrapi.fgf.agroazores.application

import android.app.Application
import com.nando.debug.Debug
import com.nando.debug.DebugManager
import com.nando.debug.settings.DebuggerSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.tetrapi.fgf.agroazores.AppData
import pt.tetrapi.fgf.agroazores.BuildConfig
import pt.tetrapi.fgf.agroazores.models.Product
import pt.tetrapi.fgf.agroazores.models.User
import pt.tetrapi.fgf.agroazores.network.Api

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

class Application: Application() {

    override fun onCreate() {
        super.onCreate()

        DebugManager.init(
            DebuggerSettings
                .Builder(this, BuildConfig.DEBUG)
                .setDisplayCustomTags(true)
                .setUseSingleTag(true)
                .setDefaultTag("AgroAzores")
                .build()
        )

        getProducts()
    }

    private fun getProducts() {
        CoroutineScope(Dispatchers.Main).launch {
            Debug(this, "getProducts").debug()
            val response = Api.getProducts()
            val result = Api.getData(response)
            if (response.isSuccessful) {
                AppData.products = Api.gson.fromJson(result, Array<Product>::class.java).toList()
            }
        }
    }
}
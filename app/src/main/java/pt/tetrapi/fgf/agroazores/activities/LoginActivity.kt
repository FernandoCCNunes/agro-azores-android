package pt.tetrapi.fgf.agroazores.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.nando.debug.Debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.tetrapi.fgf.agroazores.AppData
import pt.tetrapi.fgf.agroazores.databinding.ActivityLoginBinding
import pt.tetrapi.fgf.agroazores.models.User
import pt.tetrapi.fgf.agroazores.network.Api

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

class LoginActivity: Activity() {

    private lateinit var xml: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xml = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(xml.root)
        setupLoginButton()
    }

    private fun setupLoginButton() {
        xml.loginBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val response = Api.getUser(100)
                val result = Api.getData(response)
                if (response.isSuccessful) {
                    AppData.user = User.fromJson(result)
                    navigateToApp()
                }
            }

        }
    }

    private fun navigateToApp() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}
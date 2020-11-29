package pt.tetrapi.fgf.agroazores.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import pt.tetrapi.fgf.agroazores.R
import pt.tetrapi.fgf.agroazores.databinding.ActivityMainBinding

class MainActivity : FragmentActivity() {

    private lateinit var xml: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xml = ActivityMainBinding.inflate(layoutInflater)
        setContentView(xml.root)
        setupNavigation()
    }

    private fun setupNavigation() {
        navController = findNavController(R.id.main_fragment)
        xml.bottomBar.onItemSelected = {
            when (it) {
                0 -> {
                    navController.navigate(R.id.orders)
                    updateHeader("Encomendas", R.drawable.ic_outline_receipt_long_black_24dp)
                }
                1 -> {
                    navController.navigate(R.id.catalog)
                    updateHeader("Catálogo", R.drawable.ic_outline_list_alt_black_24dp)
                }
                else -> {
                    navController.navigate(R.id.profile)
                    updateHeader("Perfil", R.drawable.ic_outline_person_24)
                }
            }
        }
    }

    private fun updateHeader(title: String, icon: Int) {
        xml.title.text = title
        xml.icon.setImageDrawable(ContextCompat.getDrawable(this, icon))
    }

}
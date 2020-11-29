package pt.tetrapi.fgf.agroazores.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.tetrapi.fgf.agroazores.databinding.ActivityCreateReservationBinding

class CreateReservationActivity : AppCompatActivity() {

    private lateinit var xml: ActivityCreateReservationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xml = ActivityCreateReservationBinding.inflate(layoutInflater)
        setContentView(xml.root)
        setupToolbar()
    }

    private fun setupToolbar () {
        xml.toolbar.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
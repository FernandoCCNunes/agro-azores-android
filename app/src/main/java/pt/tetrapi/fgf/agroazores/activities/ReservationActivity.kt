package pt.tetrapi.fgf.agroazores.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.nando.debug.Debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.tetrapi.fgf.agroazores.R
import pt.tetrapi.fgf.agroazores.databinding.ActivityReservationBinding
import pt.tetrapi.fgf.agroazores.dialogs.DialogFragment
import pt.tetrapi.fgf.agroazores.models.Product
import pt.tetrapi.fgf.agroazores.models.Stock
import pt.tetrapi.fgf.agroazores.network.Api
import pt.tetrapi.fgf.agroazores.objects.AppData
import pt.tetrapi.fgf.agroazores.objects.Constants
import pt.tetrapi.fgf.agroazores.objects.ResultCodes
import pt.tetrapi.fgf.agroazores.utility.round2Decimals

class ReservationActivity : AppCompatActivity() {

    private lateinit var xml: ActivityReservationBinding

    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xml = ActivityReservationBinding.inflate(layoutInflater)
        setContentView(xml.root)
        getStock()
        setupToolbar()
        setupHeader()
        setupContent()
        setupFooter()
        setupTheme()
    }

    private fun getStock() {
        product = Product.fromJson(intent.getStringExtra(Constants.PRODUCT)!!)
    }

    private fun setupToolbar () {
        xml.toolbar.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun setupHeader() {
        Glide.with(xml.image).load(Api.getUrl(product.image)).into(xml.image)
        xml.name.text = product.name
    }

    private fun setupContent() {
        xml.unit.text = product.typeString
    }

    private fun setupTheme() {
        window.statusBarColor = Color.parseColor(product.color)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        xml.toolbar.setBackgroundColor(Color.parseColor(product.color))
        xml.nameContainer.setCardBackgroundColor(Color.parseColor(product.colorLight))
    }

    private fun setupFooter() {
        xml.price.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                calculateTotalPrice()
            }
        })

        xml.quantity.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                calculateTotalPrice()
            }
        })

        xml.reservationBtn.setOnClickListener {
            reserveProduct()
        }
    }

    private fun calculateTotalPrice() {
        val quantityString = xml.quantity.text.toString()
        val priceString = xml.price.text.toString()

        if (quantityString.isNotEmpty() && priceString.isNotEmpty()) {
            val quantity = quantityString.toInt()
            val price = priceString.toDouble()
            val total = quantity * price
            Debug(this, "total -> $total").debug()
            xml.totalPrice.text = total.round2Decimals()
        }
    }

    private fun reserveProduct() {
        val quantityString = xml.quantity.text.toString()
        if (quantityString.isEmpty()) return
        val quantity = quantityString.toInt()
        val priceString = xml.price.text.toString()
        if (priceString.isEmpty()) return
        val price = priceString.toDouble()

        val json = JSONObject()
            .put("buyer_id", AppData.user.id)
            .put("quantity", quantity)
            .put("price", price)
        CoroutineScope(Dispatchers.Main).launch {
            val response = Api.reserveProduct(product.id, json)
            val result = Api.getData(response)
            Debug(this, "result -> $result").debug()
            if(response.isSuccessful) {
                showPurchaseSuccess()
            } else {
                showPurchaseFailed()
            }
        }
    }

    private fun showPurchaseSuccess() {
        val dialog = DialogFragment(
            this,
            "Parabéns",
            "A sua reserva foi efetuada com successo",
            R.drawable.ic_round_check_circle_24
        )
        dialog.setOnDismissListener {
            setResult(ResultCodes.OK)
            finish()
        }
        dialog.show()
    }

    private fun showPurchaseFailed() {
        val dialog = DialogFragment(
            this,
            "Erro",
            "Ocorreu um erro com a sua reserva",
            R.drawable.ic_round_error_24
        )
        dialog.show()
    }

}
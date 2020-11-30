package pt.tetrapi.fgf.agroazores.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import pt.tetrapi.fgf.agroazores.databinding.ActivityCreateReservationBinding
import pt.tetrapi.fgf.agroazores.models.Stock
import pt.tetrapi.fgf.agroazores.network.Api
import pt.tetrapi.fgf.agroazores.objects.Constants

class CreateReservationActivity : AppCompatActivity() {

    private lateinit var xml: ActivityCreateReservationBinding

    private lateinit var stock: Stock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xml = ActivityCreateReservationBinding.inflate(layoutInflater)
        setContentView(xml.root)
        setupToolbar()
        getStock()
        setupHeader()
        setupContent()
        setupFooter()
    }

    private fun setupToolbar () {
        xml.toolbar.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun getStock() {
        stock = Stock.fromJson(intent.getStringExtra(Constants.STOCK)!!)
    }

    private fun setupHeader() {
        Glide.with(xml.image).load(Api.getUrl(stock.product.image)).into(xml.image)
        xml.name.text = stock.product.name
    }

    private fun setupContent() {
        xml.availableQuantity.text = stock.quantityLeftString
        if (stock.minPurchase > 0) {
            xml.minQuantityCard.visibility = View.VISIBLE
            xml.minQuantity.text = stock.minPurchaseString
        }
        xml.price.text = stock.priceString
    }

    private fun setupFooter() {
        xml.quantity.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {calculateTotalPrice()}
        })
    }

    private fun calculateTotalPrice() {
        val quantity = xml.quantity.text.toString().toInt()
        val total = quantity * stock.price
        xml.totalPrice.text = total.toString()

        xml.addReservation.setOnClickListener {

        }
    }

}
package pt.tetrapi.fgf.agroazores.activities


import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.nando.debug.Debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.tetrapi.fgf.agroazores.R
import pt.tetrapi.fgf.agroazores.databinding.ActivityOrderBinding
import pt.tetrapi.fgf.agroazores.dialogs.DialogFragment
import pt.tetrapi.fgf.agroazores.models.Product
import pt.tetrapi.fgf.agroazores.models.Stock
import pt.tetrapi.fgf.agroazores.network.Api
import pt.tetrapi.fgf.agroazores.objects.AppData
import pt.tetrapi.fgf.agroazores.objects.Constants
import pt.tetrapi.fgf.agroazores.objects.ResultCodes
import pt.tetrapi.fgf.agroazores.utility.round2Decimals


class OrderActivity : AppCompatActivity() {

    private lateinit var xml: ActivityOrderBinding

    private lateinit var stock: Stock
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xml = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(xml.root)
        getStock()
        setupToolbar()
        setupHeader()
        setupContent()
        setupFooter()
        setupTheme()
        showPurchaseFailed()
    }

    private fun setupToolbar () {
        xml.toolbar.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun getStock() {
        stock = Stock.fromJson(intent.getStringExtra(Constants.STOCK)!!)
        product = Product.fromJson(intent.getStringExtra(Constants.PRODUCT)!!)
        Debug(this, "stock -> $stock").debug()
        Debug(this, "product -> $product").debug()
    }

    private fun setupHeader() {
        Glide.with(xml.image).load(Api.getUrl(product.image)).into(xml.image)
        xml.name.text = product.name
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
        xml.quantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                if (editable == null) return
                val valueString = editable.toString()
                if (valueString.isEmpty()) return
                val value = editable.toString().toInt()
                if (stock.minPurchase < stock.quantityLeft) {
                    if (value > stock.quantityLeft) xml.quantity.setText(stock.quantityLeft.toString())
                }
                calculateTotalPrice()
                validateOrderButton()
            }

        })

        if (stock.minPurchase > stock.quantityLeft) {
            xml.purchaseBtn.isEnabled = false
            xml.quantity.isEnabled = false
            xml.quantity.setText(stock.quantityLeft.toString())
        } else {
            xml.quantity.setText(stock.minPurchase.toString())
        }

        calculateTotalPrice()

        xml.purchaseBtn.setOnClickListener {
            purchaseStock()
        }
    }

    private fun setupTheme() {
        window.statusBarColor = Color.parseColor(product.color)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        xml.toolbar.setBackgroundColor(Color.parseColor(product.color))
        xml.nameContainer.setCardBackgroundColor(Color.parseColor(product.colorLight))
    }

    private fun purchaseStock() {
        val quantityString = xml.quantity.text.toString()
        if (quantityString.isEmpty()) return
        val quantity = quantityString.toInt()
        CoroutineScope(Dispatchers.Main).launch {
            val response = Api.purchaseStock(
                stock.id,
                JSONObject()
                    .put("buyer_id", AppData.user.id)
                    .put("product_id", product.id)
                    .put("quantity", quantity)
            )
            val result = Api.getData(response)
            Debug(this, "result -> $result").debug()
            if (response.isSuccessful) {
                showPurchaseSuccess()
            } else {
                showPurchaseFailed()
            }
        }
    }

    private fun validateOrderButton() {
        val quantityString = xml.quantity.text.toString()
        if (quantityString.isEmpty()) return
        val quantity = quantityString.toInt()
        xml.purchaseBtn.isEnabled = quantity >= stock.minPurchase
        if (xml.purchaseBtn.isEnabled) {
            xml.purchaseBtn.strokeColor = ContextCompat.getColorStateList(
                this,
                R.color.colorPrimaryDark
            )
            xml.purchaseBtn.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            xml.purchaseBtn.iconTint = ContextCompat.getColorStateList(
                this,
                R.color.colorPrimaryDark
            )
            xml.purchaseBtn.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorLightGreen
                )
            )
        } else {
            xml.purchaseBtn.strokeColor = ContextCompat.getColorStateList(
                this,
                R.color.colorBlack54Percent
            )
            xml.purchaseBtn.setTextColor(ContextCompat.getColor(this, R.color.colorBlack54Percent))
            xml.purchaseBtn.iconTint = ContextCompat.getColorStateList(
                this,
                R.color.colorBlack54Percent
            )
            xml.purchaseBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLightGrey))
        }
    }

    private fun showPurchaseSuccess() {
        val dialog = DialogFragment(
            this,
            "Parabéns",
            "A sua compra foi efetuada com successo",
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
            "Ocorreu um erro com a sua compra",
            R.drawable.ic_round_error_24
        )
        dialog.show()
    }

    private fun calculateTotalPrice() {
        val quantity = xml.quantity.text.toString().toInt()
        val total = quantity * stock.price
        xml.totalPrice.text = total.round2Decimals()
    }
}
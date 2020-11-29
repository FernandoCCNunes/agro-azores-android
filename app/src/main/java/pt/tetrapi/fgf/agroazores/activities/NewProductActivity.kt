package pt.tetrapi.fgf.agroazores.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.nando.debug.Debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.tetrapi.fgf.agroazores.AppData
import pt.tetrapi.fgf.agroazores.R
import pt.tetrapi.fgf.agroazores.databinding.ActivityNewProductBinding
import pt.tetrapi.fgf.agroazores.models.Product
import pt.tetrapi.fgf.agroazores.models.Stock
import pt.tetrapi.fgf.agroazores.network.Api
import pt.tetrapi.fgf.agroazores.utility.hideKeyboard
import tech.hibk.searchablespinnerlibrary.SearchableDialog
import tech.hibk.searchablespinnerlibrary.SearchableItem


class NewProductActivity : AppCompatActivity() {

    private lateinit var xml: ActivityNewProductBinding

    private var selectedProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xml = ActivityNewProductBinding.inflate(layoutInflater)
        setContentView(xml.root)
        CoroutineScope(Dispatchers.Main).launch {
            setupToolbar()
            setupProductHeader()
            setupInputs()
            setupAddProductButton()
        }
    }

    private fun setupToolbar() {
        Debug(this, "setupToolbar").debug()
        xml.toolbar.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun setupProductHeader() {
        val productsName: List<SearchableItem> = AppData.products.map { SearchableItem(it.id.toLong(),it.name) }

        xml.productType.items = productsName
        xml.productType.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedProduct = AppData.products[p2]
                Glide.with(xml.image).load(Api.getUrl(AppData.products[p2].image)).into(xml.image)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun setupInputs() {
        xml.addDate.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                xml.dateOverlay.visibility = View.GONE
                xml.addDate.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            } else {
                hideKeyboard(this)
                xml.date.clearFocus()
                xml.dateOverlay.visibility = View.VISIBLE
                xml.addDate.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
        }

        xml.addMinQuantity.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                xml.minQuantityOverlay.visibility = View.GONE
                xml.addMinQuantity.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            } else {
                hideKeyboard(this)
                xml.minQuantity.clearFocus()
                xml.minQuantityOverlay.visibility = View.VISIBLE
                xml.addMinQuantity.setTextColor(ContextCompat.getColor(this, R.color.black))

            }
        }
    }

    private fun setupAddProductButton() {
        xml.addProductBtn.setOnClickListener {
            if (selectedProduct == null) return@setOnClickListener
            CoroutineScope(Dispatchers.Main).launch {
                val json = JSONObject()
                    .put("user_id", AppData.user.id)
                    .put("product_id", selectedProduct!!.id)
                    .put("quantity", xml.quantity.text.toString().toInt())
                    .put("price", xml.price.text.toString().toDouble())

                if (xml.addDate.isChecked) json.put("stock_date", xml.date.text.toString())
                if (xml.addMinQuantity.isChecked) json.put("min_quantity", xml.minQuantity.text.toString().toInt())

                Debug(this, "json -> $json").debug()
                val response = Api.createStock(json)
                val result = Api.getData(response)
                Debug(this, "result -> $result").debug()
                if (response.isSuccessful) {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }
}
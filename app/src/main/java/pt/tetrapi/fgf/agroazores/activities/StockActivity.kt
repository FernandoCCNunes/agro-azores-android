package pt.tetrapi.fgf.agroazores.activities

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.nando.debug.Debug
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import pt.tetrapi.fgf.agroazores.R
import pt.tetrapi.fgf.agroazores.databinding.ActivityStockBinding
import pt.tetrapi.fgf.agroazores.dialogs.DialogFragment
import pt.tetrapi.fgf.agroazores.models.Product
import pt.tetrapi.fgf.agroazores.models.Stock
import pt.tetrapi.fgf.agroazores.network.Api
import pt.tetrapi.fgf.agroazores.objects.AppData
import pt.tetrapi.fgf.agroazores.objects.Constants
import pt.tetrapi.fgf.agroazores.objects.ResultCodes
import pt.tetrapi.fgf.agroazores.utility.getDate
import pt.tetrapi.fgf.agroazores.utility.translateDate
import java.text.SimpleDateFormat
import java.util.*

class StockActivity : FragmentActivity() {

    private lateinit var xml: ActivityStockBinding

    private lateinit var product: Product
    private var stock: Stock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xml = ActivityStockBinding.inflate(layoutInflater)
        setContentView(xml.root)
        getStock()
        setupToolbar()
        setupHeader()
        setupContent()
        setupFooter()
        setupTheme()
    }

    private fun getStock() {
        if (intent.hasExtra(Constants.STOCK)) {
            stock = Stock.fromJson(intent.getStringExtra(Constants.STOCK)!!)
            product = stock!!.product
        } else {
            product = Product.fromJson(intent.getStringExtra(Constants.PRODUCT)!!)
        }
    }

    private fun setupToolbar () {
        if (stock != null) {
            xml.toolbar.title = "Visualização de produto"
        }
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
        stock?.let {
            xml.quantity.setText(it.quantityLeft.toString())
            xml.minQuantity.setText(it.minPurchase.toString())
            xml.price.setText(it.price.toString())
            xml.date.setText(it.date)
        }

        xml.dateOverlay.setOnClickListener {
            val dateString = xml.date.text.toString()
            val cal = getDate(dateString)
            val dpd: DatePickerDialog = DatePickerDialog.newInstance(
                { _, year, monthOfYear, dayOfMonth ->
                    xml.date.setText(translateDate(year, monthOfYear +1, dayOfMonth))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show(supportFragmentManager, "Datepickerdialog")
        }
    }


    private fun setupTheme() {
        window.statusBarColor = Color.parseColor(product.color)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        xml.toolbar.setBackgroundColor(Color.parseColor(product.color))
        xml.nameContainer.setCardBackgroundColor(Color.parseColor(product.colorLight))
    }

    private fun setupFooter() {
        if (stock == null) {
            xml.removeBtn.text = "Descartar"
            xml.removeBtn.setOnClickListener {
                discardStock()
            }

            xml.saveBtn.text = "Criar"
            xml.saveBtn.setOnClickListener {
                createStock()
            }
        } else {
            xml.removeBtn.text = "Remover"
            xml.removeBtn.setOnClickListener {
                removeStock()
            }

            xml.saveBtn.text = "Guardar"
            xml.saveBtn.setOnClickListener {
                updateStock()
            }
        }
    }

    private fun getStockJson(): JSONObject? {
        val quantityString = xml.quantity.text.toString()
        if (quantityString.isEmpty()) return null
        val quantity = quantityString.toInt()

        val priceString = xml.price.text.toString()
        if (priceString.isEmpty()) return null
        val price = quantityString.toDouble()

        val dateString = xml.date.text.toString()
        val minQuantityString = xml.minQuantity.text.toString()

        val json = JSONObject()
            .put("user_id", AppData.user.id)
            .put("product_id", product.id)
            .put("quantity", quantity)
            .put("price", price)

        if (dateString.isNotEmpty()) json.put("stock_date", dateString)
        if (minQuantityString.isNotEmpty()) json.put("min_quantity", minQuantityString)

        return json
    }

    private fun discardStock() {
        val dialog = DialogFragment(
            this,
            "Atenção",
            "Por favor confirme que deseja descartar este produto",
            R.drawable.ic_round_warning_24
        )
        dialog.setPositionButton("Confirmar", R.color.colorYellowDark) {
            dialog.dismiss()
            exitActivity(ResultCodes.CANCEL)
        }
        dialog.show()
    }

    private fun createStock() {
        val quantityString = xml.quantity.text.toString()
        if (quantityString.isEmpty()) return
        val quantity = quantityString.toInt()

        val priceString = xml.price.text.toString()
        if (priceString.isEmpty()) return
        val price = quantityString.toDouble()

        val dateString = xml.date.text.toString()
        val minQuantityString = xml.minQuantity.text.toString()

        val json = JSONObject()
            .put("user_id", AppData.user.id)
            .put("product_id", product.id)
            .put("quantity", quantity)
            .put("price", price)

        if (dateString.isNotEmpty()) json.put("stock_date", dateString)
        if (minQuantityString.isNotEmpty()) json.put("min_quantity", minQuantityString)

        CoroutineScope(Dispatchers.Main).launch {
            val response = Api.createStock(json)
            val result = Api.getData(response)
            Debug(this, "result -> $result").debug()
            if (response.isSuccessful) {
                showDialogSuccess("Parabéns", "O seu produto foi criado com successo")
            } else {
                showDialogError("Erro", "ocorreu um erro a criar o seu produto")
            }
        }
    }

    private fun removeStock() {
        val dialog = DialogFragment(
            this,
            "Atenção",
            "Por favor confirme que deseja remover este produto",
            R.drawable.ic_round_warning_24
        )
        dialog.setPositionButton("Confirmar", R.color.colorYellowDark) {
            dialog.dismiss()
            stock?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    val response = Api.removeStock(it.id)
                    val result = Api.getData(response)
                    Debug(this, "result -> $result").debug()
                    if (response.isSuccessful) {
                        showDialogSuccess("Parabéns", "O seu produto foi removido com sucesso")
                    } else {
                        showDialogError("Erro", "Ocorreu um erro a remover o seu produto")
                    }
                }
            }
        }
        dialog.show()
    }

    private fun updateStock() {
        val json = getStockJson()?: return
        stock?.let {
            CoroutineScope(Dispatchers.Main).launch {
                val response = Api.updateStock(it.id, json)
                val result = Api.getData(response)
                Debug(this, "result -> $result").debug()
                if (response.isSuccessful) {
                    showDialogSuccess("Parabéns", "O seu produto foi atualizado com sucesso")
                } else {
                    showDialogSuccess("Erro", "Ocorreu um erro a atualizar o seu produto")
                }
            }
        }
    }

    private fun exitActivity(resultCode: Int) {
        setResult(resultCode)
        finish()
    }

    private fun showDialogSuccess(title: String, message: String) {
        val dialog = DialogFragment(
            this,
            title,
            message,
            R.drawable.ic_round_check_circle_24
        )
        dialog.setOnDismissListener {
            exitActivity(ResultCodes.OK)
        }
        dialog.show()
    }

    private fun showDialogError(title: String, message: String) {
        val dialog = DialogFragment(
            this,
            title,
            message,
            R.drawable.ic_round_error_24
        )
        dialog.show()
    }

}
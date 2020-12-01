package pt.tetrapi.fgf.agroazores.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.nando.debug.Debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.tetrapi.fgf.agroazores.R
import pt.tetrapi.fgf.agroazores.objects.AppData
import pt.tetrapi.fgf.agroazores.databinding.ActivityViewOrderBinding
import pt.tetrapi.fgf.agroazores.dialogs.DialogFragment
import pt.tetrapi.fgf.agroazores.models.Order
import pt.tetrapi.fgf.agroazores.network.Api
import pt.tetrapi.fgf.agroazores.objects.Constants
import pt.tetrapi.fgf.agroazores.objects.ResultCodes
import pt.tetrapi.fgf.agroazores.utility.getDate
import pt.tetrapi.fgf.agroazores.utility.getTimezonePattern
import pt.tetrapi.fgf.agroazores.utility.translateDate

class ViewOrderActivity : AppCompatActivity() {

    private lateinit var xml: ActivityViewOrderBinding

    private lateinit var order: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xml = ActivityViewOrderBinding.inflate(layoutInflater)
        setContentView(xml.root)
        getOrder()
        setupToolbar()
        setupHeader()
        setupContent()
        setupTheme()
        setupFooter()
    }

    private fun getOrder() {
        order = Order.fromJson(intent.getStringExtra(Constants.ORDER)!!)
    }

    private fun setupToolbar () {
        xml.toolbar.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun setupHeader() {
        Glide.with(xml.image).load(Api.getUrl(order.stock.product.image)).into(xml.image)
        xml.name.text = order.stock.product.name
    }

    private fun setupContent() {
        xml.quantity.text = order.quantityString
        xml.price.text = order.priceString
        xml.date.text = translateDate(getDate(order.date, "yyyy-MM-dd HH:mm:ss"))
    }

    private fun setupTheme() {
        window.statusBarColor = Color.parseColor(order.stock.product.color)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        xml.toolbar.setBackgroundColor(Color.parseColor(order.stock.product.color))
        xml.nameContainer.setCardBackgroundColor(Color.parseColor(order.stock.product.colorLight))
    }

    private fun setupFooter() {
        if (order.isPending()) {
            xml.cancelBtn.visibility = View.VISIBLE
            xml.cancelBtn.setOnClickListener {
                cancelOrder()
            }
        }

        if (AppData.user.isProducer() && order.isPending()) {
            xml.approveBtn.visibility = View.VISIBLE
            xml.approveBtn.setOnClickListener {
                approveOrder()
            }
        }
    }

    private fun cancelOrder() {
        val dialog = DialogFragment(
            this,
            "Atenção",
            "Por favor confirme que deseja cancelar esta encomenda",
            R.drawable.ic_round_warning_24
        )
        dialog.setPositionButton("Confirmar", R.color.colorYellowDark) {
            dialog.dismiss()
            CoroutineScope(Dispatchers.Main).launch {
                val response = Api.cancelOrder(order.id)
                val result = Api.getData(response)
                Debug(this, "result -> $result").debug()
                if (response.isSuccessful) {
                    showDialogSuccess("A encomenda foi cancelada com sucesso")
                } else {
                    showDialogError("Erro", "Não foi possivel cancelar a encomenda")
                }
            }
        }
        dialog.show()
    }

    private fun approveOrder() {
        CoroutineScope(Dispatchers.Main).launch {
            val response = Api.approveOrder(order.id)
            val result = Api.getData(response)
            Debug(this, "result -> $result").debug()
            if (response.isSuccessful) {
                showDialogSuccess("A encomenda foi aprovada com sucesso")
            } else {
                showDialogError("Erro", "Não foi possivel aprovar a encomenda")
            }
        }
    }

    private fun exitActivity(resultCode: Int) {
        setResult(resultCode)
        finish()
    }

    private fun showDialogSuccess(message: String) {
        val dialog = DialogFragment(
            this,
            "Parabéns",
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
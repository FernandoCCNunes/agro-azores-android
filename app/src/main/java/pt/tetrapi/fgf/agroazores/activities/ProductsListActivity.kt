package pt.tetrapi.fgf.agroazores.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pt.tetrapi.fgf.agroazores.objects.AppData
import pt.tetrapi.fgf.agroazores.databinding.ActivityProductsListBinding
import pt.tetrapi.fgf.agroazores.databinding.CardProductsListBinding
import pt.tetrapi.fgf.agroazores.network.Api
import pt.tetrapi.fgf.agroazores.objects.Constants
import pt.tetrapi.fgf.agroazores.objects.RequestCodes
import pt.tetrapi.fgf.agroazores.objects.ResultCodes

class ProductsListActivity : AppCompatActivity() {

    private lateinit var xml: ActivityProductsListBinding

    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xml = ActivityProductsListBinding.inflate(layoutInflater)
        setContentView(xml.root)
        setupToolbar()
        setupList()
    }

    private fun setupToolbar () {
        xml.toolbar.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun setupList() {
        adapter = Adapter(this)
        xml.list.adapter = adapter
        xml.list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCodes.STOCK_ACTIVITY && resultCode == ResultCodes.OK || resultCode == ResultCodes.ERROR) {
            finish()
        }
    }

    class Adapter(private val activity: ProductsListActivity): RecyclerView.Adapter<Adapter.ViewHolder>() {

        class ViewHolder(val xml: CardProductsListBinding): RecyclerView.ViewHolder(xml.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(CardProductsListBinding.inflate(LayoutInflater.from(activity), parent, false))
        }

        override fun getItemCount(): Int = AppData.products.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = AppData.products[position]

            Glide.with(holder.xml.image).load(Api.getUrl(product.image)).into(holder.xml.image)
            holder.xml.name.text = product.name

            holder.itemView.setOnClickListener {
                activity.startActivityForResult(Intent(activity, StockActivity::class.java)
                    .putExtra(Constants.PRODUCT, product.toJson()),
                    RequestCodes.STOCK_ACTIVITY)
            }
        }
    }
}
package pt.tetrapi.fgf.agroazores.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nando.debug.Debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.tetrapi.fgf.agroazores.AppData
import pt.tetrapi.fgf.agroazores.activities.CreateReservationActivity
import pt.tetrapi.fgf.agroazores.databinding.*
import pt.tetrapi.fgf.agroazores.interfaces.CatalogInterface
import pt.tetrapi.fgf.agroazores.network.Api


class CatalogForSaleFragment : Fragment(), CatalogInterface {

    private lateinit var xml: FragmentCatalogInProductionBinding

    private lateinit var adapter: Adapter

    lateinit var parent: CatalogFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        xml = FragmentCatalogInProductionBinding.inflate(inflater, container, false)
        return xml.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getStockAvailable()
    }

    private fun getStockAvailable() {
        CoroutineScope(Dispatchers.Main).launch {
            if (AppData.user.stockAvailable.isEmpty()) {
                AppData.user.getStockAvailable()
            }
            setupList()
        }
    }

    private fun setupList() {
        adapter = Adapter(requireContext(), this@CatalogForSaleFragment)
        xml.list.adapter = adapter
        xml.list.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        parent.stockForSaleRefreshListener = { refreshList() }

    }

    private fun refreshList() {
        if (this::adapter.isInitialized) {
            CoroutineScope(Dispatchers.Main).launch {
                AppData.user.getStockAvailable()
                adapter.notifyDataSetChanged()
            }
        }
    }

    class Adapter(private val context: Context, private val fragment: CatalogForSaleFragment): RecyclerView.Adapter<CatalogInterface.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogInterface.ViewHolder {
            return CatalogInterface.ViewHolder.getViewHolder(getViewTypeForUser(), context, parent)
        }

        override fun getItemCount(): Int = AppData.user.stockAvailable.size

        override fun onBindViewHolder(holder: CatalogInterface.ViewHolder, position: Int) {
            when(holder) {
                is CatalogInterface.ViewHolder.BuyerViewHolder -> onBindViewHolder(holder, position)
                is CatalogInterface.ViewHolder.SellerViewHolder -> onBindViewHolder(holder, position)
            }
        }

        private fun onBindViewHolder(holder: CatalogInterface.ViewHolder.SellerViewHolder, position: Int) {
            val stock = AppData.user.stockAvailable[position]

            Glide.with(holder.xml.image).load(Api.getUrl(stock.product.image)).into(holder.xml.image)
            holder.xml.product.text = stock.product.name
            holder.xml.dateValue.text = stock.date
            holder.xml.price.text = stock.priceString
            holder.xml.quantity.text = stock.quantityString
        }

        private fun onBindViewHolder(holder: CatalogInterface.ViewHolder.BuyerViewHolder, position: Int) {
            val stock = AppData.user.stockAvailable[position]

            Glide.with(holder.xml.image).load(Api.getUrl(stock.product.image)).into(holder.xml.image)
            holder.xml.producer.text = stock.product.name
            holder.xml.date.text = stock.date
            holder.xml.price.text = stock.priceString
            holder.xml.quantity.text = stock.quantityString

            holder.itemView.setOnClickListener {
                fragment.requireContext().startActivity(Intent(fragment.requireContext(), CreateReservationActivity::class.java))
            }
        }

        private fun getViewTypeForUser(): Int {
            return if (AppData.user.isProducer()) 0
            else 1
        }
    }

    companion object {
        fun newInstance() = CatalogForSaleFragment()
    }
}
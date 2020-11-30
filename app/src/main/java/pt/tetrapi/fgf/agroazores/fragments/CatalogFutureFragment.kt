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
import pt.tetrapi.fgf.agroazores.databinding.FragmentCatalogInProductionBinding
import pt.tetrapi.fgf.agroazores.databinding.ViewCatalogBuyerEmptyBinding
import pt.tetrapi.fgf.agroazores.databinding.ViewCatalogSellerEmptyBinding
import pt.tetrapi.fgf.agroazores.databinding.ViewLoadingBinding
import pt.tetrapi.fgf.agroazores.interfaces.CatalogInterface
import pt.tetrapi.fgf.agroazores.network.Api
import pt.tetrapi.fgf.agroazores.objects.Constants

class CatalogFutureFragment : Fragment() {

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
        setupList()
    }

    fun getStockFuture() {
        CoroutineScope(Dispatchers.Main).launch {
            setLoadingView()
            AppData.user.getStockFuture()
            if (AppData.user.stockFuture.isEmpty()) {
                if (AppData.user.isRetailer()) {
                    inflateAndShowRetailerEmptyView()
                }

                if (AppData.user.isProducer()) {
                    inflateAndShowProducerEmptyView()
                }
            } else {
                adapter.notifyDataSetChanged()
                if (xml.root.nextView == xml.root.getChildAt(0)) {
                    xml.root.showNext()
                }
            }
        }
    }

    private fun setupList() {
        if (!this::adapter.isInitialized) {
            adapter = Adapter(requireContext(), this)
            xml.list.adapter = adapter
            xml.list.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setLoadingView() {
        if (AppData.user.ordersPending.isEmpty()) {
            xml.emptyCatalogView.removeAllViews()
            val xml = ViewLoadingBinding.inflate(LayoutInflater.from(requireContext()), xml.emptyCatalogView, true)
            xml.message.text = if (parent.selectedProduct != null) {
                "Buscando stock de  ${parent.selectedProduct!!.name} disponível"
            } else {
                "Buscando stock disponível"
            }
            if (this.xml.root.nextView == this.xml.root.getChildAt(1)) {
                this.xml.root.showNext()
            }
        }
    }

    private fun inflateAndShowRetailerEmptyView() {
        xml.emptyCatalogView.removeAllViews()
        val xml = ViewCatalogBuyerEmptyBinding.inflate(LayoutInflater.from(requireContext()), this.xml.emptyCatalogView, true)
        xml.catalogMessage.text = if (parent.selectedProduct == null) {
            "Não existe stock disponível"
        } else {
            "Não existe stock de ${parent.selectedProduct!!.name}s disponível"
        }
        xml.makeReservation.setOnClickListener {
            // Add New create reservation for product
        }
        if (this.xml.root.nextView == this.xml.root.getChildAt(1)) {
            this.xml.root.showNext()
        }
    }

    private fun inflateAndShowProducerEmptyView() {
        xml.emptyCatalogView.removeAllViews()
        val xml = ViewCatalogSellerEmptyBinding.inflate(layoutInflater, xml.emptyCatalogView, true)
        xml.catalogMessage.text = "Não tens produtos disponiveis"
        if (this.xml.root.nextView == this.xml.root.getChildAt(1)) {
            this.xml.root.showNext()
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::adapter.isInitialized) {
            getStockFuture()
        }
    }

    class Adapter(private val context: Context, private val fragment: CatalogFutureFragment): RecyclerView.Adapter<CatalogInterface.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogInterface.ViewHolder {
            return CatalogInterface.ViewHolder.getViewHolder(getViewTypeForUser(), context, parent)
        }

        override fun getItemCount(): Int = AppData.user.stockFuture.size

        override fun onBindViewHolder(holder: CatalogInterface.ViewHolder, position: Int) {
            when(holder) {
                is CatalogInterface.ViewHolder.SellerViewHolder -> onBindViewHolder(holder, position)
                is CatalogInterface.ViewHolder.BuyerViewHolder -> onBindViewHolder(holder, position)
            }
        }

        private fun onBindViewHolder(holder: CatalogInterface.ViewHolder.SellerViewHolder, position: Int) {
            val stock = AppData.user.stockFuture[position]

            Glide.with(holder.xml.image).load(Api.getUrl(stock.product.image)).into(holder.xml.image)
            holder.xml.product.text = stock.product.name
            holder.xml.dateKey.text = "Disponível:"
            holder.xml.dateValue.text = stock.date
            holder.xml.price.text = stock.priceString
            holder.xml.quantity.text = stock.quantityLeftString
        }

        private fun onBindViewHolder(holder: CatalogInterface.ViewHolder.BuyerViewHolder, position: Int) {
            val stock = AppData.user.stockFuture[position]

            Glide.with(holder.xml.image).load(Api.getUrl(stock.product.image)).into(holder.xml.image)
            holder.xml.producer.text = stock.product.name
            holder.xml.date.text = stock.date
            holder.xml.price.text = stock.priceString
            holder.xml.quantity.text = stock.quantityLeftString

            holder.itemView.setOnClickListener {
                fragment.requireContext().startActivity(
                    Intent(
                        fragment.requireContext(),
                        CreateReservationActivity::class.java
                    )
                        .putExtra(Constants.STOCK, stock.toJson())
                )
            }
        }

        private fun getViewTypeForUser(): Int {
            return if (AppData.user.isProducer()) 0
            else 1
        }
    }

    companion object {

        fun newInstance() = CatalogFutureFragment()

    }
}
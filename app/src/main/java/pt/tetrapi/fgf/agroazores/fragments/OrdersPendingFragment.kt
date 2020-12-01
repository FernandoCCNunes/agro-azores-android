package pt.tetrapi.fgf.agroazores.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pt.tetrapi.fgf.agroazores.activities.ViewOrderActivity
import pt.tetrapi.fgf.agroazores.objects.AppData
import pt.tetrapi.fgf.agroazores.databinding.*
import pt.tetrapi.fgf.agroazores.network.Api
import pt.tetrapi.fgf.agroazores.objects.Constants
import pt.tetrapi.fgf.agroazores.objects.RequestCodes

class OrdersPendingFragment : Fragment() {

    private lateinit var xml: FragmentOrdersPendingBinding

    private lateinit var adapter: Adapter

    lateinit var parent: OrdersFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        xml = FragmentOrdersPendingBinding.inflate(inflater, container, false)
        return xml.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
    }

    private fun getOrdersPending() {
        CoroutineScope(Dispatchers.Main).launch {
            setLoadingView()
            AppData.user.getOrdersPending()
            if (AppData.user.ordersPending.isEmpty()) {
                inflateAndShowEmptyView()
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
            adapter = Adapter(requireActivity())
            xml.list.adapter = adapter
            xml.list.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setLoadingView() {
        if (AppData.user.ordersPending.isEmpty()) {
            xml.emptyOrderView.removeAllViews()
            val xml = ViewLoadingBinding.inflate(LayoutInflater.from(requireContext()), xml.emptyOrderView, true)
            xml.message.text = "Buscando encomendas pendentes"
            if (this.xml.root.nextView == this.xml.root.getChildAt(1)) {
                this.xml.root.showNext()
            }
        }
    }

    private fun inflateAndShowEmptyView() {
        xml.emptyOrderView.removeAllViews()
        val xml = ViewOrdersEmptyBinding.inflate(LayoutInflater.from(requireContext()), this.xml.emptyOrderView, true)
        xml.message.text = "Tens 0 encomendas pendentes neste momento"

        if (this.xml.root.nextView == this.xml.root.getChildAt(1)) {
            this.xml.root.showNext()
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::adapter.isInitialized) {
            getOrdersPending()
        }
    }

    class Adapter(private val activity: FragmentActivity): RecyclerView.Adapter<Adapter.ViewHolder>() {

        class ViewHolder(val xml: CardOrderPendingBinding): RecyclerView.ViewHolder(xml.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(CardOrderPendingBinding.inflate(LayoutInflater.from(activity), parent, false))
        }

        override fun getItemCount(): Int = AppData.user.ordersPending.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val order = AppData.user.ordersPending[position]
            if (AppData.user.isProducer()) {
                Glide.with(holder.xml.image).load(Api.getUrl(order.stock.product.image)).into(holder.xml.image)
                holder.xml.entityKey.text = "Revendedor"
                holder.xml.entityValue.text = order.buyer.company.name
            } else {
                Glide.with(holder.xml.image).load(Api.getUrl(order.stock.product.image)).into(holder.xml.image)
                holder.xml.entityKey.text = "Produtor"
                holder.xml.entityValue.text = order.stock.user.company.name
            }

            holder.xml.date.text = order.date
            holder.xml.value.text = order.priceString

            holder.itemView.setOnClickListener {
                activity.startActivityForResult(
                    Intent(activity, ViewOrderActivity::class.java)
                        .putExtra(Constants.ORDER, order.toJson()),
                    RequestCodes.VIEW_ORDER_ACTIVITY)
            }
        }

    }

    companion object {

        fun newInstance() = OrdersPendingFragment()
    }
}
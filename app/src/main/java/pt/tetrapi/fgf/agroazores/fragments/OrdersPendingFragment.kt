package pt.tetrapi.fgf.agroazores.fragments

import android.content.Context
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
import pt.tetrapi.fgf.agroazores.R
import pt.tetrapi.fgf.agroazores.databinding.CardOrderPendingBinding
import pt.tetrapi.fgf.agroazores.databinding.FragmentOrdersPendingBinding
import pt.tetrapi.fgf.agroazores.network.Api

class OrdersPendingFragment : Fragment() {

    private lateinit var xml: FragmentOrdersPendingBinding

    private lateinit var adapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        xml = FragmentOrdersPendingBinding.inflate(inflater, container, false)
        return xml.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getOrdersPending()
    }

    private fun getOrdersPending() {
        CoroutineScope(Dispatchers.Main).launch {
            if (AppData.user.ordersPending.isEmpty()) {
                AppData.user.getOrdersPending()
            }
            adapter = Adapter(requireContext())
            xml.list.adapter = adapter
            xml.list.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    class Adapter(private val context: Context): RecyclerView.Adapter<Adapter.ViewHolder>() {

        class ViewHolder(val xml: CardOrderPendingBinding): RecyclerView.ViewHolder(xml.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(CardOrderPendingBinding.inflate(LayoutInflater.from(context), parent, false))
        }

        override fun getItemCount(): Int = AppData.user.ordersPending.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val order = AppData.user.ordersPending[position]


            if (AppData.user.isProducer()) {
                Glide.with(holder.xml.image).load(Api.getUrl(order.buyer.company.image)).into(holder.xml.image)
                holder.xml.entityKey.text = "Revendedor"
                holder.xml.entityValue.text = order.buyer.company.name
            } else {
                Glide.with(holder.xml.image).load(Api.getUrl(order.stock.user.company.image)).into(holder.xml.image)
                holder.xml.entityKey.text = "Produtor"
                holder.xml.entityValue.text = order.stock.user.company.name
            }

            holder.xml.date.text = order.date
            holder.xml.value.text = order.priceString
        }

    }

    companion object {

        fun newInstance() = OrdersPendingFragment()
    }
}
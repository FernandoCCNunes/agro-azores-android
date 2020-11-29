package pt.tetrapi.fgf.agroazores.interfaces

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.tetrapi.fgf.agroazores.databinding.CardCatalogBuyerBinding
import pt.tetrapi.fgf.agroazores.databinding.CardCatalogSellerBinding
import pt.tetrapi.fgf.agroazores.models.Stock

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

interface CatalogInterface {

    sealed class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        class SellerViewHolder(val xml: CardCatalogSellerBinding): ViewHolder(xml.root) {
            companion object {
                fun getViewHolder(context: Context, parent: ViewGroup): ViewHolder {
                    return SellerViewHolder(
                        CardCatalogSellerBinding.inflate(
                            LayoutInflater.from(context),
                            parent,
                            false
                        )
                    )
                }
            }
        }

        class BuyerViewHolder(val xml: CardCatalogBuyerBinding): ViewHolder(xml.root) {
            companion object {
                fun getViewHolder(context: Context, parent: ViewGroup): ViewHolder {
                    return BuyerViewHolder(
                        CardCatalogBuyerBinding.inflate(
                            LayoutInflater.from(context),
                            parent,
                            false
                        )
                    )
                }
            }
        }

        companion object {
            fun getViewHolder(viewType: Int, context: Context, parent: ViewGroup): ViewHolder {
                return when(viewType) {
                    0 -> SellerViewHolder.getViewHolder(context, parent)
                    else -> BuyerViewHolder.getViewHolder(context, parent)
                }
            }
        }
    }
}
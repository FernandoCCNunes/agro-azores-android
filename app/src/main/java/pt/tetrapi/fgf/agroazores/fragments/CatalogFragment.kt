package pt.tetrapi.fgf.agroazores.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import pt.tetrapi.fgf.agroazores.objects.AppData
import pt.tetrapi.fgf.agroazores.activities.ProductsListActivity
import pt.tetrapi.fgf.agroazores.databinding.FragmentCatalogBinding
import pt.tetrapi.fgf.agroazores.models.Product
import pt.tetrapi.fgf.agroazores.objects.RequestCodes
import tech.hibk.searchablespinnerlibrary.SearchableItem


class CatalogFragment : Fragment() {

    private lateinit var xml: FragmentCatalogBinding

    private lateinit var adapter: ScreenSlidePagerAdapter

    var selectedProduct: Product = AppData.products[0]
        set(value) {
            field = value
            refreshLists()
        }
    var canEditProduct = false
    var selectedFilter: String? = null
        set(value) {
            field = value
            refreshLists()
        }
    var canEditFilter = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        xml = FragmentCatalogBinding.inflate(inflater, container, false)
        return xml.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupListFilters()
        setupNewProductButton()
        setupProductSelector()
    }

    private fun setupListFilters() {
        val filters: List<SearchableItem> = listOf(
            SearchableItem(0.toLong(), "Data"),
            SearchableItem(1.toLong(), "Preço"),
            SearchableItem(2.toLong(), "Pontuação")
        )

        xml.filters.items = filters
        xml.filters.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (canEditFilter) selectedFilter = filters[p2].title
                else canEditFilter = true
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun setupNewProductButton() {
        if (AppData.user.isProducer()) {
            xml.addProductBtn.visibility = View.VISIBLE
            xml.addProductBtn.setOnClickListener {
                startActivityForResult(
                    Intent(requireContext(), ProductsListActivity::class.java),
                    RequestCodes.PRODUCT_LIST_ACTIVITY
                )
            }
        }
    }

    private fun setupProductSelector() {
        if (AppData.user.isRetailer()) {
            xml.selectProductContainer.visibility = View.VISIBLE

            val productsName: List<SearchableItem> = AppData.products.map { SearchableItem(it.id.toLong(),it.name) }
            xml.products.items = productsName
            xml.products.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (canEditProduct) selectedProduct = AppData.products[p2]
                    else canEditProduct = true
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    private fun setupViewPager() {
        adapter = ScreenSlidePagerAdapter(this, requireActivity())
        xml.viewPager.adapter = adapter
        TabLayoutMediator(xml.viewPagerTab, xml.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "À Venda"
                else -> "Em Produção"
            }
        }.attach()
    }

    private fun refreshLists() {
        for (i in 0 until adapter.itemCount) {
            val fragment: Fragment = requireActivity().supportFragmentManager.findFragmentByTag("f$i") ?: continue
            when(fragment) {
                is CatalogForSaleFragment -> fragment.getStockAvailable()
                is CatalogFutureFragment -> fragment.getStockFuture()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            refreshLists()
        }
    }

    private inner class ScreenSlidePagerAdapter(val fragment: CatalogFragment, fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> CatalogForSaleFragment.newInstance().apply { parent = fragment }
                else -> CatalogFutureFragment.newInstance().apply { parent = fragment }
            }
        }

    }

    companion object {
        fun newInstance() = CatalogFragment()
    }
}
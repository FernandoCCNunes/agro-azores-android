package pt.tetrapi.fgf.agroazores.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import pt.tetrapi.fgf.agroazores.R
import pt.tetrapi.fgf.agroazores.databinding.FragmentOrdersBinding
import tech.hibk.searchablespinnerlibrary.SearchableItem

class OrdersFragment : Fragment() {

    private lateinit var xml: FragmentOrdersBinding

    private lateinit var adapter: ViewPagerAdapter

    var selectedFilter: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        xml = FragmentOrdersBinding.inflate(inflater, container, false)
        return xml.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListFilters()
        setupViewPager()
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
                selectedFilter = filters[p2].title
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

    }

    private fun setupViewPager() {
        adapter = ViewPagerAdapter(this, requireActivity())
        xml.viewPager.adapter = adapter
        TabLayoutMediator(xml.viewPagerTab, xml.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "Pendentes"
                else -> "Concluidas"
            }
        }.attach()
    }


    class ViewPagerAdapter(val fragment: OrdersFragment, fa: FragmentActivity): FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> OrdersPendingFragment.newInstance().apply { parent = fragment }
                else -> OrdersConcludedFragment.newInstance().apply { parent = fragment }
            }
        }
    }

    companion object {
        fun newInstance() = OrdersFragment()
    }
}
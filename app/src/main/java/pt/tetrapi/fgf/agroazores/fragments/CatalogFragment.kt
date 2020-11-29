package pt.tetrapi.fgf.agroazores.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.nando.debug.Debug
import pt.tetrapi.fgf.agroazores.AppData
import pt.tetrapi.fgf.agroazores.R
import pt.tetrapi.fgf.agroazores.activities.NewProductActivity
import pt.tetrapi.fgf.agroazores.databinding.FragmentCatalogBinding
import java.io.Serializable

class CatalogFragment : Fragment() {

    private lateinit var xml: FragmentCatalogBinding

    private lateinit var adapter: ViewPagerAdapter

    var stockForSaleRefreshListener: (() -> Unit)? = null
    var stockFutureRefreshListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        xml = FragmentCatalogBinding.inflate(inflater, container, false)
        return xml.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOrderSections()
        setupViewPager()
        setupNewProductButton()
    }

    private fun setupOrderSections() {
        xml.forSale.setOnClickListener {
            xml.forSale.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            xml.inProduction.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_54))
            xml.viewPager.setCurrentItem(0, true)
        }

        xml.inProduction.setOnClickListener {
            xml.inProduction.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            xml.forSale.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_54))
            xml.viewPager.setCurrentItem(1, true)
        }
    }

    private fun setupNewProductButton() {
        if (AppData.user.isProducer()) {
            xml.addProductBtn.visibility = View.VISIBLE
            xml.addProductBtn.setOnClickListener {
                startActivityForResult(Intent(requireContext(), NewProductActivity::class.java), 100)
            }
        }
    }

    private fun setupViewPager() {
        adapter = ViewPagerAdapter(
            this,
            requireActivity().supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
        )
        xml.viewPager.adapter = adapter
    }

    private fun refreshLists() {
        stockForSaleRefreshListener?.invoke()
        stockFutureRefreshListener?.invoke()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            refreshLists()
        }
    }

    class ViewPagerAdapter(val fragment: CatalogFragment, fm: FragmentManager, behavior: Int): FragmentStatePagerAdapter(fm, behavior) {

        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment {
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
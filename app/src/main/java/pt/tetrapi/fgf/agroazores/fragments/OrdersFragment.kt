package pt.tetrapi.fgf.agroazores.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import pt.tetrapi.fgf.agroazores.R
import pt.tetrapi.fgf.agroazores.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

    private lateinit var xml: FragmentOrdersBinding

    private lateinit var adapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        xml = FragmentOrdersBinding.inflate(inflater, container, false)
        return xml.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOrderSections()
        setupViewPager()
    }

    private fun setupOrderSections() {
        xml.pending.setOnClickListener {
            xml.pending.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            xml.completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_54))
            xml.viewPager.setCurrentItem(0, true)
        }

        xml.completed.setOnClickListener {
            xml.completed.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            xml.pending.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_54))
            xml.viewPager.setCurrentItem(1, true)
        }
    }

    private fun setupViewPager() {
        adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        xml.viewPager.adapter = adapter
    }


    class ViewPagerAdapter(fm: FragmentManager, behavior: Int): FragmentStatePagerAdapter(fm, behavior) {

        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> OrdersPendingFragment.newInstance()
                else -> OrdersConcludedFragment.newInstance()
            }
        }

    }

    companion object {
        fun newInstance() = OrdersFragment()
    }
}
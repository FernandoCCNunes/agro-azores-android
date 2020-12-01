package pt.tetrapi.fgf.agroazores.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pt.tetrapi.fgf.agroazores.objects.AppData
import pt.tetrapi.fgf.agroazores.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var xml: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        xml = FragmentProfileBinding.inflate(inflater, container, false)
        return xml.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPersonalInfo()
        setupCompanyInfo()
    }

    private fun setupPersonalInfo() {
        xml.userName.text = "${AppData.user.profile.firstName} ${AppData.user.profile.lastName}"
        xml.userEmaill.text = AppData.user.profile.email
        xml.userContact.text = AppData.user.profile.contact
        xml.userNif.text = AppData.user.profile.nif
    }

    private fun setupCompanyInfo() {
        xml.companyName.text = AppData.user.profile.company.name
        xml.companyEmail.text = AppData.user.profile.company.email
        xml.companyContact.text = AppData.user.profile.company.contact
        xml.companyNif.text = AppData.user.profile.company.nif
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
package nl.svdoetelaar.capstoneproject.ui.main.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import nl.svdoetelaar.capstoneproject.R
import nl.svdoetelaar.capstoneproject.databinding.FragmentOverviewBinding
import nl.svdoetelaar.capstoneproject.viewmodel.UserViewModel

class OverviewFragment : Fragment() {
    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var binding: FragmentOverviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.overview_window_title)



        observe()
    }



    private fun observe() {
        userViewModel.user.observe(viewLifecycleOwner, {
            if (it == null) {
                findNavController().navigate(R.id.action_OverviewFragment_to_UserInfoFragment)
            }
        })
    }
}
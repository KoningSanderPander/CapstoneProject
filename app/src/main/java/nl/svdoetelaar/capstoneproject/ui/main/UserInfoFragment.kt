package nl.svdoetelaar.capstoneproject.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import nl.svdoetelaar.capstoneproject.databinding.FragmentUserInfoBinding
import nl.svdoetelaar.capstoneproject.model.User
import nl.svdoetelaar.capstoneproject.ui.maps.MapsActivity
import nl.svdoetelaar.capstoneproject.util.LocationService
import nl.svdoetelaar.capstoneproject.util.MyApplication
import nl.svdoetelaar.capstoneproject.viewmodel.UserViewModel

class UserInfoFragment : Fragment() {

    private lateinit var binding: FragmentUserInfoBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSetGeofence.setOnClickListener {

            MyApplication.closeKeyboard(requireActivity())

            var isValidInput = true
            val inputFields = arrayOf(
                binding.firstName,
                binding.lastName,
                binding.hourlyWage,
            )

            for (inputField in inputFields) {
                if (inputField.text.toString().isEmpty()) {
                    isValidInput = false
                }
            }


            if (binding.hourlyWage.text.toString()
                    .isDigitsOnly() and binding.hourlyWage.text.toString().isNotEmpty()
            ) {
                if (binding.hourlyWage.text.toString().toDouble() < 0) {
                    isValidInput = false
                }
            }

            if (isValidInput) {
                userViewModel.createUser(
                    User(
                        inputFields[0].text.toString(),
                        inputFields[1].text.toString(),
                        inputFields[2].text.toString().toDouble(),
                    )
                )

                if (!LocationService.hasPermissionFineLocation()) {
                    LocationService.requestFineLocation(requireActivity())
                }
                if (LocationService.hasPermissionFineLocation()) {
                    startActivity(
                        Intent(context, MapsActivity::class.java)
                    )
                }
            } else {
                Snackbar.make(view, "Not all required fields are filled in", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }
}
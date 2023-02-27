package com.example.distancetrackerapp.ui.permission

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.distancetrackerapp.R
import com.example.distancetrackerapp.databinding.FragmentPermissionBinding
import com.example.distancetrackerapp.utils.Permissions
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class PermissionFragment : Fragment(),EasyPermissions.PermissionCallbacks {         // use PermissionCallbacks to override permission denied, permission granted

    private var _binding: FragmentPermissionBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)

        binding.continueButton.setOnClickListener {
         if (Permissions.hasLocationPermissions(requireContext())){
             findNavController().navigate(R.id.action_permissionFragment_to_mapsFragment)
         }
            else {
                Permissions.requestLocationPermissions(this) // this = ref to this fragment
           }
        }

        return binding.root
    }

    // Control + o -> override method
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) { // trigger when user ให้สิทธืบางอย่างกับแอปเรา
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){      // perms  refer to fine location (first permission)
            AppSettingsDialog.Builder(requireActivity()).build().show()
        } else {
            Permissions.requestLocationPermissions(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) { // trigger when user denied our permissions
        findNavController().navigate(R.id.action_permissionFragment_to_mapsFragment)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}
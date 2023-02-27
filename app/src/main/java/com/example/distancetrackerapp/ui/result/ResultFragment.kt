package com.example.distancetrackerapp.ui.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.distancetrackerapp.R
import com.example.distancetrackerapp.databinding.FragmentResultBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ResultFragment : BottomSheetDialogFragment() {

    private val args: ResultFragmentArgs by navArgs() //  ResultFragmentArgs auto generated when we add new arguments to our navigation graph

     private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        // implement logic to get the result which we have passed from maps fragment and fill textview with that same data
        binding.distanceValueTextView.text = getString(R.string.result, args.result.distance) // get the string and concatenate the second parameter
        binding.timeValueTextView.text = args.result.time
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}
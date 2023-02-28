package com.example.distancetrackerapp.ui.result

import android.content.Intent
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

        binding.shareButton.setOnClickListener{
            shareResult()
        }

        return binding.root
    }

    // share data to other apps
    private fun shareResult() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type  = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "I want ${args.result.distance} km in ${args.result.time}!") // specify data which we want to send using putExtra
        }
        startActivity(shareIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}
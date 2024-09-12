package com.nurullahsevinckan.artlib.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.nurullahsevinckan.artlib.R
import com.nurullahsevinckan.artlib.databinding.ArtRowBinding
import com.nurullahsevinckan.artlib.databinding.FragmentArtDetailBinding
import com.squareup.picasso.Picasso


class ArtDetail : Fragment() {
    private lateinit var binding: FragmentArtDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentArtDetailBinding.inflate(layoutInflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            binding.addDescriptionText.text = ArtDetailArgs.fromBundle(bundle).description
            Picasso.get().load(ArtDetailArgs.fromBundle(bundle).url).into(binding.imageView)
        }
    }


}
package com.nurullahsevinckan.artlib.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nurullahsevinckan.artlib.databinding.ArtRowBinding
import com.nurullahsevinckan.artlib.model.Arts
import com.squareup.picasso.Picasso

class MainRecyclerAdapter(val artList : List<Arts>) : RecyclerView.Adapter<PostHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
    val binding = ArtRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return artList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val artDescription : String = "Art name: ${artList.get(position).name}" +
                "\nArt year: ${artList.get(position).year} \n" +
                "Artist:${artList.get(position).artist} \n" +
                "Description: ${artList.get(position).description}"
        holder.binding.recyclerContentText.text = artDescription
        holder.binding.recyclerEmailText.text = artList.get(position).userMail
        Picasso.get().load(artList.get(position).url).into(holder.binding.recyclerImage)
    }


}
package com.nurullahsevinckan.artlib.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nurullahsevinckan.artlib.R
import com.nurullahsevinckan.artlib.adapter.MainRecyclerAdapter
import com.nurullahsevinckan.artlib.databinding.FragmentMainPageBinding
import com.nurullahsevinckan.artlib.model.Arts


class MainPage : Fragment() {
    private var binding : FragmentMainPageBinding? = null
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Arts>
    private lateinit var mainAdapter: MainRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainPageBinding.inflate(layoutInflater,container,false)
        val view = binding!!.root
        return view

    }

    private fun getData(){
        // orderBy("date",Query.Direction.DESCENDING) -> shorted post by posted date (new to old)
        db.collection("Posts").orderBy("date",
            Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(requireActivity(),error.localizedMessage, Toast.LENGTH_LONG).show()
            }else{
                if(value != null && !value.isEmpty){
                    val documents = value.documents

                    postArrayList.clear() // Avoid duplication of posts

                    for(document in documents){

                        //casting
                        val userMail = document.get("userEmail") as String
                        val postDescription = document.get("comment") as String
                        val imageUrl = document.get("downloadedUrl") as String
                        val artYear = document.get("artYear") as String
                        val artistName = document.get("artistName") as String
                        val artName = document.get("artName") as String
                        val post = Arts(artName,artYear,artistName, imageUrl,postDescription,userMail)
                        postArrayList.add(post)
                    }

                    mainAdapter.notifyDataSetChanged() // When something chane it ll notify dataset
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore
        postArrayList = ArrayList<Arts>()
        getData()
        //Connect recycler_row with our xml ui
        binding!!.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        mainAdapter = MainRecyclerAdapter(postArrayList)
        binding!!.recyclerView.adapter = mainAdapter

        // Menü host and provider settings
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.art_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.add_item -> {
                        // Add post
                        val action = MainPageDirections.actionMainPageToAddArt()
                        Navigation.findNavController(view).navigate(action)
                        true
                    }
                    R.id.logout -> {
                        // Log Out
                        auth.signOut()
                        val action = MainPageDirections.actionMainPageToUserLogin()
                        Navigation.findNavController(view).navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}

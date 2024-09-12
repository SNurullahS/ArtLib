package com.nurullahsevinckan.artlib.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import com.nurullahsevinckan.artlib.R
import com.nurullahsevinckan.artlib.databinding.FragmentAddArtBinding
import com.nurullahsevinckan.artlib.databinding.FragmentNewUserBinding


class NewUser : Fragment() {

    private var binding : FragmentNewUserBinding? = null
    private lateinit var auth : FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewUserBinding.inflate(layoutInflater,container,false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding!!.signupButton.setOnClickListener {
           val userName = binding!!.usernameText.text.toString()
           val userMail = binding!!.mailText.text.toString()
           val userPassword = binding!!.passwordText.text.toString()

           if(userMail.isEmpty() || userPassword.isEmpty()) {
               Toast.makeText(view.context,"Enter Information Correctly", Toast.LENGTH_LONG).show()
           }else{
               auth.createUserWithEmailAndPassword(userMail, userPassword).addOnSuccessListener{task ->
                   //success
                   val action = NewUserDirections.actionNewUserToUserLogin()
                   Navigation.findNavController(view).navigate(action)

               }.addOnFailureListener{task ->
                   //fail
                   Toast.makeText(view.context,task.localizedMessage, Toast.LENGTH_LONG).show()
               }

           }
       }
    }


}
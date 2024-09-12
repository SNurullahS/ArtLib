package com.nurullahsevinckan.artlib.view

import android.content.Intent
import android.os.Binder
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
import com.nurullahsevinckan.artlib.databinding.FragmentUserLoginBinding


class UserLogin : Fragment() {
    private lateinit var _binding : FragmentUserLoginBinding
    private val binding get() = _binding
    private lateinit var auth : FirebaseAuth
    private lateinit var userMail : String
    private lateinit var userPassword : String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserLoginBinding.inflate(layoutInflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = auth.currentUser

        //If there is a current user then directly go to main page
        currentUser?.let {
            val action = UserLoginDirections.actionUserLoginToMainPage()
            Navigation.findNavController(view).navigate(action)
        }

        // Clicked action for signinButton
        binding.signinButton.setOnClickListener { view ->
            userMail = binding.mailText.text.toString()
            userPassword = binding.passwordText.text.toString()
            if(userMail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(view.context,"Enter Information Correctly",Toast.LENGTH_LONG).show()
            }else{
                auth.signInWithEmailAndPassword(userMail, userPassword).addOnSuccessListener{task ->
                    //success
                    val action = UserLoginDirections.actionUserLoginToMainPage()
                    Navigation.findNavController(view).navigate(action)

                }.addOnFailureListener{task ->
                    //fail
                    Toast.makeText(view.context,task.localizedMessage,Toast.LENGTH_LONG).show()
                }

            }
        }

        binding.createAccountButton.setOnClickListener { view ->
            val action = UserLoginDirections.actionUserLoginToNewUser()
            Navigation.findNavController(view).navigate(action)

        }
    }




}
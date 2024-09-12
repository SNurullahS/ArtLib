package com.nurullahsevinckan.artlib.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

import com.nurullahsevinckan.artlib.R
import com.nurullahsevinckan.artlib.databinding.FragmentAddArtBinding
import java.util.UUID


class AddArt : Fragment() {
    private  var binding : FragmentAddArtBinding? = null
    private lateinit var activityResultLauncger : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedPicture : Uri? = null
    //For firebase futures
    private lateinit var userAuth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
        //Implementation Of Firebase Components
        userAuth = Firebase.auth
        fireStore = Firebase.firestore
        storage = Firebase.storage

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddArtBinding.inflate(layoutInflater,container,false)
        val view = binding!!.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Publish button clicked
        binding!!.postButtonClick.setOnClickListener { view ->
            //Give every image unique name
            //We use uuid, if we don t use this, every time we save image with same name
            //Its cause override same name pictures
            val uuid = UUID.randomUUID()
            val randomImageName = "$uuid.jpg"

            val reference = storage.reference // Give use the storage main space
            val imageReferance = reference.child("images").child(randomImageName)

            selectedPicture?.let { selectedPicture ->
                imageReferance.putFile(selectedPicture).addOnSuccessListener { uploadTask ->
                    //download url to firestore
                    var uploadedImageReferance = storage.reference.child("images").child(randomImageName) //get the url of the image we uploaded before
                    uploadedImageReferance.downloadUrl.addOnSuccessListener { urlOfImage ->
                        val downloadedUrl = urlOfImage.toString() // We downloaded url, now we store it in post information
                        //First check, is user authenticate or not
                        if(userAuth.currentUser != null){
                            val postInformationMap = hashMapOf<String,Any>()
                            postInformationMap.put("downloadedUrl",downloadedUrl)
                            postInformationMap.put("userEmail",userAuth.currentUser!!.email.toString())
                            postInformationMap.put("artYear",binding!!.addArtYearText.text.toString())
                            postInformationMap.put("artistName",binding!!.addArtistNameText.text.toString())
                            postInformationMap.put("artName",binding!!.addArtNameText.text.toString())
                            postInformationMap.put("comment",binding!!.addDescriptionText.text.toString())
                            postInformationMap.put("date", Timestamp.now())

                            fireStore.collection("Posts").add(postInformationMap).addOnSuccessListener {
                                //Uploading post information success
                                var action = AddArtDirections.actionAddArtToMainPage()
                                Navigation.findNavController(view).navigate(action)

                            }.addOnFailureListener { exception->
                                //Uploading post information failure
                                Toast.makeText(view.context,exception.localizedMessage,Toast.LENGTH_LONG).show()
                            }
                        }

                    }

                }.addOnFailureListener{exception ->
                    //error message
                    Toast.makeText(view.context,exception.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
        }

        //Image button clicked action
        binding!!.imageSelectButtonClick.setOnClickListener { view ->
            if (ContextCompat.checkSelfPermission(view.context,
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)
            {
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission Needed For Gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }.show()
                }else{
                    // request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }

            }else{
                //Intent gallery
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // Start activity for result
                activityResultLauncger.launch(intentToGallery)

            }

        }



    }

    private fun registerLauncher() {
        activityResultLauncger = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){
                var activityResultUri = result.data
                if(activityResultUri != null) {
                    selectedPicture = activityResultUri.data
                    selectedPicture?.let { selectedPicture->
                        //We wont convert this image data to bitmap cause Firebase can store image Uri format
                        binding!!.imageSelectButtonClick.setImageURI(selectedPicture.normalizeScheme())
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->

            if(result){
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncger.launch(intentToGallery)
            }else{
                //permission denied
                Toast.makeText(context,"Permission needed", Toast.LENGTH_LONG).show()
            }

        }


}
}
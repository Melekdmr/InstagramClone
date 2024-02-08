package com.melekdmr.kotlininstagram.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.melekdmr.kotlininstagram.databinding.ActivityUploadBinding
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    var selectedPicture: Uri?=null
    private lateinit var auth:FirebaseAuth
    private lateinit var  firestore:FirebaseFirestore
    private lateinit var storage:FirebaseStorage





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUploadBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        registerLauncher()

        auth=Firebase.auth
        firestore=Firebase.firestore
       storage=Firebase.storage


    }

    fun upload (view: View){

        /* Rastgele bir UUID (Universally Unique Identifier) oluşturuluyor ve
        bu UUID kullanılarak resmin adı belirleniyor.
        Firebase Storage ve Firebase Storage referansı oluşturuluyor. */
        val uuid=UUID.randomUUID()
        val imageName="$uuid.jpg"

        val storage = Firebase.storage
        val reference=storage.reference
        /*"images" klasörü altında bir alt klasör oluşturularak resmin
         referansı alınıyor.Eğer kullanıcı bir resim seçmişse, seçilen
         resim Firebase Storage'a yükleniyor. Yükleme başarılı olduğunda:*/

        val imageReference=reference.child("images").child(imageName)
        if(selectedPicture!=null){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                /* Yüklenen resmin indirme URL'si alınıyor. */
                //dowload URI ->firestore


                val uploadPictureReference=storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl=it.toString()
                    /*Firestore için bir referans oluşturularak, yüklenen
                       resmin URL'si, kullanıcı e-posta adresi, yorum ve tari
                      bilgileri içeren bir harita (map) oluşturuluyor.  */
                    if(auth.currentUser!=null){
                        val postMap= hashMapOf<String, Any>()
                        postMap.put("downloadUrl",downloadUrl)
                        postMap.put("userEmail",auth.currentUser!!.email!!.toString())
                        postMap.put("comment",binding.commentText.text.toString())
                        postMap.put("date",Timestamp.now())

                        println(postMap)

                      /*  Eğer kullanıcı oturum açmışsa, bu bilgiler Firestore "Posts"
                      koleksiyonuna ekleniyor. */
                        firestore.collection("Posts").add(postMap).addOnSuccessListener {
                          /* Eğer ekleme işlemi başarılıysa, aktivite sonlandırılıyor.
                           Aksi takdirde bir hata mesajı gösteriliyor.  */
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                    }



                }.addOnFailureListener {
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                }

            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }


    }

    fun selectImage(view:View){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){



        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    // request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)


                }.show()
            }else{
                //request pemission
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }else{
            val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //start activity for result
            activityResultLauncher.launch(intentToGallery)

        }
    }else{
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                        // request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)


                    }.show()
                }else{
                    //request pemission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //start activity for result
                activityResultLauncher.launch(intentToGallery)

            }
    }}

      private fun registerLauncher(){
          activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
              if(result.resultCode== RESULT_OK){
                  val intentFromResult=result.data
                  if(intentFromResult!= null){
                     selectedPicture= intentFromResult.data
                      selectedPicture?.let {
                          binding.imageView.setImageURI(it)
                      }
                  }
              }

          }


            permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
                if(result){
                    //permission granted

                    val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)


                }else{
                    Toast.makeText(this@UploadActivity,"Permission needed",Toast.LENGTH_LONG).show()
                }

            }
      }
}
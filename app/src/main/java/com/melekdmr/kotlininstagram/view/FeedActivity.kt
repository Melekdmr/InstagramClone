package com.melekdmr.kotlininstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.melekdmr.kotlininstagram.R
import com.melekdmr.kotlininstagram.adapter.FeedRecyclerAdapter
import com.melekdmr.kotlininstagram.databinding.ActivityFeedBinding
import com.melekdmr.kotlininstagram.model.Post
import com.google.firebase.firestore.Query



class FeedActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFeedBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var postArrayList:ArrayList<Post>
    private lateinit var feedAdapter:FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFeedBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        auth=Firebase.auth
        db=Firebase.firestore
        getData()
        postArrayList=ArrayList<Post>()

        /* binding.recyclerView.layoutManager = LinearLayoutManager(this): Bu satırda,
        RecyclerView'ın düzenini belirleyen bir LayoutManager atanır. LinearLayoutManager,
         öğeleri dikey veya yatay bir liste olarak düzenleyen bir düzen yöneticisidir.
          this ifadesi, bu kodun içinde bulunduğu aktiviteyi temsil eder. */
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        /*feedAdapter = FeedRecyclerAdapter(postArrayList): Bu satırda, FeedRecyclerAdapter
        adlı bir özel Adapter sınıfına postArrayList listesini veri kaynağı olarak kullanarak
        bir adaptör örneği oluşturulur. FeedRecyclerAdapter, Post nesnelerini RecyclerView'da
        görüntülemek üzere özelleştirilmiş bir adaptördür. */
        feedAdapter=FeedRecyclerAdapter(postArrayList)
        /*binding.recyclerView.adapter = feedAdapter: Bu satırda, RecyclerView'ın kullanacağı
        adaptörü belirtilir. feedAdapter, önceki adımda oluşturulan FeedRecyclerAdapter örneğidir.
        Bu sayede RecyclerView belirtilen veri kaynağını kullanarak verileri görüntüler. */
        binding.recyclerView.adapter=feedAdapter


    }

    private fun getData(){
        /*
Bu kod, Firestore veritabanındaki "Posts" koleksiyonundan veri çekmeyi amaçlayan
bir işlevi içerir. İşlev, koleksiyondaki belgeleri tarih sırasına göre azalan
(DESCENDING) şekilde sıralar ve ardından bir snapshot dinleyicisi kullanarak
herhangi bir değişiklik olduğunda çalıştırılır. Bu kodun adım adım açıklaması
şu şekildedir:   */

        /* db.collection("Posts"): Firestore veritabanında "Posts" adlı koleksiyona referans alınır. */
        /*addSnapshotListener { value, error ->: Snapshot dinleyicisi eklenir. Bu dinleyici,
         koleksiyondaki belgelerde herhangi bir değişiklik olduğunda tetiklenir.  */
        db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error!=null){

                /*if (error != null) { ... }: Eğer bir hata oluşmuşsa,
                 hatanın bilgisini Toast mesajı ile kullanıcıya gösterir.  */
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                /*else { ... }: Eğer herhangi bir hata oluşmamışsa,
                 koleksiyondan gelen veriyi işler.  */
                /*if (value != null) { ... }: Gelen veri null değilse işlemlere devam eder.  */
                if(value!=null){
                    /*if (!value.isEmpty) { ... }: Gelen veri boş değilse, yani koleksiyon
                     belgeleri varsa işlemlere devam eder. */
                    if(!value.isEmpty){
                        /*val documents = value.documents: Koleksiyondaki belgelerin bir listesi alınır.  */
                        val documents=value.documents

                        /* postArrayList.clear(): Önceki verileri temizler. */
                        postArrayList.clear()

                        /*for (document in documents) { ... }: Belge listesini döngüye alır ve
                        her belge için aşağıdaki işlemleri yapar:  */
                        for (document in documents){
                            //casting
                            /*val comment = document.get("comment") as String: Belgeden
                            "comment" alanını alır ve String'e çevirir.  */
                            val comment=document.get("comment") as String
                            val userEmail=document.get("userEmail") as String

                            /*val downloadUrl = document.get("downloadUrl") as String: Belgeden
                             "downloadUrl" alanını alır ve String'e çevirir.  */
                            val downloadUrl=document.get("downloadUrl") as String

                             /*val post = Post(userEmail, comment, downloadUrl):
                             Yukarıdaki bilgileri kullanarak bir Post nesnesi oluşturur.  */
                            val post=Post(userEmail,comment,downloadUrl)

                            /* postArrayList.add(post): Oluşturulan Post nesnesini postArrayList
                            listesine ekler. */
                            postArrayList.add(post)


                        }
                        /*feedAdapter!!.notifyDataSetChanged(): Adapter'a değişiklikleri bildirerek
                        verilerin güncellenmesini sağlar. (feedAdapter null olmadığı varsayılarak !!
                        kullanılmıştır.) */
                        feedAdapter!! .notifyDataSetChanged()

                        /*Bu işlev, Firestore koleksiyonundaki verileri alarak bunları Post nesnelerine
                         dönüştürür ve bu nesneleri postArrayList listesine ekleyerek, ardından feedAdapter
                         üzerinden arayüzü günceller.





  */


                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.insta_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== R.id.add_post){
            val intent= Intent(this, UploadActivity::class.java)
            startActivity(intent)

        }else if(item.itemId== R.id.signout){
            auth.signOut()
            val intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        return super.onOptionsItemSelected(item)
    }
}
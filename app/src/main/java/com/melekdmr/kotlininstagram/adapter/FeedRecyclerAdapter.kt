package com.melekdmr.kotlininstagram.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.melekdmr.kotlininstagram.databinding.ActivityUploadBinding
import com.melekdmr.kotlininstagram.databinding.RecyclerRowBinding
import com.melekdmr.kotlininstagram.model.Post
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(private val postList:ArrayList<Post>) :RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>(){

    class PostHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }


    /*override fun onBindViewHolder(holder: PostHolder, position: Int) {: Bu fonksiyon,
    RecyclerView'ın belirli bir pozisyonundaki öğeyi bağlamak için kullanılır.
     PostHolder, bir önceki soruda bahsettiğimiz gibi, RecyclerView.ViewHolder
     sınıfından türetilmiş özel bir sınıftır ve bu sınıfın örneğini taşır.
      position, bağlanacak verinin konumunu temsil eder.   */
    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        /*holder.binding.recyclerEmailText.text = postList[position].email: Bu satırda,
         PostHolder sınıfının içinde bulunan binding özelliği üzerinden recyclerEmailText
         adlı bir TextView'e, postList isimli bir listenin belirli bir pozisyonundaki
         email özelliği atanır. Bu, kullanıcının e-posta adresini RecyclerView'ın belirli
         bir öğesinde görüntülemek için kullanılır.  */
       holder.binding.recyclerEmailText.text=postList.get(position).email
        holder.binding.recyclerCommentText.text=postList.get(position).comment
        /*Picasso.get().load(postList[position].downloadUrl).into(holder.binding.recyclerImageView):
        Bu satırda, Picasso kütüphanesi kullanılarak belirli bir URL'den gelen resim, PostHolder
        sınıfının içinde bulunan binding özelliği üzerinden recyclerImageView adlı bir ImageView'e
        yüklenir. Bu, kullanıcının paylaştığı medyayı (resim veya video) RecyclerView'ın belirli
        bir öğesinde görüntülemek için kullanılır.  */
        Picasso.get().load(postList[position].downloadUrl).into(holder.binding.recyclerImageView)


    }
    override fun getItemCount(): Int {
          return postList.size
    }
}
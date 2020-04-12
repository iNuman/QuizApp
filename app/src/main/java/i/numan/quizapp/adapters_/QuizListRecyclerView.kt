package i.numan.quizapp.adapters_

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import i.numan.quizapp.R
import i.numan.quizapp.db_.entity_.QuizListModel
import kotlinx.android.synthetic.main.single_list_item.view.*

class QuizListRecyclerView(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<QuizListRecyclerView.QuizListViewHolder>() {

    // since Diff Util is working on main thread causes some issue while dealing with large amount of data
    // so to avoid this we'll use AsyncListDiffer
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<QuizListModel>() {

        override fun areItemsTheSame(oldItem: QuizListModel, newItem: QuizListModel): Boolean {
            TODO("not implemented")
        }

        override fun areContentsTheSame(oldItem: QuizListModel, newItem: QuizListModel): Boolean {
            TODO("not implemented")
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizListViewHolder {
        return QuizListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.single_list_item, parent, false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: QuizListViewHolder, position: Int) {
        when (holder) {
            is QuizListViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<QuizListModel>) {
        differ.submitList(list)
    }


    class QuizListViewHolder constructor(
        itemView: View,
        private var interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: QuizListModel?) = with(itemView) {

            item!!.let {

                list_title.text = item.name
                Glide.with(context)
                    .load(item.image)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .into(list_image)
                list_difficulty.text = item.level

                var listDescription = item.desc
                if(listDescription.length > 150)
                    listDescription = listDescription.substring(0,150)
                    list_desc.text = "$listDescription..."


            }

            list_btn.setOnClickListener {
                /*
                * it'll get the position of the clicked item with item
                 */
                interaction!!.onItemSelected(adapterPosition, item)
            }

        }

    }

    interface Interaction {
        fun onItemSelected(position: Int, item: QuizListModel?)
    }


}
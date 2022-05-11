package ru.skillbranch.sbdelivery.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.ItemDishCustomBinding
import ru.skillbranch.sbdelivery.ui.categories.DishesAdapter.Holder
import ru.storozh.models.delivery.database.domains.DishV

class DishesAdapter(
    private val onClickListener: (DishV) -> Unit,
    private val onClickListenerAdd: (DishV) -> Unit,
    private val onClickListenerLike: (DishV) -> Unit
) : ListAdapter<DishV, Holder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        ItemDishCustomBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        parent.findViewTreeLifecycleOwner()
    )

    override fun onBindViewHolder(holder: Holder, pos: Int) =
        holder.bind(getItem(pos), onClickListener, onClickListenerAdd, onClickListenerLike)

    class Holder(private val vb: ItemDishCustomBinding, private val lo: LifecycleOwner?) :
        RecyclerView.ViewHolder(vb.root) {

        fun bind(
            item: DishV?,
            onClickListener: (DishV) -> Unit,
            onClickListenerAdd: (DishV) -> Unit,
            onClickListenerLike: (DishV) -> Unit
        ) {
            item?.let {
                vb.root.setId(item.id)
                vb.root.setPrice(item.price.toString())
                vb.root.setName(item.name)
                vb.root.setLike(item.favorite)
                vb.root.addOnClickListener { onClickListener.invoke(item) }
                vb.root.addOnClickListenerForAddButton { onClickListenerAdd.invoke(item) }
                vb.root.addOnClickListenerForLikeButton { onClickListenerLike.invoke(item) }

                Glide
                    .with(vb.root)
                    .load(item.image)
                    .placeholder(R.drawable.ic_menu_slideshow)
                    .error(R.drawable.ic_empty_dish)
                    .into(vb.root.getPhoto())

                lo?.let {
                    vb.root.registerLifecycle(lo.lifecycle)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DishV>() {
            override fun areItemsTheSame(o: DishV, n: DishV) = o.id == n.id
            override fun areContentsTheSame(o: DishV, n: DishV) = o == n
        }
    }
}

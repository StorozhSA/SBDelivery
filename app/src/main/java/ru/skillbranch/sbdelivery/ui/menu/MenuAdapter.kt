package ru.skillbranch.sbdelivery.ui.menu

import android.graphics.drawable.PictureDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.databinding.ItemCategoryBinding
import ru.skillbranch.sbdelivery.ui.menu.MenuAdapter.Holder
import ru.skillbranch.sbdelivery.utils.svg.SvgSoftwareLayerSetter
import ru.storozh.models.delivery.database.domains.CategoryV

class MenuAdapter(private val listener: (CategoryV) -> Unit) :
    ListAdapter<CategoryV, Holder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, pos: Int) = holder.bind(getItem(pos), listener)

    class Holder(private val vb: ItemCategoryBinding) : RecyclerView.ViewHolder(vb.root) {
        fun bind(item: CategoryV, listener: (CategoryV) -> Unit) {
            vb.menuTvCategory.text = item.name

            if (item.id == "1") {
                Glide
                    .with(vb.root)
                    .load(R.drawable.ic_action_dishes)
                    .into(vb.menuIvCategory)
            } else {
                val requestBuilder = Glide
                    .with(vb.root)
                    .`as`(PictureDrawable::class.java)
                    .listener(SvgSoftwareLayerSetter())
                requestBuilder
                    .load(item.icon)
                    .into(vb.menuIvCategory)
            }
            itemView.setOnClickListener { listener(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryV>() {
        override fun areItemsTheSame(o: CategoryV, n: CategoryV) = (o.id == n.id)
        override fun areContentsTheSame(o: CategoryV, n: CategoryV) = (o == n)
    }
}

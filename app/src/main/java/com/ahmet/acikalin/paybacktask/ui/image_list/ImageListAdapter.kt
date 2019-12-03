package com.ahmet.acikalin.paybacktask.ui.image_list

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ahmet.acikalin.paybacktask.R
import com.ahmet.acikalin.paybacktask.data.model.api.Hit
import com.ahmet.acikalin.paybacktask.data.remote.NetworkState
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_list_item_layout.view.*
import kotlinx.android.synthetic.main.network_state_item.view.*


class ImageListAdapter(private val context: Context) :
    PagedListAdapter<Hit, RecyclerView.ViewHolder>(
        HitDiffCallback()
    ) {

    private val HIT_VIEW_TYPE = 1
    private val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        return if (viewType == HIT_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.image_list_item_layout, parent, false)
            HitItemViewHolder(
                view
            )
        } else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            NetworkStateItemViewHolder(
                view
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == HIT_VIEW_TYPE) {
            (holder as HitItemViewHolder).bind(getItem(position), context, listener)
        } else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    private var listener: View.OnClickListener? = null
    fun setTagListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    private lateinit var bitmap: Bitmap
    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            HIT_VIEW_TYPE
        }
    }


    class HitDiffCallback : DiffUtil.ItemCallback<Hit>() {
        override fun areItemsTheSame(oldItem: Hit, newItem: Hit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Hit, newItem: Hit): Boolean {
            return oldItem == newItem
        }

    }


    class HitItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(hit: Hit?, context: Context, listener: View.OnClickListener?) {
            itemView.username.text = hit!!.user
            itemView.username.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_account_circle_black_24dp,
                0,
                0,
                0
            )
            itemView.username.compoundDrawablePadding = 12

            Picasso.get()
                .load(hit.largeImageURL)
                .fit()
                .placeholder(R.drawable.ic_image_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(itemView.image_detail)

            val tagsList: List<String> = hit.tags!!.split(", ")

            itemView.chip_group.removeAllViews()
            for (tag in tagsList) {
                val chip = Chip(itemView.chip_group.context)
                chip.text = tag
                // necessary to get single selection working
                chip.isClickable = true
                chip.chipIcon = ContextCompat.getDrawable(context,R.drawable.ic_tag_24dp)
                chip.isCheckable = false
                itemView.chip_group.addView(chip)
                chip.setOnClickListener(listener)
            }

            with(itemView.image_detail) {
                setOnClickListener {
                    basicAlert(context,it, hit)
                }
            }


        }

        private fun basicAlert(context: Context, view: View, hit: Hit) {

            val builder = AlertDialog.Builder(context, R.style.myDialog)

            with(builder) {
                setTitle(context.getString(R.string.alert))
                setMessage(context.getString(R.string.do_you_want_more_details))
                setPositiveButton(android.R.string.yes) { _, _ ->
                    val bundle = bundleOf("hit" to Gson().toJson(hit))
                    val navController: NavController = Navigation.findNavController(view)
                    navController.navigate(
                        R.id.action_searchResultListFragment_to_imageDetailFragment,
                        bundle
                    )
                }
                setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.cancel()
                }

            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    class NetworkStateItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                itemView.progress_bar_item.visibility = View.VISIBLE
            } else {
                itemView.progress_bar_item.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                itemView.error_msg_item.visibility = View.VISIBLE
                itemView.error_msg_item.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.ENDOFLIST) {
                itemView.error_msg_item.visibility = View.VISIBLE
                itemView.error_msg_item.text = networkState.msg
            } else {
                itemView.error_msg_item.visibility = View.GONE
            }
        }
    }


    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {                             //hadExtraRow is true and hasExtraRow false
                notifyItemRemoved(super.getItemCount())    //remove the progressbar at the end
            } else {                                       //hasExtraRow is true and hadExtraRow false
                notifyItemInserted(super.getItemCount())   //add the progressbar at the end
            }
        } else if (hasExtraRow && previousState != newNetworkState) { //hasExtraRow is true and hadExtraRow true and (NetworkState.ERROR or NetworkState.ENDOFLIST)
            notifyItemChanged(itemCount - 1)       //add the network message at the end
        }

    }
}
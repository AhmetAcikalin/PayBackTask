package com.ahmet.acikalin.paybacktask.ui.image_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.ahmet.acikalin.paybacktask.R
import com.ahmet.acikalin.paybacktask.data.model.api.Hit
import com.ahmet.acikalin.paybacktask.databinding.ImageDetailFragmentBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.gson.Gson
import com.squareup.picasso.Picasso


class ImageDetailFragment : Fragment() {

    companion object {
        fun newInstance() =
            ImageDetailFragment()
    }

    private lateinit var imageDetail: Hit
    private lateinit var viewModel: ImageDetailViewModel
    private lateinit var binding: ImageDetailFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageDetail = Gson().fromJson(arguments!!.getString("hit"), Hit::class.java)!!
        viewModel = ViewModelProviders.of(this).get(ImageDetailViewModel::class.java)
        viewModel.hit = imageDetail
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.image_detail_fragment, container, false
        )

        binding.setVariable(BR.hit, imageDetail)
        binding.executePendingBindings()

        setIcons()
        createTagChips()

        Picasso.get()
            .load(imageDetail.largeImageURL)
            .placeholder(R.drawable.ic_image_black_24dp)
            .error(R.drawable.ic_broken_image_black_24dp)
            .fit()
            .into(binding.detailImage)

        /*  viewModel.downloadImage(imageDetail.largeImageURL!!).observe(this, Observer {
            binding.detailImage.setImageBitmap(it)

        })*/
        return binding.root
    }

    private fun setIcons() {
        setLeftDrawable(binding.detailUsername, R.drawable.ic_account_circle_black_24dp)
        setLeftDrawable(binding.detailLikes, R.drawable.ic_thumb_up_black_24dp)
        setLeftDrawable(binding.detailComments, R.drawable.ic_mode_comment_black_24dp)
        setLeftDrawable(binding.detailFavs, R.drawable.ic_star_black_24dp)
    }

    private fun setLeftDrawable(tv: TextView, drawableId: Int) {
        tv.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0)
    }

    private fun createTagChips() {
        val tagsList: List<String> = imageDetail.tags!!.split(", ")
        for (tag in tagsList) {
            val chip = Chip(binding.detailTagsGroup.context)
            val drawable =
                ChipDrawable.createFromAttributes(
                    context,
                    null,
                    0,
                    R.style.Widget_MaterialComponents_Chip_Action
                )
            chip.setChipDrawable(drawable)
            chip.isClickable = false
            chip.chipIcon = ContextCompat.getDrawable(context!!,R.drawable.ic_tag_24dp)
            chip.isCheckable = false
            chip.text = tag
            binding.detailTagsGroup.addView(chip)
        }
    }


}

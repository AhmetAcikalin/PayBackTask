package com.ahmet.acikalin.paybacktask.ui.image_list

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmet.acikalin.paybacktask.R
import com.ahmet.acikalin.paybacktask.data.remote.NetworkState
import com.ahmet.acikalin.paybacktask.databinding.ImageListFragmentBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable


class ImageListFragment : Fragment() {

    companion object {
        fun newInstance() =
            ImageListFragment()

    }

    private lateinit var viewModel: ImageListViewModel
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var listAdapter: ImageListAdapter
    private lateinit var binding: ImageListFragmentBinding
    private var layoutManagerOption: Int = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.image_list_fragment, container, false
        )
        viewModel = ViewModelProviders.of(this).get(ImageListViewModel::class.java)
        when {
            savedInstanceState != null -> {
                layoutManagerOption = savedInstanceState.getInt("layout_manager")
            }
        }
        initRecyclerList()
        loadTagChips()
        Log.e("SS",viewModel.searchTxtVal)
        Log.e("array",viewModel.searchArray.toString())
        startObserve()
        swipeRefreshListener()
        searchAddClickListener()
        removeAllClickListener()
        binding.openPopup.setOnClickListener(showMenuPopup())

        return binding.root
    }

    private fun doVisibilityClearAllBtn(){
        binding.clearAll.visibility = when {
            viewModel.searchTxtVal.isEmpty() -> {
                View.GONE
            }
            else -> { View.VISIBLE}
        }
    }
    private fun initRecyclerList() {
        listAdapter =
            ImageListAdapter(
                this.context!!
            )
        listAdapter.setTagListener(View.OnClickListener {
            val chip: Chip = it as Chip
            createTagChips(chip.text.toString())
            addSearch(chip.text.toString())
        })
        loadLayoutManager()
        binding.resultRecyclerList.layoutManager = layoutManager
        binding.resultRecyclerList.adapter = listAdapter

    }
    private fun startObserve() {
        viewModel.hitPagedList.observe(this, Observer {
            listAdapter.submitList(it)

        })
        observeNetworkState()
    }

    private fun search() {
        viewModel.searchImage(this)
        startObserve()
        doVisibilityClearAllBtn()
    }

    private fun observeNetworkState() {
        viewModel.networkState.observe(this, Observer {
            binding.progressBar.visibility =
                if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.txtError.visibility =
                if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) {
                listAdapter.setNetworkState(it)
            }
        })
    }

    private fun searchAddClickListener() {
        binding.addSearchTag.setOnClickListener {
            createTagChips(binding.searchBar.text.toString())
            addSearch(binding.searchBar.text.toString())
            binding.searchBar.text.clear()
        }
    }

    private fun loadTagChips() {
        for (i in 0 until viewModel.searchArray.size) {
            createTagChips(viewModel.searchArray[i])
        }
        doVisibilityClearAllBtn()
    }

    private fun createTagChips(tag: String) {
        val chip = Chip(binding.searchChipGroup.context)
        val drawable =
            ChipDrawable.createFromAttributes(context, null, 0, R.style.CustomChipEntry)
        chip.setChipDrawable(drawable)
        // necessary to get single selection working
        chip.isClickable = true
        chip.chipIcon = ContextCompat.getDrawable(context!!,R.drawable.ic_tag_24dp)
        chip.isCheckable = false
        chip.text = tag

        binding.searchChipGroup.addView(chip, 0)
        chip.setOnCloseIconClickListener {
            val index = binding.searchChipGroup.indexOfChild(chip)
            binding.searchChipGroup.removeView(chip)
            removeSearch(index)
        }
    }

    private fun addSearch(txt: String) {
        viewModel.searchArray.add(0, txt)
        search()

    }

    private fun removeSearch(index: Int) {
        viewModel.searchArray.removeAt(index)
        search()
    }

    private fun swipeRefreshListener() {
        binding.swipeRefresh.setOnRefreshListener {
            search()
            binding.swipeRefresh.isRefreshing = false
        }
    }
    private fun removeAllClickListener() {
        binding.clearAll.setOnClickListener {
            binding.searchChipGroup.removeAllViews()
            viewModel.searchArray.clear()
            search()
        }
    }

    private fun showMenuPopup(): View.OnClickListener {
        return View.OnClickListener {
            val popupMenu = PopupMenu(context!!, it)
            addItems(popupMenu)
            popupMenu.setOnMenuItemClickListener { item ->

                layoutManagerOption=item.itemId
                loadLayoutManager()
                binding.resultRecyclerList.layoutManager = layoutManager
                true
            }
            popupMenu.show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("layout_manager", layoutManagerOption)
        super.onSaveInstanceState(outState)
    }
    private fun loadLayoutManager(){
        when (layoutManagerOption) {
            1 ->
                layoutManager = LinearLayoutManager(context)
            2 ->
                layoutManager = GridLayoutManager(context, 2)
            3 ->
                layoutManager = GridLayoutManager(context, 3)
        }
    }
    private fun menuIconWithText(
        r: Drawable,
        title: String
    ): CharSequence? {
        r.setBounds(0, 0, r.intrinsicWidth, r.intrinsicHeight)
        val sb = SpannableString("    $title")
        val imageSpan = ImageSpan(r, ImageSpan.ALIGN_BOTTOM)
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }

    private fun addItems(popup: PopupMenu): PopupMenu {
        popup.menu.add(0, 3, 3, menuIconWithText(ContextCompat.getDrawable(context!!,R.drawable.ic_list_24dp)!!, resources.getString(R.string.grid_view_3)))
        popup.menu.add(0, 2, 2, menuIconWithText(ContextCompat.getDrawable(context!!,R.drawable.rotated_grid)!!, resources.getString(R.string.grid_view_2)))
        popup.menu.add(0, 1, 1, menuIconWithText(ContextCompat.getDrawable(context!!,R.drawable.rotated_list)!!, resources.getString(R.string.list_view)))
        return popup
    }
}

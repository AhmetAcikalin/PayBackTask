package com.ahmet.acikalin.paybacktask.ui.image_list

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.ahmet.acikalin.paybacktask.data.model.api.Hit
import com.ahmet.acikalin.paybacktask.data.remote.ApiRequest
import com.ahmet.acikalin.paybacktask.data.remote.HitPagedListRepository
import com.ahmet.acikalin.paybacktask.data.remote.NetworkState
import com.ahmet.acikalin.paybacktask.data.remote.RetrofitRequest
import io.reactivex.disposables.CompositeDisposable


class ImageListViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val apiRequest: ApiRequest = RetrofitRequest.getClient()
    private var hitRepository: HitPagedListRepository = HitPagedListRepository(apiRequest)
    var searchTxtVal: String = "fruits"
    var searchArray: ArrayList<String> = ArrayList(listOf(searchTxtVal))

    var hitPagedList: LiveData<PagedList<Hit>> =
        hitRepository.fetchLiveHitPagedList(searchTxtVal, compositeDisposable)

    val networkState: LiveData<NetworkState> by lazy {
        hitRepository.getNetworkState()
    }


    fun listIsEmpty(): Boolean {
        return hitPagedList.value?.isEmpty() ?: true
    }

    fun searchImage(
        lifecycleOwner: LifecycleOwner
    ) {
        hitPagedList.removeObservers(lifecycleOwner)
        convertToSearchTxt()
        hitPagedList = hitRepository.fetchLiveHitPagedList(searchTxtVal, compositeDisposable)

    }

    fun refresh() {
        hitPagedList.value!!.dataSource.invalidate()
    }

    private fun convertToSearchTxt() {
        for (i in 0 until searchArray.size) {
            if (i == 0)
                searchTxtVal = searchArray[i]
            else
                searchTxtVal += "+" + searchArray[i]
        }
        if (searchArray.size <= 0) {
            searchTxtVal = ""
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}

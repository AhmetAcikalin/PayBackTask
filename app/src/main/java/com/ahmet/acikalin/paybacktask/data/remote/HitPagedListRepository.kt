package com.ahmet.acikalin.paybacktask.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ahmet.acikalin.paybacktask.data.model.api.Hit
import io.reactivex.disposables.CompositeDisposable

class HitPagedListRepository(private val apiService: ApiRequest) {

    private lateinit var hitPagedList: LiveData<PagedList<Hit>>
    private lateinit var hitDataSourceFactory: HitDataSourceFactory

    fun fetchLiveHitPagedList(
        searchTxt: String,
        compositeDisposable: CompositeDisposable
    ): LiveData<PagedList<Hit>> {
        hitDataSourceFactory = HitDataSourceFactory(searchTxt, apiService, compositeDisposable)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        hitPagedList = LivePagedListBuilder(hitDataSourceFactory, config).build()

        return hitPagedList
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<HitDataSource, NetworkState>(
            hitDataSourceFactory.hitsLiveDataSource, HitDataSource::networkState
        )
    }
}
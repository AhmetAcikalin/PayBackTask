package com.ahmet.acikalin.paybacktask.data.remote

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.ahmet.acikalin.paybacktask.data.model.api.Hit
import io.reactivex.disposables.CompositeDisposable

class HitDataSourceFactory(
    private var searchTxt: String,
    private val apiService: ApiRequest,
    private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, Hit>() {

    val hitsLiveDataSource = MutableLiveData<HitDataSource>()

    override fun create(): DataSource<Int, Hit> {
        val hitDataSource = HitDataSource(searchTxt, apiService, compositeDisposable)

        hitsLiveDataSource.postValue(hitDataSource)
        return hitDataSource
    }
}
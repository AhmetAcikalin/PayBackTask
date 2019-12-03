package com.ahmet.acikalin.paybacktask.data.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.ahmet.acikalin.paybacktask.data.model.api.Hit
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HitDataSource(
    private var searchTxt: String,
    private val apiService: ApiRequest,
    private val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, Hit>() {

    private var page = FIRST_PAGE

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Hit>
    ) {

        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            apiService.search(searchTxt, 1)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        callback.onResult(it.hits, null, page + 1)
                        networkState.postValue(NetworkState.LOADED)
                        Log.e("initial",searchTxt)
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                        it.printStackTrace()
                    }
                )
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Hit>) {

        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            apiService.search(searchTxt, params.key)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        if (it.total.div(20).toDouble() >= params.key) {
                            callback.onResult(it.hits, params.key + 1)
                            networkState.postValue(NetworkState.LOADED)
                            Log.e("loadAfter",searchTxt)
                        } else {
                            networkState.postValue(NetworkState.ENDOFLIST)
                        }
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                        it.printStackTrace()
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Hit>) {

    }

    private val TAG = HitDataSource::class.java.simpleName

}



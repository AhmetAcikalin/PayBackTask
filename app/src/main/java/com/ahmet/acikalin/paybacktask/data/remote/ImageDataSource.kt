package com.ahmet.acikalin.paybacktask.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ImageDataSource(
    private val apiService: ApiRequest,
    private val compositeDisposable: CompositeDisposable
) {


    private val networkState: MutableLiveData<NetworkState> = MutableLiveData()
    private val image: MutableLiveData<Bitmap> = MutableLiveData()


    fun loadInitial(url: String): MutableLiveData<Bitmap> {
        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            apiService.getImage(url)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        val bmp: Bitmap = BitmapFactory.decodeStream(it.byteStream())
                        image.postValue(bmp)
                        networkState.postValue(NetworkState.LOADED)
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                        it.printStackTrace()
                    }
                )
        )
        return image
    }

    private val TAG = HitDataSource::class.java.simpleName

}

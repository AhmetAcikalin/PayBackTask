package com.ahmet.acikalin.paybacktask.ui.image_detail

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ahmet.acikalin.paybacktask.data.model.api.Hit
import com.ahmet.acikalin.paybacktask.data.remote.ApiRequest
import com.ahmet.acikalin.paybacktask.data.remote.ImageDataSource
import com.ahmet.acikalin.paybacktask.data.remote.RetrofitRequest
import io.reactivex.disposables.CompositeDisposable

class ImageDetailViewModel : ViewModel() {
    lateinit var hit: Hit

    private val compositeDisposable = CompositeDisposable()
    private val apiRequest: ApiRequest = RetrofitRequest.getClient()
    private val imageDataSource: ImageDataSource = ImageDataSource(apiRequest, compositeDisposable)
    fun downloadImage(url: String): MutableLiveData<Bitmap> {

        return imageDataSource.loadInitial(url)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}

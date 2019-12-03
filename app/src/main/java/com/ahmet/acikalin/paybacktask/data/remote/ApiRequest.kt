package com.ahmet.acikalin.paybacktask.data.remote

import com.ahmet.acikalin.paybacktask.data.model.api.SearchResult
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 *
 * @author ahmet
 * @since 10/9/19
 *
 */
interface ApiRequest {

    @GET("api/")
    fun search(
        @Query("q") search: String?,
        @Query("page") page: Int?
    ): Observable<SearchResult>

    @GET
    fun getImage(@Url url: String): Observable<ResponseBody>
}
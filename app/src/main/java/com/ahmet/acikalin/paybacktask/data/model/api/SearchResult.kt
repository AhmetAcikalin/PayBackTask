package com.ahmet.acikalin.paybacktask.data.model.api


import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("hits")
    var hits: List<Hit>,
    @SerializedName("total")
    var total: Int,
    @SerializedName("totalHits")
    var totalHits: Int
)
package com.github.jing332.text_searcher.ui.search

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SearchSourceState(initialLoad: Boolean = true) : Parcelable {
    var requestLoad by mutableStateOf(initialLoad)

    constructor(parcel: Parcel) : this(true) {
        requestLoad = parcel.readInt() == 1
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(if (requestLoad) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchSourceState> {
        override fun createFromParcel(parcel: Parcel): SearchSourceState {
            return SearchSourceState(parcel)
        }

        override fun newArray(size: Int): Array<SearchSourceState?> {
            return arrayOfNulls(size)
        }
    }
}
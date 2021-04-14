package com.example.huskeliste1.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Items(val item:String, var isChecked:Boolean = false):Parcelable
package com.example.musicplayer

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//Add Annotation and plugin in build.gradle
/*
By adding the @Parcelize annotation to a Kotlin data class or a class that meets certain criteria,
 you can instruct the compiler to generate the necessary Parcelable implementation code.
 This eliminates the need for you to manually write the often boilerplate code required for Parcelable.
 */
@Parcelize
data class MusicData(
    val songUri : Uri?,
    val songName : String?,
    val songArtist : String?,
) :Parcelable

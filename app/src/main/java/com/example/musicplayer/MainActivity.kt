package com.example.musicplayer

import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
//This line declares a mutable list of MusicData objects. MusicData is likely a data class representing
    // information about a music track, such as its title, artist, and URI.
    private val musicPlayerList: MutableList<MusicData> = arrayListOf()

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       //This line declares a variable adapter of type MusicAdapter, which seems to be a
        // custom adapter for populating a RecyclerView with music data.
        adapter = MusicAdapter(arrayListOf()) {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("song", it)
            startActivity(intent)
        }
        binding.rvAudioList.layoutManager = LinearLayoutManager(this)
        binding.rvAudioList.adapter = adapter

        //checking whether certain permissions are granted (checkPermissions).
        // If permissions are granted, you call getSampleAudioUri().
        // Otherwise, you request the necessary permissions (requestPermissions).
        if (checkPermissions()) {
            getSampleAudioUri()
        } else {
            requestPermissions()
        }


    }

    private fun checkPermissions(): Boolean {
        //This is a common pattern as Android has introduced changes to how storage permissions are handled in more recent
        //equal to or greater than 33
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),
                PERMISSION_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    //It is called when the user responds to a permission request dialog.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            //The if condition checks whether the requestCode matches
            // the code used when requesting permissions (PERMISSION_REQUEST_CODE).
            getSampleAudioUri()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSampleAudioUri() {
        // Define the columns to be queried from the audio content provider
        //Defines an array of column names to be queried from the audio content provider.
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
        )

         // Query the audio content provider using a ContentResolver
//Uses a ContentResolver to query the MediaStore.Audio.Media.EXTERNAL_CONTENT_URI with the specified projection.
// The result is a Cursor object containing the queried data
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        // // Use the cursor in a "use" block to ensure it is closed properly
        cursor?.use {
            //  // Move the cursor to the first row
            it.moveToFirst()
             // Iterate through the cursor to process each audio file
            do {
                //          // Retrieve the ID of the audio file
                val idIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val id = cursor.getLong(idIndex)
                //Create a content URI for the audio file using its ID
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                // Retrieve the title of the audio file
                val titleColumnIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val title = cursor.getString(titleColumnIndex)

                //
                // Retrieve the artist of the audio file
                val artistColumnIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val artist = cursor.getString(artistColumnIndex)

                // Create a MusicData object representing the audio file
                val musicData = MusicData(contentUri, title, artist)
                Log.d("Song Name", "${musicData.songName}")
                Log.d("Song Uri", "${musicData.songUri}")
                Log.d("Song Artist", "${musicData.songArtist}")

                // Add the MusicData object to the musicPlayerList
                musicPlayerList.add(musicData)

            } while (it.moveToNext())
        }
        // Update the adapter's data list with the new musicPlayerList
        adapter.updateList(musicPlayerList)

    }


    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}

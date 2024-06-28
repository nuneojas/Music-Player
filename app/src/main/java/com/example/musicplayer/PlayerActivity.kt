package com.example.musicplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.example.musicplayer.databinding.ActivityPlayerBinding
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var musicData: MusicData? = null
    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("song")) {
            musicData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("song", MusicData::class.java)
            } else {
                intent.getParcelableExtra("song")
            }
        }

        binding.tvTitle.text = musicData?.songName
        binding.tvArtist.text = musicData?.songArtist
        initializeMediaPlayer(musicData?.songUri)

        setListeners()

        handler = Handler()
        updateSeekBarAndTextViews()
    }

    private fun initializeMediaPlayer(uri: Uri?) {
        mediaPlayer = MediaPlayer()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer?.setAudioAttributes(audioAttributes)

        if (uri != null) {
            mediaPlayer?.setDataSource(this, uri)
            mediaPlayer?.prepare()
        }
    }


    private fun setListeners() {
        binding.btnPlayPause.setOnClickListener {
            togglePlayback()
        }

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        mediaPlayer?.setOnCompletionListener {
            if (!it.isPlaying) {
                updateIcon(false)
            }
        }
    }

    private fun updateSeekBarAndTextViews() {
        handler?.postDelayed(object : Runnable {
            override fun run() {
                val currentPosition = mediaPlayer?.currentPosition ?: 0
                val totalDuration = mediaPlayer?.duration ?: 0

                binding.seekbar.max = totalDuration
                binding.seekbar.progress = currentPosition

                binding.tvStartTime.text = formatDuration(currentPosition)
                binding.tvEndTime.text = formatDuration(totalDuration)

                handler?.postDelayed(this, 1000) // Update every second
            }
        }, 0)
    }


    private fun formatDuration(durationInMillis: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis.toLong()) -
                TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun togglePlayback() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            updateIcon(false)
        } else {
            mediaPlayer?.start()
            updateIcon(true)
        }
    }

    private fun updateIcon(isPlay: Boolean) {
        if (isPlay) {
            binding.btnPlayPause.setImageResource(R.drawable.pause)
        } else {
            binding.btnPlayPause.setImageResource(R.drawable.play)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            updateIcon(false)
        }
    }

    override fun onStart() {
        super.onStart()
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
            updateIcon(true)
        }
    }
}
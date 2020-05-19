package com.danish.playbutton

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.play_button_layout.view.*


class PlayButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val TAG = PlayButton::class.java.simpleName

    lateinit var progressUpdate: Runnable

    // Data Values
    private var isPlayButtonStart = false
    private var audioFileName: String = ""


    // Views
    private lateinit var fabPlayButton: FloatingActionButton

    // Media Player
    private var mMediaPlayer: MediaPlayer = MediaPlayer()


    /**
     * Default Icon Resource ID
     */
    var defaultPlayIcon: Int = R.drawable.ic_play_arrow
    var defaultPauseIcon: Int = R.drawable.ic_pause
    var defaultPlayButtonColor: Int = R.color.fab_background
    var defaultCardBackgroundColor: Int = R.drawable.background
    var defaultProgressColor: Int = R.color.progress_background

    var playButtonListener: OnButtonListener? = null


    init {
        init(context, attrs)
    }

    private fun init(context: Context, attributes: AttributeSet?) {
        attributes?.let { attrs ->
            // Getting the views
            inflate(context, R.layout.play_button_layout, this)
            fabPlayButton = findViewById(R.id.fabPlayButton)
            //ciruclarProgressBar=findViewById(R.id.circularProgressBar)


            fabPlayButton.setOnClickListener {
                playButtonListener?.onPlayButtonClicked(this)
                if (!isPlayButtonStart) {
                    isPlayButtonStart = true
                    fabPlayButton.setImageDrawable(getDrawable(defaultPauseIcon))
                    playAudio(context, audioFileName)
                } else {
                    stopAudio()
                    isPlayButtonStart = false
                }


            }

            val typedArray =
                context.obtainStyledAttributes(attributes, R.styleable.play_button, 0, 0)
            typedArray.apply {
                defaultPlayIcon = getResourceId(
                    R.styleable.play_button_play_button_icon,
                    R.drawable.ic_play_arrow
                )
                defaultPauseIcon =
                    getResourceId(R.styleable.play_button_pause_button_icon, R.drawable.ic_pause)
                defaultPlayButtonColor = getResourceId(
                    R.styleable.play_button_play_button_default_background,
                    R.color.fab_background
                )
                defaultCardBackgroundColor = getResourceId(
                    R.styleable.play_button_card_background_color,
                    R.drawable.background
                )
                defaultProgressColor = getResourceId(
                    R.styleable.play_button_progress_color,
                    R.color.progress_background
                )

            }

            typedArray.recycle()

            // Apply Attributes
            applyAttributes()

        }
    }

    private fun applyAttributes() {
        fabPlayButton.setImageDrawable(getDrawable(defaultPlayIcon))
        fabPlayButton.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, defaultPlayButtonColor))
        frameContainer.setBackgroundResource(defaultCardBackgroundColor)
        progress.progressTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, defaultProgressColor))

    }

    /**
     * Get Drawable object from resource Id, irrespective of vector or image resource.
     *
     * @param resId Drawable resource Id
     * @return Drawable object from resource Id
     */
    private fun getDrawable(resId: Int): Drawable {
        return AppCompatResources.getDrawable(context, resId)!!
    }

    /**
     * this method is used to play the audio
     * @param mContext = view context
     * @param fileName = audio file name
     */
    fun playAudio(mContext: Context, fileName: String) {
        try {
            mMediaPlayer = MediaPlayer.create(
                mContext,
                mContext.resources.getIdentifier(fileName, "raw", mContext.packageName)
            )
            mMediaPlayer.setOnCompletionListener {
                progress.progress = 0
                progress.max = 0
                isPlayButtonStart = false
                handler.removeCallbacks(progressUpdate)
                fabPlayButton.setImageDrawable(getDrawable(defaultPlayIcon))
            }
            mMediaPlayer.start()
            updateProgress()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    /**
     * this method will stop the audio-- call it onDestroy() method
     */
    fun stopAudio() {
        try {
            progress.progress = 0
            progress.max = 0
            handler.removeCallbacks(progressUpdate)
            mMediaPlayer.release()
            fabPlayButton.setImageDrawable(getDrawable(defaultPlayIcon))

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }


    /**
     * this method will pause the audio-- call it onPause() method
     */
    fun pauseAudio() {
        try {
            isPlayButtonStart=false
            progress.progress = 0
            progress.max = 0
            handler.removeCallbacks(progressUpdate)
            mMediaPlayer.pause()
            fabPlayButton.setImageDrawable(getDrawable(defaultPlayIcon))

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }


    /**
     * this method will update the progress when audio play
     */
    fun updateProgress() {
        progress.max = mMediaPlayer.duration
        val interval: Long = 100

        progressUpdate = object : Runnable {
            override fun run() {
                progress.progress = mMediaPlayer.currentPosition
                handler.postDelayed(this, interval)
            }
        }
        progressUpdate.run()
    }

    /**
     * this method is used to set the audio file
     */
    fun setAudioFileName(fileName: String) {
        audioFileName = fileName
    }

    /**
     * this is a interface for play button click
     */
    interface OnButtonListener {
        fun onPlayButtonClicked(playButton: PlayButton)
    }

}

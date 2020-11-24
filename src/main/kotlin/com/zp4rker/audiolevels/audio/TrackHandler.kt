package com.zp4rker.audiolevels.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.zp4rker.audiolevels.PLAYER
import com.zp4rker.disbot.API
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author zp4rker
 */
class TrackHandler : AudioEventAdapter() {

    private val queue = LinkedBlockingQueue<TrackData>()

    init {
        PLAYER.addListener(this)
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) nextTrack()
    }

    fun queue(track: AudioTrack, channel: TextChannel, requester: User) {
        if (!PLAYER.startTrack(track, true)) {
            queue.offer(TrackData(track, channel, requester))
        }
    }

    fun nextTrack() {
        if (queue.peek() == null) API.guilds.forEach { it.audioManager.closeAudioConnection() }
        else PLAYER.startTrack(queue.poll()?.track, false)
    }

    fun restartTrack() {
        PLAYER.playingTrack.position = 0
    }

    fun getQueue(): Array<AudioTrack> = queue.toArray(arrayOf())

    fun clearQueue() {
        queue.clear()
    }

    var volume: Int
        set(value) {
            PLAYER.volume = value
        }
        get() = PLAYER.volume

    var paused: Boolean
        set(value) {
            PLAYER.isPaused = value
        }
        get() = PLAYER.isPaused

}
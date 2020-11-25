package com.zp4rker.audiolevels.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.zp4rker.audiolevels.HANDLER
import com.zp4rker.audiolevels.SCHEDULER
import com.zp4rker.disbot.extenstions.embed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
class TrackLoader(private val channel: TextChannel, private val requester: User, private val preLoad: Boolean = false) : AudioLoadResultHandler {

    override fun trackLoaded(track: AudioTrack) {
        if (!preLoad) channel.sendMessage(embed {
            title { text = "Added track to queue" }

            description = "```${track.info.title}```"

            field {
                name = "Duration"
                value = translateMillis(track.duration)
            }

            field {
                name = "Queue position"
                value = "${SCHEDULER.getQueue().size}"
            }

            footer {
                text = "Requested by ${requester.name}"
                iconUrl = requester.effectiveAvatarUrl
            }
        }).queue()

        val audioManager = channel.guild.audioManager

        if (audioManager.sendingHandler != HANDLER) audioManager.sendingHandler = HANDLER

        if (!audioManager.isConnected) audioManager.openAudioConnection(channel.guild.voiceChannels.first())

        SCHEDULER.queue(track, channel, requester)
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        playlist.tracks.forEach { SCHEDULER.queue(it, channel, requester) }

        if (!preLoad) channel.sendMessage(embed {
            title { text = "Added tracks from playlist to queue" }

            description = "```${playlist.name}```"

            field {
                name = "Amount of tracks"
                value = "${playlist.tracks.size}"
            }

            field {
                name = "Total duration of playlist"
                value = translateMillis(playlist.tracks.sumOf { it.duration })
            }

            footer {
                text = "Requested by ${requester.name}"
                iconUrl = requester.effectiveAvatarUrl
            }
        }).queue()
    }

    override fun noMatches() {
        if (!preLoad) channel.sendMessage(embed {
            title { text = "No matches found from that URL" }

            footer {
                text = "Requested by ${requester.name}"
                iconUrl = requester.effectiveAvatarUrl
            }
        }).queue()
    }

    override fun loadFailed(exception: FriendlyException) {
        if (!preLoad) channel.sendMessage(embed {
            title { text = "Failed to load track" }

            description = "```${exception.message}```"

            footer {
                text = "Requested by ${requester.name}"
                iconUrl = requester.effectiveAvatarUrl
            }
        }).queue()
    }
}

fun translateMillis(millis: Long): String {
    var seconds = millis / 1000
    val minutes = TimeUnit.SECONDS.toMinutes(seconds).also { seconds -= TimeUnit.MINUTES.toSeconds(it) }

    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}
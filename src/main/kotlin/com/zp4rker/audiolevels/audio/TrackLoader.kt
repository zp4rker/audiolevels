package com.zp4rker.audiolevels.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.zp4rker.audiolevels.SCHEDULER
import com.zp4rker.disbot.extenstions.embed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
class TrackLoader(private val channel: TextChannel, private val requester: User) : AudioLoadResultHandler {

    override fun trackLoaded(track: AudioTrack) {
        SCHEDULER.queue(track, channel, requester)

        channel.sendMessage(embed {
            title { text = "Added track to queue" }

            description = "```${track.info.title}```"

            field {
                name = "Duration"
                value = translateMillis(track.duration)
            }

            field {
                name = "Queue position"
                value = "${SCHEDULER.getQueue().indexOf(track) + 1}"
            }

            footer {
                text = "Requested by ${requester.name}"
                iconUrl = requester.effectiveAvatarUrl
            }
        }).queue()
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        playlist.tracks.forEach { SCHEDULER.queue(it, channel, requester) }

        channel.sendMessage(embed {
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
        channel.sendMessage(embed {
            title { text = "No matches found from that URL" }

            footer {
                text = "Requested by ${requester.name}"
                iconUrl = requester.effectiveAvatarUrl
            }
        }).queue()
    }

    override fun loadFailed(exception: FriendlyException) {
        channel.sendMessage(embed {
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
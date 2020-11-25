package com.zp4rker.audiolevels

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.zp4rker.audiolevels.audio.AudioHandler
import com.zp4rker.audiolevels.audio.TrackHandler
import com.zp4rker.audiolevels.audio.TrackLoader
import com.zp4rker.audiolevels.command.audio.*
import com.zp4rker.audiolevels.storage.audio.TrackBean
import com.zp4rker.disbot.API
import com.zp4rker.disbot.Bot
import com.zp4rker.disbot.bot
import com.zp4rker.disbot.extenstions.event.on
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.requests.GatewayIntent

/**
 * @author zp4rker
 */

lateinit var MANAGER: AudioPlayerManager
lateinit var PLAYER: AudioPlayer
lateinit var SCHEDULER: TrackHandler
lateinit var HANDLER: AudioHandler

fun main() {
    bot {
        name = "AudioLevels"
        version = Bot::class.java.`package`.implementationVersion

        token = System.getenv("AL_TOKEN")
        prefix = "!"

        intents = GatewayIntent.ALL_INTENTS

        commands = arrayOf(
            // Audio command
            PlayCommand,
            PauseCommand,
            ResumeCommand,
            StopCommand,
            QueueCommand
        )

        quit = {
            // save the queue
            val db = TrackBean()
            SCHEDULER.getQueue().forEach(db::add)
        }
    }

    MANAGER = DefaultAudioPlayerManager().also { AudioSourceManagers.registerRemoteSources(it) }
    PLAYER = MANAGER.createPlayer()
    SCHEDULER = TrackHandler()
    HANDLER = AudioHandler()

    API.on<ReadyEvent> {
        // load queue
        val db = TrackBean()
        db.getAll().forEach {
            MANAGER.loadItemOrdered(
                PLAYER,
                it.url,
                TrackLoader(API.getTextChannelById(it.channel)!!, API.getUserById(it.requester)!!, true)
            )
        }
    }
}
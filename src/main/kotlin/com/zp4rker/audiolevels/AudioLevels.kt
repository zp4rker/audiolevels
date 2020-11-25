package com.zp4rker.audiolevels

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.zp4rker.audiolevels.audio.AudioHandler
import com.zp4rker.audiolevels.audio.TrackHandler
import com.zp4rker.audiolevels.audio.TrackLoader
import com.zp4rker.audiolevels.command.audio.*
import com.zp4rker.audiolevels.command.levels.LeaderboardCommand
import com.zp4rker.audiolevels.command.levels.XpCommand
import com.zp4rker.audiolevels.levels.MessageListener
import com.zp4rker.audiolevels.storage.audio.TrackBean
import com.zp4rker.audiolevels.storage.levels.UserBean
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

val AUDIO_DB = TrackBean()
val LEVEL_DB = UserBean()

fun main() {
    println("uhhh hello?")
    bot {
        name = "AudioLevels"
        version = Bot::class.java.`package`.implementationVersion

        token = System.getenv("AL_TOKEN")
        prefix = "!"

        intents = GatewayIntent.ALL_INTENTS

        commands = arrayOf(
            // Audio commands
            PlayCommand,
            PauseCommand,
            ResumeCommand,
            StopCommand,
            QueueCommand,

            // Levels commands
            XpCommand,
            LeaderboardCommand
        )

        quit = {
            // save the queue
            SCHEDULER.getQueue().forEach(AUDIO_DB::add)
            // flush cache
            MessageListener.flushCache()
        }
    }

    MessageListener.register()

    MANAGER = DefaultAudioPlayerManager().also { AudioSourceManagers.registerRemoteSources(it) }
    PLAYER = MANAGER.createPlayer()
    SCHEDULER = TrackHandler()
    HANDLER = AudioHandler()

    API.on<ReadyEvent> {
        // load queue
        AUDIO_DB.getAll().forEach {
            MANAGER.loadItemOrdered(
                PLAYER,
                it.url,
                TrackLoader(API.getTextChannelById(it.channel)!!, API.getUserById(it.requester)!!, true)
            )
        }
    }
}
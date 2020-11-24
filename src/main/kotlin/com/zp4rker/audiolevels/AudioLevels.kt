package com.zp4rker.audiolevels

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.zp4rker.audiolevels.audio.AudioHandler
import com.zp4rker.audiolevels.audio.TrackHandler
import com.zp4rker.disbot.Bot
import com.zp4rker.disbot.bot
import net.dv8tion.jda.api.requests.GatewayIntent

/**
 * @author zp4rker
 */

lateinit var MANAGER: AudioPlayerManager
lateinit var PLAYER: AudioPlayer
lateinit var SCHEDULER: TrackHandler
lateinit var HANDLER: AudioHandler

fun main() {
    MANAGER = DefaultAudioPlayerManager()
    PLAYER = MANAGER.createPlayer()
    SCHEDULER = TrackHandler()
    HANDLER = AudioHandler(PLAYER)

    bot {
        name = "AudioLevels"
        version = Bot::class.java.`package`.implementationVersion

        token = System.getenv("AL_TOKEN")
        prefix = "!"

        intents = GatewayIntent.ALL_INTENTS
    }
}
package com.zp4rker.audiolevels.command.audio

import com.zp4rker.audiolevels.MANAGER
import com.zp4rker.audiolevels.PLAYER
import com.zp4rker.audiolevels.SCHEDULER
import com.zp4rker.audiolevels.audio.TrackLoader
import com.zp4rker.disbot.command.Command
import com.zp4rker.disbot.extenstions.embed
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
object PlayCommand : Command(aliases = arrayOf("play"), minArgs = 1) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        if (SCHEDULER.getQueue().any { it.track.info.uri == args[0] }) {
            channel.sendMessage(embed {
                title { text = "Track already in queue!" }

                description = "```${args[0]}```"

                footer {
                    text = "Requested by ${message.author.name}"
                    iconUrl = message.author.effectiveAvatarUrl
                }

                colour = 0x00ec644b
            }).queue { it.delete().queueAfter(3, TimeUnit.SECONDS) }
            return
        }

        MANAGER.loadItemOrdered(PLAYER, args[0], TrackLoader(channel, message.author))

        message.suppressEmbeds(true).queue()
    }

}
package com.zp4rker.audiolevels.command.audio

import com.zp4rker.audiolevels.SCHEDULER
import com.zp4rker.audiolevels.audio.translateMillis
import com.zp4rker.disbot.command.Command
import com.zp4rker.disbot.extenstions.embed
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.time.Instant

/**
 * @author zp4rker
 */
object QueueCommand : Command(aliases = arrayOf("queue")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val trackList = SCHEDULER.getQueue().map { it.track }

        val listString = if (trackList.isEmpty()) {
            "No items currently in the queue."
        } else {
            trackList.mapIndexed { i, t ->
                "${if (i > 0) "\n$i. " else ""}${t.info.title} ${if (i == 0) "[Currently playing]" else ""}"
            }.joinToString("\n\n")
        }

        channel.sendMessage(embed {
            title { text = "Current queue" }

            description = "```$listString```"

            field {
                name = "Total duration"
                value = translateMillis(trackList.sumOf { it.duration })
            }

            val durationRemaining = trackList[0].run { duration - position } + trackList.drop(1).sumOf { it.duration }

            field {
                name = "Tracks remaining"
                value = "${trackList.size - 1} (${translateMillis(durationRemaining)})"
            }

            footer { text = "Finishes" }

            timestamp = Instant.now().plusMillis(durationRemaining)
        }).queue()
    }

}
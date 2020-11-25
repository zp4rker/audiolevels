package com.zp4rker.audiolevels.command.audio

import com.zp4rker.audiolevels.SCHEDULER
import com.zp4rker.disbot.command.Command
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object PauseCommand : Command(aliases = arrayOf("pause")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        SCHEDULER.paused = true
        // TODO: add response msg
    }

}
package com.zp4rker.audiolevels.command.levels

import com.zp4rker.audiolevels.LEVEL_DB
import com.zp4rker.audiolevels.levels.xpToLevels
import com.zp4rker.disbot.command.Command
import com.zp4rker.disbot.extenstions.embed
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author zp4rker
 */
object LeaderboardCommand : Command(aliases = arrayOf("leaderboard", "lb", "toplevels")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        val topUsers = LEVEL_DB.getAll(10)

        channel.sendMessage(embed {
            title { text = "Top 10 Users" }

            description = topUsers.mapIndexed { i, d ->
                val user = channel.guild.getMemberById(d.user)!!.user
                "**${i + 1}.** ${user.asTag} (Lvl ${xpToLevels(d.xp)})"
            }.joinToString("\n")
        }).queue()
    }

}
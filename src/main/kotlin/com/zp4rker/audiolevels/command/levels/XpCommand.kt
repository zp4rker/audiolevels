package com.zp4rker.audiolevels.command.levels

import com.zp4rker.audiolevels.levels.UserData
import com.zp4rker.audiolevels.levels.nextLevelXp
import com.zp4rker.audiolevels.levels.remainingXp
import com.zp4rker.audiolevels.levels.xpToLevels
import com.zp4rker.disbot.command.Command
import com.zp4rker.disbot.extenstions.embed
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import java.util.concurrent.TimeUnit

/**
 * @author zp4rker
 */
object XpCommand : Command(aliases = arrayOf("xp", "level")) {

    override fun handle(args: Array<String>, message: Message, channel: TextChannel) {
        if (args.isEmpty()) {
            val data = UserData(message.author)

            channel.sendMessage(embed {
                title { text = "Your XP" }

                thumbnail = message.author.effectiveAvatarUrl

                field {
                    name = "Current level"
                    value = "${xpToLevels(data.xp)}"
                }

                field {
                    name = "Total XP"
                    value = "${data.xp}"
                }
            }).queue()
        } else {
            val user = if (message.mentionedUsers.isNotEmpty()) {
                message.mentionedUsers[0]
            } else {
                val username = args.joinToString(" ")
                channel.guild.members.find { it.effectiveName == username }?.user
            }

            if (user == null) {
                channel.sendMessage(embed {
                    title { text = "Could not find that user!" }

                    description = "Please try again."

                    colour = 0x00ec644b
                }).queue { it.delete().queueAfter(3, TimeUnit.SECONDS) }
            } else {
                val data = UserData(user)

                channel.sendMessage(embed {
                    title { text = "${user.name}'s XP" }

                    thumbnail = user.effectiveAvatarUrl

                    field {
                        name = "Current level"
                        value = "${xpToLevels(data.xp)}"
                    }

                    field {
                        name = "Total XP"
                        value = "${data.xp}"
                    }
                }).queue()
            }
        }
    }

}
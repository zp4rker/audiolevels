package com.zp4rker.audiolevels.levels

import com.zp4rker.audiolevels.LEVEL_DB
import com.zp4rker.disbot.API
import com.zp4rker.disbot.extenstions.event.on
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.random.Random

/**
 * @author zp4rker
 */
class MessageListener {

    companion object {
        private val spamCheck = mutableSetOf<User>()
        val cache = mutableSetOf<UserData>()
        private var flushTask: TimerTask? = null

        fun flushCache() {
            cache.forEach(LEVEL_DB::set)
            cache.clear()
            flushTask?.cancel()
            flushTask = null
        }

        fun register() {
            MessageListener()
        }
    }

    init {
        API.on<GuildMessageReceivedEvent>({ !spamCheck.contains(it.author) && !it.author.isBot }) {
            val data = cache.find { d -> d.user == it.author } ?: UserData(it.author)
            data.xp += Random.nextInt(10) + 16

            spamCheck.add(it.author).apply {
                Timer().schedule(1 * 60 * 1000) { spamCheck.remove(it.author) }
            }

            cache(data)
        }
    }

    private fun cache(data: UserData) {
        cache.removeIf { it.user == data.user }
        cache.add(data)

        if (cache.size > 15) {
            flushCache()
        }

        if (flushTask == null) {
            flushTask = Timer().scheduleAtFixedRate(0, 2 * 60 * 1000) { flushCache() }
        }
    }

}
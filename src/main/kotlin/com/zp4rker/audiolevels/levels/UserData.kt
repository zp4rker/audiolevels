package com.zp4rker.audiolevels.levels

import com.zp4rker.audiolevels.LEVEL_DB
import com.zp4rker.disbot.API
import net.dv8tion.jda.api.entities.User

/**
 * @author zp4rker
 */
data class UserData(val user: User, var xp: Long = 0) {

    init {
        xp = MessageListener.cache.find { it.user == user }?.xp ?: LEVEL_DB.get(user).xp
    }

    companion object {
        fun getAll(): Set<UserData> {
            val rawData = LEVEL_DB.getAll()
            return rawData.map { UserData(API.getUserById(it.user)!!, it.xp) }.toSet()
        }
    }

    fun save() = LEVEL_DB.set(this)

}

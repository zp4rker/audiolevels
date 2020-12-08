package com.zp4rker.audiolevels.storage.levels

import net.dv8tion.jda.api.entities.User
import java.sql.Connection
import java.sql.DriverManager


/**
 * @author zp4rker
 */
class UserBean {

    private val userSet = mutableSetOf<UserData>()

    init {
        getConnection().use {
            val sql = """
                CREATE TABLE IF NOT EXISTS users (
                    user BIGINT NOT NULL PRIMARY KEY,
                    xp BIGINT NOT NULL DEFAULT 0
                );
            """.trimIndent()

            it.createStatement().execute(sql)
        }
    }

    fun getAll(limit: Int = 0): Set<UserData> {
        val users = mutableSetOf<UserData>()

        getConnection().use {
            val sql = """
                SELECT * FROM users ORDER BY xp DESC${if (limit > 0) " LIMIT $limit" else ""};
            """.trimIndent()

            val result = it.createStatement().executeQuery(sql)

            while (result.next()) {
                val user = result.getLong("user")
                val xp = result.getLong("xp")

                users.add(UserData(user, xp))
            }
        }

        return users.also { userSet.addAll(users) }
    }

    fun get(user: User): UserData {
        userSet.find { it.user == user.idLong }?.let { return it }

        val data = UserData(user.idLong)
        getConnection().use {
            val sql = """
                SELECT xp FROM users WHERE user=${user.idLong};
            """.trimIndent()

            val result = it.createStatement().executeQuery(sql)

            while (result.next()) {
                data.xp = result.getLong("xp")
            }
        }

        return data
    }

    fun set(data: com.zp4rker.audiolevels.levels.UserData) {
        val user = data.user.idLong
        val xp = data.xp

        getConnection().use {
            val sql = """
                INSERT INTO users(user, xp) VALUES($user,$xp) ON DUPLICATE KEY UPDATE xp=$xp;
            """.trimIndent()

            it.createStatement().executeUpdate(sql)
        }
    }

    fun remove(user: User) {
        getConnection().use {
            val sql = """
                DELETE FROM users WHERE user=${user.idLong};
            """.trimIndent()

            it.createStatement().executeUpdate(sql)
        }
    }

    private fun getConnection(): Connection {
        Class.forName("com.mysql.cj.jdbc.Driver")
        return DriverManager.getConnection("jdbc:mysql://au1.zp4rker.com/audiolevels", "audiolevels", "ifyoureadthisyouareawesome")
    }

    data class UserData(var user: Long = 0, var xp: Long = 0)

}
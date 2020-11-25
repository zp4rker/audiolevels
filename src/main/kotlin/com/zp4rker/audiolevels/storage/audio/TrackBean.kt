package com.zp4rker.audiolevels.storage.audio

import java.io.File
import java.sql.Connection
import java.sql.DriverManager

/**
 * @author zp4rker
 */
class TrackBean {

    private val trackSet = mutableSetOf<TrackData>()

    init {
        getConnection().use {
            val sql = """
                CREATE TABLE IF NOT EXISTS tracks (
                    url TEXT NOT NULL PRIMARY KEY,
                    channel BIGINT NOT NULL,
                    requester BIGINT NOT NULL
                );
            """.trimIndent()

            it.createStatement().execute(sql)
        }
    }

    fun getAll(): Set<TrackData> {
        val tracks = mutableSetOf<TrackData>()

        getConnection().use {
            val sql = """
                SELECT * FROM tracks;
            """.trimIndent()

            val result = it.createStatement().executeQuery(sql)

            while (result.next()) {
                val url = result.getString("url")
                val channel = result.getLong("channel")
                val requester = result.getLong("requester")
                tracks.add(TrackData(url, channel, requester))
            }
        }

        return tracks.also { trackSet.addAll(tracks) }
    }

    fun add(trackData: com.zp4rker.audiolevels.audio.TrackData) {
        val url = trackData.track.info.uri
        val channel = trackData.channel.idLong
        val requester = trackData.requester.idLong

        getConnection().use {
            val sql = """
                INSERT INTO tracks(url, channel, requester) VALUES(?, ?, ?);
            """.trimIndent()

            it.prepareStatement(sql).runCatching {
                setString(1, url)
                setLong(2, channel)
                setLong(3, requester)
                executeUpdate()
            }
        }
    }

    fun remove(url: String) {
        getConnection().use {
            val sql = """
                DELETE FROM tracks WHERE url="$url";
            """.trimIndent()

            it.createStatement().executeUpdate(sql)
        }

        trackSet.removeIf { it.url == url }
    }

    fun removeAll() {
        getConnection().use {
            val sql = """
                DELETE FROM tracks;
            """.trimIndent()

            it.createStatement().executeUpdate(sql)
        }
    }

    private fun getConnection(): Connection {
        val file = File("queue.db")
        return DriverManager.getConnection("jdbc:sqlite:${file.absolutePath}")
    }

    data class TrackData(var url: String = "null", var channel: Long = 0, var requester: Long = 0)

}
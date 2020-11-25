package com.zp4rker.audiolevels.levels

import kotlin.math.pow

/**
 * @author zp4rker
 */

fun nextLevelXp(level: Int) = 2L * (level * level + 24 * level + 25)

fun levelsToXp(level: Int) = ((2 * level.toDouble().pow(3) + 3 * level.toDouble().pow(2) + level) / 6 + 12 * level.toDouble().pow(2) + 37 * level).toLong()

fun xpToLevels(xp: Long): Int {
    var level = 0

    while (true) {
        val nextXp = nextLevelXp(level + 1)
        level++
        if (xp < nextXp) break
    }

    return level
}

fun currentLevelXp(xp: Long) = xp - levelsToXp(xpToLevels(xp))

fun remainingXp(xp: Long) = nextLevelXp(xpToLevels(xp) + 1) - xp
package qorrnsmj.clematis.listener

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import qorrnsmj.clematis.Clematis.logger
import java.io.File
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object VoiceTimeManager {
    private val gson = Gson()

    private val joinTimes = ConcurrentHashMap<Long, Long>()
    private val totalTimes = ConcurrentHashMap<Long, Long>()
    private val weeklyTimes = ConcurrentHashMap<Long, Long>()
    private val monthlyTimes = ConcurrentHashMap<Long, Long>()

    private var currentWeek = getCurrentWeek()
    private var currentMonth = getCurrentMonth()

    init {
        loadAll()
    }

    /* ----------------- JOIN / LEAVE ----------------- */

    fun onJoin(userId: Long) {
        joinTimes[userId] = System.currentTimeMillis()
    }

    fun onLeave(userId: Long) {
        val joinTime = joinTimes.remove(userId) ?: return
        val duration = System.currentTimeMillis() - joinTime

        totalTimes[userId] = totalTimes.getOrDefault(userId, 0L) + duration
        weeklyTimes[userId] = weeklyTimes.getOrDefault(userId, 0L) + duration
        monthlyTimes[userId] = monthlyTimes.getOrDefault(userId, 0L) + duration

        saveAll()
    }

    /* ----------------- GET TIME ----------------- */

    fun getTotalTime(userId: Long): Long {
        val extra = joinTimes[userId]?.let { System.currentTimeMillis() - it } ?: 0L
        return totalTimes.getOrDefault(userId, 0L) + extra
    }

    fun getWeeklyTime(userId: Long): Long {
        val extra = joinTimes[userId]?.let { System.currentTimeMillis() - it } ?: 0L
        return weeklyTimes.getOrDefault(userId, 0L) + extra
    }

    fun getMonthlyTime(userId: Long): Long {
        val extra = joinTimes[userId]?.let { System.currentTimeMillis() - it } ?: 0L
        return monthlyTimes.getOrDefault(userId, 0L) + extra
    }

    /* ----------------- GET RANKING ----------------- */

    fun getTotalRanking() = totalTimes.entries
        .map { it.key to getTotalTime(it.key) }
        .sortedByDescending { it.second }

    fun getWeeklyRanking() = weeklyTimes.entries
        .map { it.key to getWeeklyTime(it.key) }
        .sortedByDescending { it.second }

    fun getMonthlyRanking() = monthlyTimes.entries
        .map { it.key to getMonthlyTime(it.key) }
        .sortedByDescending { it.second }

    /* ----------------- SAVE / LOAD ----------------- */

    private fun saveAll() {
        saveMap(totalTimes, "voice_times_total.json")
        saveMap(weeklyTimes, "voice_times_weekly.json")
        saveMap(monthlyTimes, "voice_times_monthly.json")
    }

    private fun loadAll() {
        loadMap(totalTimes, "voice_times_total.json")
        loadMap(weeklyTimes, "voice_times_weekly.json")
        loadMap(monthlyTimes, "voice_times_monthly.json")
        checkReset()
    }

    private fun saveMap(map: Map<Long, Long>, filename: String) {
        try {
            File(filename).writeText(gson.toJson(map))
        } catch (e: Exception) {
            logger.error("Failed to save $filename", e)
        }
    }

    private fun loadMap(map: MutableMap<Long, Long>, filename: String) {
        val file = File(filename)
        if (!file.exists()) return
        try {
            val type = object : TypeToken<Map<Long, Long>>() {}.type
            val data: Map<Long, Long> = gson.fromJson(file.readText(), type)
            map.putAll(data)
        } catch (e: Exception) {
            logger.error("Failed to load $filename", e)
        }
    }

    /* ----------------- RESET WEEK / MONTH ----------------- */

    private fun checkReset() {
        val week = getCurrentWeek()
        val month = getCurrentMonth()

        if (week != currentWeek) {
            weeklyTimes.clear()
            saveMap(weeklyTimes, "voice_times_weekly.json")
            currentWeek = week
            logger.info("Weekly voice times reset")
        }

        if (month != currentMonth) {
            monthlyTimes.clear()
            saveMap(monthlyTimes, "voice_times_monthly.json")
            currentMonth = month
            logger.info("Monthly voice times reset")
        }
    }

    private fun getCurrentWeek(): Int {
        return LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
    }

    private fun getCurrentMonth(): Int {
        return LocalDate.now().monthValue
    }
}

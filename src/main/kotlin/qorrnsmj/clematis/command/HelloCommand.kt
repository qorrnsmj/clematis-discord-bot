package qorrnsmj.clematis.command

import com.github.kaktushose.jda.commands.annotations.interactions.Choices
import com.github.kaktushose.jda.commands.annotations.interactions.Interaction
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand
import com.github.kaktushose.jda.commands.dispatching.events.interactions.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Interaction
class HelloCommand {
    @SlashCommand(value = "hello", desc = "Hello?")
    fun hello(event: CommandEvent) {
        event.reply("Hello ${event.member?.nickname ?: event.member!!.effectiveName} !")
    }

    @SlashCommand(value = "day-hello", desc = "Good Morning!")
    fun dayHello(event: CommandEvent, @Choices("set", "unset") action: String) {
        when (action) {
            "set" -> setDayHello(event)
            "unset" -> unsetDayHello(event)
        }
    }

    private fun setDayHello(event: CommandEvent) {
        if (scheduler != null && !scheduler!!.isShutdown) {
            event.reply("Already set day-hello!")
                .delete().queueAfter(5, TimeUnit.SECONDS)
            return
        }

        scheduler = Executors.newSingleThreadScheduledExecutor()
        scheduleNext(event)

        event.reply("Set day-hello!")
            .delete().queueAfter(5, TimeUnit.SECONDS)
    }

    private fun scheduleNext(event: CommandEvent) {
        val delay = getInitialDelay()
        scheduler?.schedule({
            event.messageChannel.sendMessageEmbeds(getMessageEmbed()).queue()
            scheduleNext(event)
        }, delay, TimeUnit.SECONDS)
    }

    private fun unsetDayHello(event: CommandEvent) {
        scheduler?.shutdownNow()
        scheduler = null

        event.reply("Unset day-hello!")
            .delete().queueAfter(5, TimeUnit.SECONDS)
    }

    private fun getMessageEmbed(): MessageEmbed {
        val oCount = Random.nextInt(1, 11)
        val emoji = if (oCount == 1) "<:god:1327940708589371442>" else ":sunny:"
        val title = "$emoji G" + "o".repeat(oCount) + "d Morning!"

        val today = LocalDate.now()
        val daysPassed = today.dayOfYear
        val progress = (daysPassed / 365f)
        val barLength = 20
        val filledLength = (barLength * progress).toInt()
        val progressBar = "#".repeat(filledLength) + " - ".repeat(barLength - filledLength)

        val description = """
            **[Date]** ${today.format(DateTimeFormatter.ofPattern("MMMM d - EEEE"))}
            **[Progress]** ${"%.2f".format(progress * 100f)}% ($daysPassed / 365)
            **[$progressBar]**
        """.trimIndent()

        return EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .setFooter("provided by clematis", null)
            .setColor(0xbb9adc)
            .build()
    }

    private fun getInitialDelay(): Long {
        val now = Calendar.getInstance()
        val target = now.clone() as Calendar
        target.set(Calendar.HOUR_OF_DAY, TARGET_HOUR)
        target.set(Calendar.MINUTE, 0)
        target.set(Calendar.SECOND, 0)
        target.set(Calendar.MILLISECOND, 0)

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_MONTH, 1)
        }

        return (target.timeInMillis - now.timeInMillis) / 1000
    }

    companion object {
        private const val TARGET_HOUR = 7
        private var scheduler: ScheduledExecutorService? = null
    }
}

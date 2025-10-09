package qorrnsmj.clematis.listener

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import qorrnsmj.clematis.Clematis.logger

class VoiceTimeListener : ListenerAdapter() {
    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        val member = event.member

        // joined channel
        if (event.channelJoined != null && event.channelLeft == null) {
            VoiceTimeManager.onJoin(member.idLong)
            logger.info("${member.effectiveName} joined ${event.channelJoined?.name}")
        }

        // left channel
        if (event.channelLeft != null && event.channelJoined == null) {
            VoiceTimeManager.onLeave(member.idLong)
            logger.info("${member.effectiveName} left ${event.channelLeft?.name}")
        }

        // moved between channels
        if (event.channelJoined != null && event.channelLeft != null) {
            VoiceTimeManager.onLeave(member.idLong)
            VoiceTimeManager.onJoin(member.idLong)
            logger.info("${member.effectiveName} moved from ${event.channelLeft?.name} to ${event.channelJoined?.name}")
        }
    }
}

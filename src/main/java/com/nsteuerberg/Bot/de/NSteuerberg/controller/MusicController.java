package com.nsteuerberg.Bot.de.NSteuerberg.controller;

import com.nsteuerberg.Bot.de.NSteuerberg.services.MusicService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MusicController {
    private final MusicService musicService;

    @Autowired
    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    public void playMusic(String songName, Guild guild, long idLong) {
        VoiceChannel voiceChanel = guild.getVoiceChannelById(idLong);
        AudioManager audioManager = guild.getAudioManager();
        if(!audioManager.isConnected()) {
            audioManager.openAudioConnection(voiceChanel);
        }

    }
}

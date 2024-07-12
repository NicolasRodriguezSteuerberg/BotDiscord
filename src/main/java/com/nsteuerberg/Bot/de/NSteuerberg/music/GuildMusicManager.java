package com.nsteuerberg.Bot.de.NSteuerberg.music;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/*
 * Esta clase se encarga de manejar la música en un servidor
 */
public class GuildMusicManager {
    private final AudioPlayer player;

    public final TrackScheduler scheduler;

    /**
     * Crea el reproductor de música y el planificador de pistas
     * @param manager Administrador de reproductores de audio para crear el reproductor
     */
    public GuildMusicManager(AudioPlayerManager manager) {
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }

}

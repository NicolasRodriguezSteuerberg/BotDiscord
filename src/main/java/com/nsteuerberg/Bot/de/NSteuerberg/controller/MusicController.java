package com.nsteuerberg.Bot.de.NSteuerberg.controller;

import com.nsteuerberg.Bot.de.NSteuerberg.music.AudioPlayerSendHandler;
import com.nsteuerberg.Bot.de.NSteuerberg.music.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MusicController {
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;


    public MusicController() {
        this.musicManagers = new HashMap<>();

        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public void playMusic(SlashCommandInteractionEvent event) {
        if(connectToVoiceChannerl(event)) {
            // recogemos el gestor de música del servidor
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
            String url = event.getOption("cancion").getAsString();
            System.out.println(url);
            playerManager.loadItemOrdered(musicManager, url,new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack audioTrack) {
                        event.reply("Añadiendo a la cola: " + audioTrack.getInfo().title).queue();
                        play(musicManager, audioTrack);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist audioPlaylist) {
                        AudioTrack firstTrack = audioPlaylist.getSelectedTrack();

                        if (firstTrack == null) {
                            firstTrack = audioPlaylist.getTracks().getFirst();
                        }
                        event.reply(
                                "Añadiendo a la cola: " + firstTrack.getInfo().title + " (Primera pista de la lista de reproducción" + audioPlaylist.getName() + ")"
                        ).queue();
                        play(musicManager, firstTrack);
                    }

                    @Override
                    public void noMatches() {
                        event.reply("No se ha encontrado la canción").queue();
                    }

                    @Override
                    public void loadFailed(FriendlyException e) {
                        event.reply("No se ha podido cargar la canción " + e.getMessage()).queue();
                    }
                }
            );
        }
    }

    public void skip(SlashCommandInteractionEvent event){
        // recogemos el gestor de música del servidor
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.scheduler.nextTrack();
        event.reply("Pista saltada").queue();
    }

    private void play(GuildMusicManager musicManager, AudioTrack track) {
        musicManager.scheduler.queue(track);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = guild.getIdLong();
        // recogemos el gestor de música del servidor de la lista
        GuildMusicManager musicManager = musicManagers.get(guildId);
        // si no existe lo creamos
        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    private boolean connectToVoiceChannerl(SlashCommandInteractionEvent event){
        // recogemos el miembro que ha ejecutado el comando
        Member member = event.getMember();
        // comprobamos que no sea nulo no vaya a ser que haya cambios en la api de discord
        if(member != null){
            // recogemos el estado de voz del miembro
            GuildVoiceState voiceState = member.getVoiceState();
            // comprobamos que no sea nulo y que el miembro este en un canal de voz
            if(voiceState != null && voiceState.inAudioChannel()){
                // recogemos el servidor
                Guild guild = event.getGuild();
                // recogemos el canal de voz por el canal que esta el miembro
                VoiceChannel voiceChanel = guild.getVoiceChannelById(voiceState.getChannel().getIdLong());
                // recogemos el audio manager del servidor (gestiona el audio)
                AudioManager audioManager = guild.getAudioManager();
                // comprobamos que el bot no este conectado
                if (!audioManager.isConnected()) {
                    audioManager.openAudioConnection(voiceChanel);
                    return true;
                } else {
                    event.reply("Ya estoy conectado a un canal de voz").queue();
                    return false;
                }

            } else {
                event.reply("Debes estar en un canal de voz").queue();
                return false;
            }
        } else {
            event.reply("No se ha podido conectar al canal de voz").queue();
            return false;
        }
    }
}

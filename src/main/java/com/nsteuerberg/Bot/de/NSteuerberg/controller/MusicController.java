package com.nsteuerberg.Bot.de.NSteuerberg.controller;

import com.nsteuerberg.Bot.de.NSteuerberg.music.GuildMusicManager;
import com.nsteuerberg.Bot.de.NSteuerberg.music.SpotifySearcher;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MusicController {
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private final SpotifySearcher spotifySearcher;

    @Autowired
    public MusicController(SpotifySearcher spotifySearcher) {
        this.musicManagers = new HashMap<>();
        playerManager = new DefaultAudioPlayerManager();
        this.spotifySearcher = spotifySearcher;
        YoutubeAudioSourceManager youtubeAudioSourceManager = new dev.lavalink.youtube.YoutubeAudioSourceManager();
        playerManager.registerSourceManager(youtubeAudioSourceManager);
        AudioSourceManagers.registerRemoteSources(playerManager,com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public void playMusic(SlashCommandInteractionEvent event) {
        if(connectToVoiceChannerl(event)) {
            String url = event.getOption("cancion").getAsString();

            play(event, url);
        }
    }

    public void skip(SlashCommandInteractionEvent event){
        // recogemos el gestor de música del servidor
        if (connectToVoiceChannerl(event)) {
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
            musicManager.scheduler.nextTrack();
            event.reply("Pista saltada").queue();
        }
    }

    private void play(SlashCommandInteractionEvent event, String trackUrl) {
        Guild guild = event.getGuild();
        GuildMusicManager guildMusicManager = getGuildAudioPlayer(guild);
        playerManager.loadItemOrdered(guildMusicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildMusicManager.getTrackScheduler().queue(track);
                System.out.println(track.getInfo().title);
                System.out.println(track.getInfo().toString());
                event.reply(track.getInfo().title + " añadida a la cola").queue();
            }
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                guildMusicManager.getTrackScheduler().queueAll(playlist.getTracks());
                event.reply("Playlist añadida a la cola").queue();
            }

            @Override
            public void noMatches() {
                // Notify the user that we've got nothing
                event.reply("No se ha encontrado la canción").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                // Notify the user that everything exploded
                event.reply("No se ha podido cargar la canción").queue();
            }
        });
    }

    private GuildMusicManager getGuildAudioPlayer(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            GuildMusicManager musicManager = new GuildMusicManager(playerManager);
            guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
            return musicManager;
        });
        /* seria algo asi
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
        */
    }

    private boolean connectToVoiceChannerl(SlashCommandInteractionEvent event){
        // recogemos el miembro que ha ejecutado el comando
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();
        // recogemos el estado de voz del miembro
        GuildVoiceState voiceState = member.getVoiceState();
        // comprobamos que no sea nulo y que el miembro este en un canal de voz
        if(voiceState != null && voiceState.inAudioChannel()) {
            Member self = event.getGuild().getSelfMember();
            GuildVoiceState selfVoiceState = self.getVoiceState();
            if (!selfVoiceState.inAudioChannel()) {
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.openAudioConnection(memberVoiceState.getChannel());
                GuildMusicManager guildMusicManager = getGuildAudioPlayer(event.getGuild());
                guildMusicManager.getTrackScheduler().setAudioManager(audioManager);
                if(event.getChannel() instanceof TextChannel){
                    System.out.println("Es un canal de texto");
                    guildMusicManager.getTrackScheduler().setTextChannel((TextChannel) event.getChannel());
                }
                else if(event.getChannel() instanceof VoiceChannel) {
                    guildMusicManager.getTrackScheduler().setVoiceChannel((VoiceChannel) event.getChannel());
                    System.out.println("Es un canal de voz");
                }
                return true;
            } else {
                if (selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
                    event.reply("Ya estoy conectado a un canal de voz").queue();
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            event.reply("Debes estar en un canal de voz").queue();
            return false;
        }
    }
}

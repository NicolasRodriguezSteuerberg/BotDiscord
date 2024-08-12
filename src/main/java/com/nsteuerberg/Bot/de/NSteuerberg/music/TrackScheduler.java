package com.nsteuerberg.Bot.de.NSteuerberg.music;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Esta clase se encarga de manejar las canciones en un servidor.
 * Contiene un reproductor de audio y una cola de reproducción
 */
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private TextChannel textChannel;
    private VoiceChannel voiceChannel;

    private AudioManager audioManager;

    /**
     * Constructor de la clase
     * @param player reproductor de audio
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public void setVoiceChannel(VoiceChannel voiceChannel) {
        this.voiceChannel = voiceChannel;
    }

    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public void queue(AudioTrack track){
        if(!player.startTrack(track, true)){
            queue.offer(track);
        }
    }

    /**
     * Reproduce la siguiente canción de la cola
     */
    public void nextTrack(){
        // Inicia la siguiente pista, sin importar si ya se está reproduciendo algo o no.
        // En caso de que la cola esté vacía, pasamos null a startTrack, lo cual es un argumento válido y simplemente detendrá el reproductor.
        // queue.poll() recupera y elimina el primer elemento de la cola
        player.startTrack(queue.poll(), false);
        if (queue.isEmpty()) {
            disconnect();
        }
    }

    private void disconnect(){
        player.stopTrack();
        player.destroy();
        if (voiceChannel != null) {
            voiceChannel = null;
            textChannel = null;
            audioManager.closeAudioConnection();
        }
    }

    public AudioTrack getTrack() {
        return queue.peek();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        super.onPlayerPause(player);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        super.onPlayerResume(player);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        super.onTrackStart(player, track);
        if(textChannel != null){
            textChannel.sendMessageEmbeds(createEmbed(track).build()).queue();
        } else if (voiceChannel != null){
            voiceChannel.sendMessageEmbeds(createEmbed(track).build()).queue();
        }
    }

    private EmbedBuilder createEmbed(AudioTrack track){
        AudioTrackInfo info = track.getInfo();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Reproduciendo ahora");
        embed.addField("**Título**", info.title, true);
        embed.addField("**Autor**", info.author, true);
        embed.addField("**Duración**", String.valueOf(info.length), true);
        return embed;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext){
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        super.onTrackException(player, track, exception);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        super.onTrackStuck(player, track, thresholdMs);
    }
}

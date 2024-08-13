package com.nsteuerberg.Bot.de.NSteuerberg.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;


/**
 * Este es un envoltorio alrededor de AudioPlayer que lo hace comportarse como un AudioSendHandler para JDA.
 * Como JDA llama a canProvide antes de cada llamada a provide20MsAudio(),
 * extraemos el marco en canProvide() y usamos el marco que ya hemos extraído en provide20MsAudio().
 */
public class AudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private final ByteBuffer buffer;
    private final MutableAudioFrame frame;

    /**
     * @param audioPlayer reproductor de audio que se va a enviar
     */
    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.buffer = ByteBuffer.allocate(1024);
        this.frame = new MutableAudioFrame();
        this.frame.setBuffer(buffer);
    }

    @Override
    public boolean canProvide() {
        return audioPlayer.provide(frame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        // flip para hacer que la memoria sea accesible
        return buffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}

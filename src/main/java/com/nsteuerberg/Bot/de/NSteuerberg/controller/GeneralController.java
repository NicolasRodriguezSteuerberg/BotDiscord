package com.nsteuerberg.Bot.de.NSteuerberg.controller;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeneralController extends ListenerAdapter {

    private final UserScoreController userScoreController;
    private final MusicController musicController;

    @Autowired
    public GeneralController(UserScoreController userScoreController, MusicController musicController) {
        this.userScoreController = userScoreController;
        this.musicController = musicController;
    }

    // al recibir un mensaje, se impprime hola
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.getAuthor().isBot() && event.isFromGuild()) {
            userScoreController.incrementScore(
                    event.getAuthor().getId(),
                    event.getGuild().getId()
            );
        } else if(!event.isFromGuild() && !event.getAuthor().isBot()){
            event.getChannel().sendMessage("Por favor úsame en un server, no puedo hacer nada por mensajes privados").queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.isFromGuild() && !event.getUser().isBot()) {
            switch (event.getName()) {
                case "saludar":
                    event.reply("Buenas tardes que tal").queue();
                    break;
                case "puntuacion":
                    event.reply("Tu puntuación es: " +
                                    userScoreController.obtenerPuntuacion(
                                            event.getUser().getId(),
                                            event.getGuild().getId()
                                    )).queue();
                    break;
                case "top":
                    event.replyEmbeds(
                        userScoreController.getTopScores(
                            event.getGuild().getId()
                        ).build()
                    ).queue();
                    break;
                case "play":
                    musicController.playMusic(event);
                    break;
                case "skip":
                    musicController.skip(event);
                    break;
                default:
                    event.reply("No se que hacer").queue();
            }
        } else{
            event.reply("Este bot no es capaz de ejecutar comandos en mensajes privados").queue();
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("play") && event.getFocusedOption().getName().equals("cancion")) {
            musicController.handleAutocomplete(event);
        }
    }
}

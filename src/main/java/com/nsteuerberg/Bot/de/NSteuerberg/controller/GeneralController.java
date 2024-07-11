package com.nsteuerberg.Bot.de.NSteuerberg.controller;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeneralController extends ListenerAdapter {

    private final UserScoreController userScoreController;

    public GeneralController(UserScoreController userScoreController) {
        this.userScoreController = userScoreController;
    }

    // al recibir un mensaje, se impprime hola
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.getAuthor().isBot()) {
            userScoreController.incrementScore(
                    event.getAuthor().getId(),
                    event.getGuild().getId()
            );
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getUser().isBot()) {
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
                /*case "top":
                    event.replyEmbeds(userScoreController.getTopScores(event.getGuild().getId()).build()).queue();
                    break;*/
                default:
                    event.reply("No se que hacer").queue();
            }
        }
    }
}

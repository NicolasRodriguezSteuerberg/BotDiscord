package com.nsteuerberg.Bot.de.NSteuerberg.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import net.dv8tion.jda.api.JDA;

@Component
public class CommandsBot {
    private final JDA jda;

    @Autowired
    public CommandsBot(JDA jda) {
        this.jda = jda;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        jda.updateCommands().addCommands(
                Commands.slash("saludar", "Saluda al bot"),
                Commands.slash("puntuacion", "Muestra la puntuación del usuario"),
                Commands.slash("top", "Muestra el top 5 de puntuaciones")
        ).queue();
    }


}

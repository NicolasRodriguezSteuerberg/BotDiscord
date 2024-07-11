package com.nsteuerberg.Bot.de.NSteuerberg.controller;

import com.nsteuerberg.Bot.de.NSteuerberg.services.UserScoreService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserScoreController{

    private final UserScoreService userScoreService;

    @Autowired
    public UserScoreController(UserScoreService userScoreService){
        this.userScoreService = userScoreService;
    }


    public void incrementScore(String userId, String serverId){
        userScoreService.incrementScore(userId, serverId);
    }

    public int obtenerPuntuacion(String userId, String serverId){
        return userScoreService.obtenerPuntuacion(userId, serverId);
    }

    public EmbedBuilder getTopScores(String serverId){
        return userScoreService.getTopScores(serverId);
    }
}

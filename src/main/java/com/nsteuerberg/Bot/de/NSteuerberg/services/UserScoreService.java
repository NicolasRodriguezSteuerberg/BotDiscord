package com.nsteuerberg.Bot.de.NSteuerberg.services;

import com.nsteuerberg.Bot.de.NSteuerberg.model.UserScore;
import com.nsteuerberg.Bot.de.NSteuerberg.model.UserScoreKey;
import com.nsteuerberg.Bot.de.NSteuerberg.repository.UserScoreRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.JDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
public class UserScoreService {

    private final UserScoreRepository userScoreRepository;


    @Autowired
    public UserScoreService(UserScoreRepository userScoreRepository) {
        this.userScoreRepository = userScoreRepository;
    }

    public UserScore findOrCreateUserScore(String userId, String serverId) {
        UserScoreKey userScoreKey = new UserScoreKey(userId, serverId);
        UserScore userScore = userScoreRepository.findById(userScoreKey).orElse(null);
        if (userScore == null) {
            userScore = userScoreRepository.save(new UserScore(userScoreKey));
        }
        return userScore;
    }

    public int obtenerPuntuacion(String userId, String serverId) {
        UserScore userScore = findOrCreateUserScore(userId, serverId);
        return userScore.getScore();
    }

    public void incrementScore(String userId, String serverId) {
        UserScore userScore = findOrCreateUserScore(userId, serverId);
        userScore.setScore(userScore.getScore() + 1);
        userScoreRepository.save(userScore);
    }

    /*
    public EmbedBuilder getTopScores(String serverId){
        // recoger puntuacion
        List<UserScore> topFive = userScoreRepository.findTopByServerId(serverId);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Tabla de puntaciones");
        if (topFive.size() > 5) {
            topFive = topFive.subList(0, 5);
        }
        for (int i = 0; i < topFive.size(); i++) {
            UserScore userScore = topFive.get(i);
            embedBuilder.addField(
                    "Puesto " + (i + 1),
                    "Usuario: " + getUserName(userScore.getUserId(), userScore.getServerId()) + " Puntuación: " + userScore.getScore(),
                    false
            );
        }
        return embedBuilder;
    }

    public String getUserName(String userId, String serverId){
        User user = jda.getUserById(userId);
        if (user != null) {
            Guild guild = jda.getGuildById(serverId);
            if (guild != null){
                var member = guild.getMemberById(userId);
                if (member != null){
                    return member.getEffectiveName();
                } else {
                    return user.getName();
                }
            }
        }
        return "Usuario no encontrado";
    }
    */
}

package com.nsteuerberg.Bot.de.NSteuerberg.services;

import com.nsteuerberg.Bot.de.NSteuerberg.model.UserScore;
import com.nsteuerberg.Bot.de.NSteuerberg.model.UserScoreKey;
import com.nsteuerberg.Bot.de.NSteuerberg.repository.UserScoreRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.util.List;

@Service
public class UserScoreService {

    private final UserScoreRepository userScoreRepository;
    private final JDA jda;

    @Autowired
    public UserScoreService(UserScoreRepository userScoreRepository, @Lazy JDA jda) {
        this.userScoreRepository = userScoreRepository;
        this.jda = jda;
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


    public EmbedBuilder getTopScores(String serverId){
        // recoger puntuacion
        List<UserScore> topFive = userScoreRepository.findTopByServerId(serverId);
        // crear embed (mensaje con formato)
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.BLUE);
        // si no hay usuarios devolvemos el embed
        if (topFive.isEmpty()) {
            return embedBuilder.setTitle("NADIE HA HABLADO EN ESTE SERVER");
        }
        // recoger la imagen del servidor
        String urlImage = jda.getGuildById(serverId).getIconUrl();
        if (urlImage == null) {
            urlImage = jda.getSelfUser().getAvatarUrl();
        }

        embedBuilder.setAuthor("TABLA DE PUNTUACIONES", null, urlImage);

        // si hay mas de 5 usuarios, se coge solo los 5 primeros
        if (topFive.size() > 5) {
            topFive = topFive.subList(0, 5);
        }
        String positionEmoji;
        for (int i = 0; i < topFive.size(); i++) {
            UserScore userScore = topFive.get(i);
            // asignar emoji segun la posicion
            switch (i) {
                case 0:
                    positionEmoji = "🥇";
                    String avatar = jda.retrieveUserById(userScore.getUserId()).complete().getAvatarUrl();
                    if (avatar != null) {
                        embedBuilder.setThumbnail(avatar);
                    }
                    break;
                case 1:
                    positionEmoji = "🥈";
                    break;
                case 2:
                    positionEmoji = "🥉";
                    break;
                default:
                    positionEmoji = "🗑️";
            }
            // añadir al embed
            embedBuilder.addField(
                    positionEmoji + " Puesto " + (i + 1),
                    "<@!" + userScore.getUserId() + "> | **XP:** `" + userScore.getScore() + "`",
                    false
            );
            // añadir imagen del servidor
        }
        return embedBuilder;
    }

    /*
    public String getUserName(String userId, String serverId){
        System.out.println("userId: " + userId + " serverId: " + serverId);
        System.out.println("--------------------");
        try {
            User user = jda.retrieveUserById(userId).complete();
            // si el usuario no existe en el servidor
            if (user != null) {
                Guild guild = jda.getGuildById(serverId);
                if (guild != null) {
                    // busca el usuario en el servidor (en la cache)
                    Member member = guild.getMemberById(userId);
                    System.out.println("member: " + member);
                    if (member != null) {
                        System.out.println("member: " + member.getEffectiveName());
                        return member.getEffectiveName();
                    } else {
                        try {
                            // si no lo encuentra en la cache, lo busca en el servidor
                            member = guild.retrieveMember(user).complete();
                            System.out.println("member: " + member.getEffectiveName());
                            return member.getEffectiveName();
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        System.out.println("user: " + user.getName());
                        return user.getEffectiveName();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return "Usuario no encontrado";
    }
    */
}

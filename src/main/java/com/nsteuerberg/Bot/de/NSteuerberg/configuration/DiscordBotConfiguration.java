package com.nsteuerberg.Bot.de.NSteuerberg.configuration;

import com.nsteuerberg.Bot.de.NSteuerberg.controller.GeneralController;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application-secret.properties", ignoreResourceNotFound = true)
/*
* Esta clase se encarga de configurar el bot de discord, poner el token y el estado del bot
*/
public class DiscordBotConfiguration {

    @Value("${discord.bot.token}")
    private String botToken;

    private final GeneralController generalController;

    @Autowired
    public DiscordBotConfiguration(GeneralController generalController) {
        this.generalController = generalController;
    }

    @Bean
    public JDA jdaBuilder() throws InterruptedException {
         return JDABuilder
                .createDefault(botToken)
                .setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, "Espiando a las chicas de la noche"))
                .addEventListeners(generalController).build().awaitReady();
    }
}

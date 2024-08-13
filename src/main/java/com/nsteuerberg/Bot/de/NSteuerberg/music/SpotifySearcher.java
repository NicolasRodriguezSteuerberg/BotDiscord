package com.nsteuerberg.Bot.de.NSteuerberg.music;

import jakarta.annotation.PostConstruct;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;

@Component
public class SpotifySearcher {
    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private SpotifyApi spotifyApi;
    private ClientCredentialsRequest clientCredentialsRequest;


    @PostConstruct
    public void init() {
        System.out.println("CLIENT ID: " + clientId);
        System.out.println("CLIENT SECRET: " + clientSecret);
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
        clientCredentialsRequest = spotifyApi.clientCredentials().build();
        updateAccessToken();
        System.out.println("Access token: " + spotifyApi.getAccessToken());
    }

    private void updateAccessToken() {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    public String searchTracks(String query) throws Exception {
        Paging<Track> trackPaging = spotifyApi.searchTracks(query).build().execute();
        if (trackPaging.getItems().length > 0) {
            return trackPaging.getItems()[0].getExternalUrls().get("spotify");
        } else {
            return null;
        }
    }

}

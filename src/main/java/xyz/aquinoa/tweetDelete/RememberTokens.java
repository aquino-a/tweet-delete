/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.aquinoa.tweetDelete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.twitter.clientlib.ApiClientCallback;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author alex
 */
public class RememberTokens implements ApiClientCallback {

    private static final Logger LOGGER = LogManager.getLogger();
    
    private final ObjectMapper objectMapper;

    public RememberTokens(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAfterRefreshToken(OAuth2AccessToken accessToken) {
        OauthValues oauthValues = null;
        try ( var is = getTokenInput()) {
            oauthValues = objectMapper.readValue(is, OauthValues.class);
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, "Problem reading Oauth values.", ex);
        }

        oauthValues.setAccessToken(accessToken.getAccessToken());
        oauthValues.setRefreshToken(accessToken.getRefreshToken());

        try ( var is = new FileOutputStream("./tokens.json")) {
            objectMapper.writeValue(is, oauthValues);
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, "Problem saving Oauth values.", ex);
        }

        LOGGER.log(Level.INFO, "Refreshed Tokens.");
        LOGGER.log(Level.INFO, "access: " + accessToken.getAccessToken());
        LOGGER.log(Level.INFO, "refresh: " + accessToken.getRefreshToken());
    }

    private InputStream getTokenInput() throws IOException {
        if (Files.exists(Path.of("./tokens.json"))) {
            return new FileInputStream("./tokens.json");
        } else {
            return RememberTokens.class.getResourceAsStream("/tokens.json");
        }
    }
}

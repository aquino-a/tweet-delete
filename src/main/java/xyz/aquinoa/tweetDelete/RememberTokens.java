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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alex
 */
public class RememberTokens implements ApiClientCallback {

    private static final Logger LOGGER = Logger.getLogger(RememberTokens.class.getName());
    private final ObjectMapper objectMapper;

    public RememberTokens(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAfterRefreshToken(OAuth2AccessToken accessToken) {
        OauthValues oauthValues = null;
        try ( var is = new FileInputStream("./tokens.json")) {
            oauthValues = objectMapper.readValue(is, OauthValues.class);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Problem reading Oauth values.", ex);
        }

        oauthValues.setAccessToken(accessToken.getAccessToken());
        oauthValues.setRefreshToken(accessToken.getRefreshToken());

        try ( var is = new FileOutputStream("./tokens.json")) {
            objectMapper.writeValue(is, oauthValues);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Problem saving Oauth values.", ex);
        }

        LOGGER.log(Level.INFO, "Refreshed Tokens.");
        LOGGER.log(Level.INFO, "access: " + accessToken.getAccessToken());
        LOGGER.log(Level.INFO, "refresh: " + accessToken.getRefreshToken());
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.aquinoa.tweetDelete;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.twitter.clientlib.ApiClientCallback;
import java.io.FileInputStream;
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

    @Override
    public void onAfterRefreshToken(OAuth2AccessToken accessToken) {
        var prop = new Properties();
        try ( var is = new FileInputStream("./config.properties")) {
            prop.load(is);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Problem reading config properties", ex);
        }

        prop.setProperty("access-token", accessToken.getAccessToken());
        prop.setProperty("refresh-token", accessToken.getRefreshToken());

        try ( var os = new FileOutputStream("./config.properties")) {
            prop.store(os, null);
        }

        LOGGER.log(Level.INFO, "access: " + accessToken.getAccessToken());
        LOGGER.log(Level.INFO, "refresh: " + accessToken.getRefreshToken());
    }
}

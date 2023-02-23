/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.aquinoa.tweetDelete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alex
 */
public class App {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws ApiException, IOException {
        var deleteOptions = getDeleteOptions();
        var twitterApi = getTwitterApi();

        var deleter = new TweetDeleter(deleteOptions, twitterApi);
        deleter.delete();
    }

    private static TwitterApi getTwitterApi() throws IOException {

        try ( var is = new FileInputStream("./tokens.json")) {
            var oauthValues = OBJECT_MAPPER.readValue(is, OauthValues.class);

            var twitterApi = new TwitterApi(new TwitterCredentialsOAuth2(
                    oauthValues.getClientId(),
                    oauthValues.getClientSecret(),
                    oauthValues.getAccessToken(),
                    oauthValues.getRefreshToken()));
            twitterApi.addCallback(new RememberTokens(OBJECT_MAPPER));
            return twitterApi;
        }
    }

    private static DeleteOptions getDeleteOptions() throws IOException {
        try(var is = new FileInputStream("./options.json")){
            return OBJECT_MAPPER.readValue(is, DeleteOptions.class);
        }
    }
}

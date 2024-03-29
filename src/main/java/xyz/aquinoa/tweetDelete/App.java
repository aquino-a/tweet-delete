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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 *
 * @author alex
 */
public class App {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws ApiException, IOException {
        var deleteOptions = getDeleteOptions();
        var twitterApi = getTwitterApi();

        var userId = getUserId(twitterApi);
        deleteOptions.setUserId(userId);
        
        var deleter = new TweetDeleter(deleteOptions, twitterApi);
        deleter.delete();
    }

    private static TwitterApi getTwitterApi() throws IOException {
        if (!Files.exists(Path.of("tokens.json"))) {
            try ( var is = App.class.getResourceAsStream("/tokens.json")) {
                return getTwitterFromStream(is);
            }
        } else {
            try ( var is = new FileInputStream("./tokens.json")) {
                return getTwitterFromStream(is);
            }
        }
    }

    private static TwitterApi getTwitterFromStream(InputStream stream) throws IOException {
        var oauthValues = OBJECT_MAPPER.readValue(stream, OauthValues.class);
        var credentials = new TwitterCredentialsOAuth2(
                oauthValues.getClientId(),
                oauthValues.getClientSecret(),
                oauthValues.getAccessToken(),
                oauthValues.getRefreshToken(),
                true);
        var twitterApi = new TwitterApi(credentials);
        twitterApi.addCallback(new RememberTokens(OBJECT_MAPPER));
        return twitterApi;
    }

    private static DeleteOptions getDeleteOptions() throws IOException {
        if (!Files.exists(Path.of("options.json"))) {
            try ( var is = App.class.getResourceAsStream("/options.json")) {
                return getOptionsFromStream(is);
            }
        } else {
            try ( var is = new FileInputStream("./options.json")) {
                return getOptionsFromStream(is);
            }
        }
    }

    private static DeleteOptions getOptionsFromStream(InputStream stream) throws IOException {
        return OBJECT_MAPPER.readValue(stream, DeleteOptions.class);
    }
    
    private static String getUserId(TwitterApi twitterApi) throws ApiException{
        var response = twitterApi.users()
                .findMyUser()
                .userFields(Set.of("id", "pinned_tweet_id"))
                .execute();
        
        var user = response.getData();
        
        return user.getId();
    }
}

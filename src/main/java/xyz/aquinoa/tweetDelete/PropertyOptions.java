/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.aquinoa.tweetDelete;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads the delete options from the config.properties file.
 *
 * @author alex
 */
public class PropertyOptions {

    private static final Logger LOGGER = Logger.getLogger(PropertyOptions.class.getName());

    public DeleteOptions Load(String path) {
        var prop = new Properties();
        try ( var is = new FileInputStream("./config.properties")) {
            prop.load(is);

            var builder = new DeleteOptions.Builder();
            addInt(builder::age, prop, "age");
            addInt(builder::likes, prop, "likes");
            addBool(builder::isRetweet, prop, "retweets");

            return builder.build();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Problem reading config properties", ex);
            return null;
        }
    }

    private void add(Consumer<String> consume, Properties prop, String propertyName) {
        if (prop.containsKey(propertyName)) {
            consume.accept((String) prop.get(propertyName));
        }
    }
    
    private void addInt(Consumer<Integer> consume, Properties prop, String propertyName) {
        if (prop.containsKey(propertyName)) {
            consume.accept((int) prop.get(propertyName));
        }
    }
    
    private void addInt(Consumer<Integer> consume, Properties prop, String propertyName) {
        if (prop.containsKey(propertyName)) {
            consume.accept((int) prop.get(propertyName));
        }
    }
    
    
    
    
}

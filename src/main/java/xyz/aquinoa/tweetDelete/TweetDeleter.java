/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.aquinoa.tweetDelete;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.Tweet;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

/**
 *
 * @author alex
 */
public class TweetDeleter {

    private final DeleteOptions deleteOptions;
    private final TwitterApi twitterApi;

    public TweetDeleter(final DeleteOptions deleteOptions, final TwitterApi twitterApi) {
        this.deleteOptions = deleteOptions;
        this.twitterApi = twitterApi;
    }

    public void delete() throws ApiException {
        var timelineResponse = twitterApi
                .tweets()
                .usersIdTimeline(deleteOptions.getUserId())
                .execute();
        var tweets = timelineResponse.getData();

        for (Tweet tweet : tweets) {
            if (shouldDelete(tweet)) {
                var deleteResponse = twitterApi
                        .tweets()
                        .deleteTweetById(tweet.getId())
                        .execute();
            }
        }
    }

    private boolean shouldDelete(Tweet tweet) {
        if (!deleteOptions.isNormal() && tweet.getEntities().getMentions().size() == 0) {
            return false;
        }

        if (deleteOptions.getAge() > -1) {
            var ageDuration = Duration.between(tweet.getCreatedAt(), ZonedDateTime.now());

            if (ageDuration.toDays() < deleteOptions.getAge()) {
                return false;
            }
        }

        if (deleteOptions.getLikes() > -1 && tweet.getPublicMetrics().getLikeCount() >= deleteOptions.getLikes()) {
            return false;
        }

        if (deleteOptions.getExceptions().size() > 0) {
            var mentionedUsers = tweet.getEntities()
                    .getMentions()
                    .stream()
                    .map(me -> me.getUsername())
                    .collect(Collectors.toSet());

            var isMatch = deleteOptions.getExceptions()
                    .stream()
                    .anyMatch(id -> mentionedUsers.contains(id));

            if (isMatch) {
                return false;
            }
        }

        //??
//        if (deleteOptions.isRetweet() && tweet.Retweet()) {
//            return false;
//        }

        if (!deleteOptions.isReply() && tweet.getInReplyToUserId() == null) {
            return false;
        }

        //??
//        if (deleteOptions.isPinned() && tweet.getPublicMetrics().getRetweetCount() > 0) {
//            return false;
//        }

        if (!deleteOptions.hasRetweets() && tweet.getPublicMetrics().getRetweetCount() == 0) {
            return false;
        }

        return true;
    }
}

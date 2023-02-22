/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.aquinoa.tweetDelete;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.TweetsApi.APIusersIdTimelineRequest;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.Tweet;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Set;
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
        var timelineResponse = getTimelineRequest().execute();
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

    public boolean shouldDelete(Tweet tweet) {
        if (!deleteOptions.isNormal() && IsNormal(tweet)) {
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

    private boolean IsNormal(Tweet tweet) {
        var entities = tweet.getEntities();
        if (entities == null) {
            return true;
        }

        var mentions = entities.getMentions();
        if (mentions == null || mentions.isEmpty()) {
            return true;
        }

        return false;
    }
    
//attachments, author_id, context_annotations, conversation_id, created_at, 
//edit_controls, edit_history_tweet_ids, entities, geo, id, in_reply_to_user_id,
//lang, non_public_metrics, organic_metrics, possibly_sensitive, 
//promoted_metrics, public_metrics, referenced_tweets, reply_settings,
//source, text, withheld
    private APIusersIdTimelineRequest getTimelineRequest() {
        return twitterApi
            .tweets()
            .usersIdTimeline(deleteOptions.getUserId())
            .tweetFields(Set.of(
                    "id",
                    "created_at", 
                    "in_reply_to_user_id",
                    "referenced_tweets"));
    }
}

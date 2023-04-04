/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.aquinoa.tweetDelete;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TweetsApi.APIusersIdTweetsRequest;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.Tweet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author alex
 */
public class TweetDeleter {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LAST_ID_FILENAME = "lastId";

    private final DeleteOptions deleteOptions;
    private final TwitterApi twitterApi;

    public TweetDeleter(final DeleteOptions deleteOptions, final TwitterApi twitterApi) {
        this.deleteOptions = deleteOptions;
        this.twitterApi = twitterApi;
    }

    public void delete() throws ApiException {
        String untilId = null;
        String firstId = null;
        while (true) {
            var tweetsRequest = getTweetsRequest(untilId);

            var tweetsResponse = tweetsRequest.execute();
            var tweets = tweetsResponse.getData();

            if (tweets == null) {
                break;
            }

            LOGGER.log(Level.INFO, String.format("Got %d tweets.", tweets.size()));
            delete(tweets);

            if (firstId == null) {
                firstId = tweets.get(0).getId();
            }

            var lastTweet = tweets.get(tweets.size() - 1);
            untilId = lastTweet.getId();
        }

        updateLastId(firstId);
        LOGGER.log(Level.INFO, "Finished Deleting.");
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

        if (deleteOptions.getLikes() > -1
                && tweet.getPublicMetrics().getLikeCount() >= deleteOptions.getLikes()) {
            return false;
        }

        if (!deleteOptions.getExceptions().isEmpty()
                && tweet.getEntities() != null) {
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
        if (!deleteOptions.hasRetweets() && tweet.getPublicMetrics().getRetweetCount() > 0) {
            return false;
        }

        if (!deleteOptions.hasReplies() && tweet.getPublicMetrics().getReplyCount() > 0) {
            return false;
        }

        return true;
    }

    private void delete(List<Tweet> tweets) throws ApiException {
        for (Tweet tweet : tweets) {
            if (shouldDelete(tweet)) {
                var msg = String.format("Deleting tweet: %s, %s", tweet.getText(), tweet.getCreatedAt());
                LOGGER.log(Level.INFO, msg);
                var deleteResponse = twitterApi
                        .tweets()
                        .deleteTweetById(tweet.getId())
                        .execute();
            }
        }
    }

    private boolean IsNormal(Tweet tweet) {
        if (tweet.getInReplyToUserId() != null) {
            return false;
        }
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
    private APIusersIdTweetsRequest getTweetsRequest(String untilId) {
        var fromTime = getFromTime();

        var sinceId = getLastId();

        var request = twitterApi
                .tweets()
                .usersIdTweets(deleteOptions.getUserId())
                .tweetFields(Set.of(
                        "id",
                        "created_at",
                        "entities",
                        "text",
                        "public_metrics",
                        "in_reply_to_user_id",
                        "referenced_tweets"))
                .maxResults(100)
                .sinceId(sinceId)
                .endTime(fromTime)
                .untilId(untilId);

        return request;
    }

    private void updateLastId(String lastId) {
        if (lastId == null) {
            return;
        }

        writeValue(Path.of(LAST_ID_FILENAME), lastId);
        LOGGER.info(String.format("Updated last tweet id: %s", lastId));
    }

    private String getLastId() {
        return readValue(Path.of(LAST_ID_FILENAME));
    }

    private String readValue(Path path) {
        try {
            if (Files.exists(path)) {
                return Files.readString(path);
            }
        } catch (IOException ex) {
            LOGGER.error(String.format("Failed to read: %s", path.toString()), ex);
        }

        return null;
    }

    private void writeValue(Path path, String value) {
        try {
            Files.writeString(path, value, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            LOGGER.error(String.format("Failed to write value (%s) to: %s", value, path.toString()), ex);
        }
    }

    private OffsetDateTime getFromTime() {
        return OffsetDateTime.now()
                .minusDays(deleteOptions.getAge())
                .truncatedTo(ChronoUnit.MINUTES);
    }
}

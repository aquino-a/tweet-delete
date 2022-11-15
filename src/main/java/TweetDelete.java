import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetDelete {
    static int MS_DAY = 86400000;

    public static void main(String[] args) {
        // check valid args
        if (args.length < 6 || !args[5].matches("\\d+")) {
            System.out.println(
                    "Invalid arguments - Call the program using {KEY} {SECRET} {TOKEN} {TOKEN_SECRET} {Time period to delete tweets older than (in days)} {Type of engagements to remove (tweets, likes, retweets)}\nFor example: \"TweetDelete.java 7 -likes - retweets\" to remove likes and retweets older than 7 days.");
            System.exit(0);
        }
        for (int i = 6; i < args.length; i++) {
            if (!args[i].matches("-likes|-retweets|-tweets")) {
                System.out.println(
                        "Invalid arguments - Call the program using {KEY} {SECRET} {TOKEN} {TOKEN_SECRET} {Time period to delete tweets older than (in days)} {Type of engagements to remove (tweets, likes, retweets)}\nFor example: \"TweetDelete.java 7 -likes - retweets\" to remove likes and retweets older than 7 days.");
                System.exit(0);
            }
        }

        // parse args
        int time = Integer.parseInt(args[5]) * MS_DAY;
        boolean likes = args.toString().contains("-likes");
        boolean tweets = args.toString().contains("-tweets");
        boolean retweets = args.toString().contains("-retweets");

        // construct twitter instance
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(args[1]).setOAuthConsumerSecret(args[2])
                .setOAuthAccessToken(args[3]).setOAuthAccessTokenSecret(args[4]);

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        // fetch the timeline
        ResponseList<Status> timeLine = null;
        try {
            if (tweets || retweets) {
                timeLine = twitter.getUserTimeline();
            }
            if (likes) {
                timeLine.addAll(twitter.getFavorites());
            }
        } catch (TwitterException e) {
            System.out.println("couldn't fetch timeline" + e.toString());
        }

        // if there are no posts we dont need to do anything
        if (timeLine == null) {
            System.exit(0);
        }

        // check each post
        for (Status tweet : timeLine) {
            // ignore the post if it is within the time period
            if (System.currentTimeMillis() - tweet.getCreatedAt().getTime() < time) {
                continue;
            }
            try {
                // deleting a tweet
                if (tweets && tweet.getUser().getId() == twitter.getId()) {
                    System.out.println("Deleting tweet: " + tweet.getText());
                    twitter.destroyStatus(tweet.getId());
                    continue; // no need to unlike or unretweet the tweet if its gone
                } 
                // deleting a retweet
                if (retweets && tweet.isRetweetedByMe()) {
                    System.out.println("Deleting retweet: " + tweet.getText());
                    twitter.destroyStatus(tweet.getCurrentUserRetweetId());
                }
                // deleting a like
                if (likes && tweet.isFavorited()) {
                    System.out.println("Deleting favorite: " + tweet.getText());
                    twitter.destroyFavorite(tweet.getId());
                }
            } catch (TwitterException e) {
                System.out.println("couldn't destroy status" + e.toString());
            }
        }
    }
}

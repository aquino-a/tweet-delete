/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.aquinoa.tweetDelete;

import com.twitter.clientlib.model.Tweet;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 *
 * @author alex
 */
@RunWith(Theories.class)
public class TweetDeleterTest {
    
    /**
     * Tweets to test shouldDelete.
     */
    @DataPoints
    public static final List<Tweet> TWEETS =
        List.of(
                new Tweet()
        );
    
    @DataPoints
    public static final List<ShouldDeleteTestArgument> SHOULD_DELETE_TEST_ARGUMENTS =
        List.of(
                new ShouldDeleteTestArgument(
                        new DeleteOptions.Builder().build(),
                        false)
        );
    
    /**
     * Tests that {@link TweetDeleter#shouldDelete()} return correct answer.
     *
     * @param tweet test tweet.
     * @param deleteTest the delete options and correct answer.
     */
    @Theory
    @SuppressWarnings("checkstyle:methodname")
    public void shouldDelete_hasCorrectAnswer(
            Tweet tweet, 
            ShouldDeleteTestArgument deleteTest) {
        var deleter = new TweetDeleter(deleteTest.deleteOptions(), null);
        var answer = deleter.shouldDelete(tweet);
        
        assertThat(
            "The answer should match",
            answer,
            is(deleteTest.shouldDelete())
        );
    }
    
    /**
     * Helper class for testing tweets.
     */
    private static final class ShouldDeleteTestArgument {

        private final DeleteOptions deleteOptions;
        private final boolean shouldDelete;

        private ShouldDeleteTestArgument(DeleteOptions deleteOptions, boolean shouldDelete) {
            this.deleteOptions = deleteOptions;
            this.shouldDelete = shouldDelete;
        }

        public DeleteOptions deleteOptions() {
            return deleteOptions;
        }

        public boolean shouldDelete() {
            return shouldDelete;
        }
    }
}

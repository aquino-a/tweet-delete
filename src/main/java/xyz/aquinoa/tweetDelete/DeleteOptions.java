/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.aquinoa.tweetDelete;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alex
 */
public class DeleteOptions {
    private final int age;
    private final int likes;
    private final List<String> exceptions;
    private final boolean isRetweet;
    private final boolean isReply;
    private final boolean isPinned;
    private final boolean hasRetweets;
    private final boolean isNormal;

    private DeleteOptions(int age, int likes, List<String> exceptions, boolean isRetweet,
                               boolean isReply, boolean isPinned, boolean hasRetweets, boolean isNormal) {
        this.age = age;
        this.likes = likes;
        this.exceptions = exceptions;
        this.isRetweet = isRetweet;
        this.isReply = isReply;
        this.isPinned = isPinned;
        this.hasRetweets = hasRetweets;
        this.isNormal = isNormal;
    }

    public int getAge() {
        return age;
    }

    public int getLikes() {
        return likes;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    public boolean isRetweet() {
        return isRetweet;
    }

    public boolean isReply() {
        return isReply;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public boolean hasRetweets() {
        return hasRetweets;
    }

    public boolean isNormal() {
        return isNormal;
    }

    public static class Builder {
        private int age = 0;
        private int likes = 0;
        private List<String> exceptions = new ArrayList<>();
        private boolean isRetweet = false;
        private boolean isReply = false;
        private boolean isPinned = false;
        private boolean hasRetweets = false;
        private boolean isNormal = false;

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder likes(int likes) {
            this.likes = likes;
            return this;
        }

        public Builder exceptions(List<String> exceptions) {
            this.exceptions = exceptions;
            return this;
        }

        public Builder isRetweet(boolean isRetweet) {
            this.isRetweet = isRetweet;
            return this;
        }

        public Builder isReply(boolean isReply) {
            this.isReply = isReply;
            return this;
        }

        public Builder isPinned(boolean isPinned) {
            this.isPinned = isPinned;
            return this;
        }

        public Builder hasRetweets(boolean hasRetweets) {
            this.hasRetweets = hasRetweets;
            return this;
        }

        public Builder isNormal(boolean isNormal) {
            this.isNormal = isNormal;
            return this;
        }

        public DeleteOptions build() {
            return new DeleteOptions(age, likes, exceptions, isRetweet, isReply, isPinned, hasRetweets, isNormal);
        }
    }
}


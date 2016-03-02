package models;

import com.mongodb.DBObject;

import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.social.facebook.api.Comment;
import org.springframework.social.facebook.api.StoryAttachment;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import controllers.InstagramCollector;
import play.Logger;

/**
 * Created by alvaro.joao.silvino on 20/08/2015.
 */

@Document
public class Post {

    @Id
    private String id;

    private String link;
    private String name;
    private String from;
    private String message;

    @Indexed
    private int likesCount;
    @Indexed
    private int commentsCount;
    private int shareCount;
    private String api;
    private Date createdTime;
    private Date updatedTime;
    private String fromName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getFromName() {
        return this.fromName;
    }
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
}
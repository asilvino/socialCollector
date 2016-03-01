package models;

import com.mongodb.DBObject;

import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.social.facebook.api.Comment;
import org.springframework.social.facebook.api.Post;
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
public class User {

    @Id
    private String id;

    private  String name;
    private  String username;
    private  String api;
    private  String profilePic;

    public  Set<Like> likes  = new HashSet<>();

    @Indexed
    private int likesCount;

    public  Set<Comment> comments  = new HashSet<>();
    
    @Indexed
    private int commentsCount;

    private  Set<Page> pages  = new HashSet<>();
    
    private int pagesCount;

    public User(){
        likes = new HashSet<>();
        comments = new HashSet<>();
        pages = new HashSet<>();
    }

    public User(String id,String name){
        this.id = id;
        this.name = name;
        likes = new HashSet<>();
        comments = new HashSet<>();
        pages = new HashSet<>();
    }
    public User(String id,String name,Page page){
        this.id = id;
        this.name = name;
        this.api = page.getApi();
        likes = new HashSet<>();
        comments = new HashSet<>();
        pages = new HashSet<>();
        pages.add(page);
    }
    public User(String id,String name,String username,String profilePic,Page page){
        this.id = id;
        this.name = name;
        this.username = username;
        this.profilePic = profilePic;
        this.api = page.getApi();
        likes = new HashSet<>();
        comments = new HashSet<>();
        pages = new HashSet<>();
        pages.add(page);
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public int getPagesCount() {
        return pagesCount;
    }

    public void setPagesCount(int pagesCount) {
        this.pagesCount = pagesCount;
    }

    public void addComment(org.springframework.social.facebook.api.Comment commentFacebook,Post post,String pageId){
        Comment comment = new Comment();
        comment.commentId = commentFacebook.getId();
        comment.message = commentFacebook.getMessage();
        comment.likeCount = commentFacebook.getLikeCount()!=null?commentFacebook.getLikeCount().intValue():0;
        comment.attachment = commentFacebook.getAttachment();
        comment.postId = post.getId();
        comment.createdDate = commentFacebook.getCreatedTime();
        comment.pageId = pageId;
        this.comments.add(comment);
    }
    public void addComment(CommentData commentInstagram, MediaFeedData post, String pageId) {
        Comment comment = new Comment();
        comment.commentId = commentInstagram.getId();
        comment.message = commentInstagram.getText();
        comment.postId = post.getId();
        comment.createdDate = new Date(Long.parseLong(commentInstagram.getCreatedTime())*1000);
        comment.pageId = pageId;
        comment.link = post.getLink();
        comment.imageLink =  post.getImages().getStandardResolution().getImageUrl();
        this.comments.add(comment);
    }

    public void addLike(Post post,String pageId){
        Like like = new Like();
        like.postId = post.getId();
        like.postMessage = post.getMessage();
        like.createdDate = post.getCreatedTime();
        like.updatedDate = post.getUpdatedTime();
        like.pageId = pageId;
        this.likes.add(like);
    }
    public void addLike(MediaFeedData post, String pageId) {
        Like like = new Like();
        like.postId = post.getId();
        like.createdDate = new Date(Long.parseLong(post.getCreatedTime())*1000);
        like.pageId = pageId;
        like.imageLink =  post.getImages().getStandardResolution().getImageUrl();
        like.link = post.getLink();
        this.likes.add(like);
    }
    public String getName(){
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }


    public Set<Like> getLikes(){
        return likes;
    }

    public void setLikes(Set<Like> likes) {
        this.likes = likes;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Page> getPages() {
        return pages;
    } 

    public void setPages(Set<Page> pages) {
        this.pages = pages;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.id.equals(((User)obj).id));
    }

    @Override
    public int hashCode() {
        return (this.id).hashCode();
    }
    @Override
    public String toString(){
        return this.id;
    }




    public class Like{
        public String postId;
        public String pageId;
        public String link;
        public String imageLink;
        public String postMessage;
        public Date createdDate;
        public Date updatedDate;
        public Like(){
        }

        @Override
        public boolean equals(Object obj) {
            return (this.pageId.equals(((Like)obj).pageId))
                    &&(this.postId.equals(((Like)obj).postId));
        }

        @Override
        public int hashCode() {
            return (this.pageId+this.postId).hashCode();
        }
    }
    public class Comment{
        public String postId;
        public String commentId;
        public String message;
        public int likeCount;
        public String link;
        public String imageLink;
        public String pageId;
        public Date createdDate;
        public StoryAttachment attachment;

        public Comment(){

        }
        @Override
        public boolean equals(Object obj) {
           return (this.commentId.equals(((Comment)obj).commentId))
                   &&(this.postId.equals(((Comment)obj).postId))
                   &&(this.pageId.equals(((Comment)obj).pageId));
        }

        @Override
        public int hashCode() {
            return (this.commentId+this.postId+pageId).hashCode();
        }
    }
}


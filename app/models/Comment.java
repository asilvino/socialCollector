package models;

import com.mongodb.DBObject;

import org.jinstagram.entity.users.feed.MediaFeedData;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.social.facebook.api.StoryAttachment;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import controllers.InstagramCollector;
import play.Logger;

/**
 * Created by alvaro.joao.silvino on 20/08/2015.
 */

@Document
public class Comment {

    @Id
    private String id;

    private String postId;
    private Page page;

    private Set<MessageTag> messageTags;
    private String message;
    private int likeCount;
    private int commentCount;
    private String commentParentId;
    private Date createdTime;
    private User from;
    private Boolean canComment;
    private Boolean canRemove;
    private Boolean userLikes;
    private Map<String,Object> extraData;

    public Comment(){
        messageTags = new HashSet<>();
        page = new Page();
    }
    public Comment(org.springframework.social.facebook.api.Comment comment,Page page,String postId){
        this.setId(comment.getId());
        this.setCanComment( comment.canComment() );
        this.setCanRemove(comment.canRemove());
        this.setCreatedTime( comment.getCreatedTime() );



        User user = new User();
        if(comment.getFrom()!=null){
            user.setId(comment.getFrom().getId());
            user.setName(comment.getFrom().getName());
        }
        this.setFrom(user);
        if(comment.getLikeCount()!=null){
            this.setLikeCount(  comment.getLikeCount() );
        }
        this.setMessage( comment.getMessage() );


        if(comment.getCommentCount()!=null){
            this.setCommentCount( comment.getCommentCount() );
        }

        if(comment.getParent()!=null){
            this.setCommentParentId( comment.getParent().getId());
        }
        this.setUserLikes(comment.userLikes() );

        this.messageTags = new HashSet<>();
        this.setPage(page);

        if(comment.getMessageTags()!=null){
            for ( int i= 0 ;i<comment.getMessageTags().size() ;i++){
                this.messageTags.add(new MessageTag(comment.getMessageTags().get(i)));
            }
        }

        this.setPostId(postId);
        this.setExtraData(comment.getExtraData());
        
    }

    public Map<String,Object> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String,Object> extraData) {
        this.extraData = extraData;
    }

    public Boolean isUserLikes() {
        return userLikes;
    }

    public void setUserLikes(Boolean userLikes) {
        this.userLikes = userLikes;
    }
    

     public Boolean iscCanRemove() {
        return canRemove;
    }

    public void setCanRemove(Boolean canRemove) {
        this.canRemove = canRemove;
    }

    public Boolean isCanComment() {
        return canComment;
    }

    public void setCanComment(Boolean canComment) {
        this.canComment = canComment;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Set<MessageTag> getMessageTags() {
        return messageTags;
    }

    public void setMessageTags(Set<MessageTag> messageTags) {
        this.messageTags = messageTags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getCommentParentId() {
        return commentParentId;
    }

    public void setCommentParentId(String commentParentId) {
        this.commentParentId = commentParentId;
    }

    
    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
    @Override
    public String toString(){
        return (this.id + " from:"+this.from.toString() + " post: "+ this.postId + " page "+this.page.toString());
    }

    public class MessageTag{

        public String id;
        public int length;
        public String name;
        public int offset;
        public String type;
        public MessageTag(org.springframework.social.facebook.api.MessageTag messageTag){
            this.id = messageTag.getId();
            this.length = messageTag.getLength();
            this.offset = messageTag.getOffset();
            this.name = messageTag.getName();
            this.type = messageTag.getType();
        }


        @Override
        public boolean equals(Object obj) {
            return (this.id.equals(((MessageTag)obj).id));
        }

        @Override
        public int hashCode() {
            return (this.id).hashCode();
        }
    }
}
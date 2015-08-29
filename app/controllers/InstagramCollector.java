package controllers;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.comments.MediaCommentsFeed;
import org.jinstagram.entity.common.Likes;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.joda.time.DateTime;
import org.springframework.social.facebook.api.Comment;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.impl.FacebookTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bootstrap.CollectorInfo;
import models.Page;
import models.User;
import play.Logger;
import play.Play;
import play.libs.ws.WS;
import service.MongoService;

/**
 * Created by alvaro.joao.silvino on 20/08/2015.
 */
//db.user.find({$where:'this.comments.length>4'}).pretty()
//> db.user.find({$and:[{$where:'this.comments.length>4'},{$where:'this.likes.length>4'}]}).pretty()
public class InstagramCollector {



    public static String clientId = Play.application().configuration().getString("instagram.clientid");
    public static String secret = Play.application().configuration().getString("instagram.secret");


    public static void collect(CollectorInfo.Moment moment,String token){
        Token tokenObject = new Token(token,secret);
        Instagram instagram = new Instagram(tokenObject);

        for(Page page:MongoService.getAllPagesInstagram()) {
            try {

            MediaFeed posts ;
            switch (moment){
                case ALL:
                    posts = instagram.getRecentMediaFeed(page.getId());
                    break;
                case RECENT:
                    posts = instagram.getRecentMediaFeed(page.getId(),25,null,null,DateTime.now().minusMonths(1).toDate(),DateTime.now().toDate());
                    break;
                default:
                    posts = instagram.getRecentMediaFeed(page.getId());
                    break;
            }

            boolean firstTime = true;
            do{
                try{
                    if(!firstTime)
                        posts = instagram.getRecentMediaNextPage(posts.getPagination());
                    firstTime = false;
                    for(MediaFeedData post: posts.getData()) {
                        Set<User> users = new HashSet<>();
                        Set<CommentData> comments = new HashSet<>();
                        fetchCommentAndUpdateUsers(post, comments, users, page,instagram);
                        fetchLikesAndUpdateUsers(post, users,page);

                        MongoService.save(post);

                        for(CommentData comment: comments){
                            MongoService.save(comment);
                        }
                        //save or update users iterations
                        MongoService.save(users);
                    }
                }catch (Exception e){
                    Logger.debug("error on get more  posts: "+e.getMessage() );
                }
                Logger.debug("update:"+page.getTitle()+"  " );
            }while(posts.getPagination().hasNextPage());
            Logger.debug("Finished page:"+page.getTitle());

            }catch (Exception e){
                Logger.debug("error on page:"+page.getTitle());
            }
        }
    }

    private static void fetchLikesAndUpdateUsers(MediaFeedData post, Set<User> users,Page page) {
        Likes likes = post.getLikes();
        for(org.jinstagram.entity.common.User userLike: likes.getLikesUserList()) {
            User user = users.stream().filter(f->f.getId().equals(userLike.getId())).findFirst().orElse(new User(userLike.getId(),userLike.getFullName(),userLike.getUserName(),page));
            user.addLike(post,page.getId());
            users.add(user);
        }
//        post.getExtraData().putIfAbsent("likesCount",totalLikes);
    }

    private static void fetchCommentAndUpdateUsers(MediaFeedData post,Set<CommentData> commentsToSave, Set<User> users,Page page,Instagram instagram) {
       try {
           MediaCommentsFeed comments = instagram.getMediaComments(post.getId());
           int totalComments = post.getComments().getCount();

           for (CommentData comment : post.getComments().getComments()) {

               User user = users.stream().filter(f -> f.getId().equals(comment.getCommentFrom().getId())).findFirst().orElse(new User(comment.getCommentFrom().getId(), comment.getCommentFrom().getFullName(),comment.getCommentFrom().getUsername(), page));
               user.addComment(comment, post, page.getId());
               users.add(user);

               commentsToSave.add(comment);
           }
       }catch (Exception e){

       }
        //post.getExtraData().putIfAbsent("commentsCount",totalComments);

    }

}

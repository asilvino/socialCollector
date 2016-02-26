package service;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.jinstagram.entity.comments.CommentData;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import  org.springframework.data.mongodb.core.script.*;
import controllers.InstagramCollector;
import play.Logger;

import bootstrap.DS;
import models.*;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.social.facebook.api.Post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Comparator;

import java.util.Map;
import java.util.Map.*;

import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
/**
 * Created by alvaro.joao.silvino on 20/08/2015.
 */
public class MongoService {
    final static String stopWords = "1 2 3 4 5 6 7 8 9 0 de a o que e do da em um para é com não uma os no se na por mais as dos como mas foi ao ele das tem à seu sua ou ser quando muito há nos já está eu também só pelo pela até isso ela entre era depois sem mesmo aos ter seus quem nas me esse eles estão você tinha foram essa num nem suas meu às minha têm numa pelos elas havia seja qual será nós tenho lhe deles essas esses pelas este fosse dele tu te vocês vos lhes meus minhas teu tua teus tuas nosso nossa nossos nossas dela delas esta estes estas aquele aquela aqueles aquelas isto aquilo estou está estamos estão estive esteve estivemos estiveram estava estávamos estavam estivera estivéramos esteja estejamos estejam estivesse estivéssemos estivessem estiver estivermos estiverem hei há havemos hão houve houvemos houveram houvera houvéramos haja hajamos hajam houvesse houvéssemos houvessem houver houvermos houverem houverei houverá houveremos houverão houveria houveríamos houveriam sou somos são era éramos eram fui foi fomos foram fora fôramos seja sejamos sejam fosse fôssemos fossem for formos forem serei será seremos serão seria seríamos seriam tenho tem temos tém tinha tínhamos tinham tive teve tivemos tiveram tivera tivéramos tenha tenhamos tenham tivesse tivéssemos tivessem tiver tivermos tiverem terei terá teremos terão teria teríamos teriam para ;) pra aqui lá";

    public static List<Page> getAllPages(){
        List<Page> pages = DS.mop.findAll(Page.class);
        return pages;
    }
    public static List<Page> getAllPagesFacebook(){
        Query query = new Query();
        query.addCriteria(Criteria.where("api").is(Utils.FacebookPages.class.getName()));

        List<Page> pages = DS.mop.find(query, Page.class);
        return pages;
    }
    public static List<Page> getAllPagesInstagram(){
        Query query = new Query();
        query.addCriteria(Criteria.where("api").is(Utils.InstagramPages.class.getName()));
        List<Page> pages = DS.mop.find(query,Page.class);
        return pages;
    }

    public static User getUserById(String id){
        return DS.mop.findById(id,User.class);
    }


    public static boolean save(Set<User> users){
        try{
            List<String> ids = users.stream().map(f->f.getId()).collect(Collectors.toList());
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").in(ids));
            List<User> usersFromDb = DS.mop.find(query,User.class);
            for(User user: users){
                User userFromDb = usersFromDb.stream().filter(f->f.getId().equals(user.getId())).findFirst().orElse(null);
                if(userFromDb!=null){
                    userFromDb.getComments().addAll(user.getComments());
                    userFromDb.getLikes().addAll(user.getLikes());
                    userFromDb.getPages().addAll(user.getPages());
                    DS.mop.save(userFromDb);
                }else{

                    DS.mop.save(user);
                }
            }
        }catch (Exception e){
            Logger.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public static boolean save(org.springframework.social.facebook.api.Comment comment,Page page,String postId){
        try{
            Comment commentModel = new Comment(comment,page,postId);
            DS.mop.save(commentModel);
        }catch (Exception e){
            Logger.error("Error in save Comment:" + e.getMessage());
            return false;
        }
        return true;
    }
    public static boolean save(CommentData comment){
        try{
            DS.mop.save(comment);
        }catch (Exception e){
            Logger.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }
    public static boolean save(Post post){
        try{
            models.Post postConvert = new models.Post();
            postConvert.setApi(Utils.FacebookPages.class.getName());
            postConvert.setCommentsCount((Integer) post.getExtraData().getOrDefault("commentsCount", 0));
            postConvert.setCreatedTime(post.getCreatedTime());
            postConvert.setFrom(post.getFrom().getId());
            postConvert.setFromName(post.getFrom().getName());
            postConvert.setId(post.getId());
            postConvert.setLikesCount((Integer) post.getExtraData().getOrDefault("likesCount", 0));
            postConvert.setLink(post.getLink());
            postConvert.setMessage(post.getMessage());
            postConvert.setName(post.getName());
            postConvert.setShareCount(post.getShares());
            postConvert.setUpdatedTime(post.getUpdatedTime());

            DS.mop.save(postConvert);
        }catch (Exception e){
            Logger.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }
    
    public static boolean save(User user){
        try{
            DS.mop.save(user);
        }catch (Exception e){
            Logger.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public static boolean save(Page page){
        try{
            DS.mop.save(page);
        }catch (Exception e){
            Logger.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }
    public static boolean deletePageById(String id){
        try{
            DS.mop.remove(findPageById(id));
        }catch (Exception e){
            Logger.error(e.getLocalizedMessage());
            return false;
        }
        return true;

    }
    public static Page findPageById(String id){
        try{
            return DS.mop.findById(id,Page.class);
        }catch (Exception e){
            Logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    public static boolean save(MediaFeedData post) {
        try{
            models.Post postConvert = new models.Post();
            postConvert.setApi(Utils.InstagramPages.class.getName());
            postConvert.setCommentsCount(post.getComments().getCount());
            postConvert.setCreatedTime(new Date(Long.parseLong(post.getCreatedTime()) * 1000));
            postConvert.setFrom(post.getCaption().getFrom().getId());
            postConvert.setFromName(post.getCaption().getFrom().getUsername());
            postConvert.setId(post.getId());
            postConvert.setLikesCount(post.getLikes().getCount());
            postConvert.setLink(post.getLink());
            postConvert.setMessage(post.getCaption().getText());
            postConvert.setName(post.getTags().toString());
            postConvert.setUpdatedTime(new Date(Long.parseLong(post.getCreatedTime()) * 1000));

            DS.mop.save(postConvert);
        }catch (Exception e){
            Logger.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public enum OrderBy{
        likesCount,commentsCount;
    }
    public enum Api{
        facebook,instagram,none;
    }


    public static List<String> getPostByKeyword(String[] keyword){
        TextCriteria criteria = TextCriteria.forDefaultLanguage()
                .matchingAny(keyword);
        Query query = TextQuery.queryText(criteria)
                .sortByScore();
        query.fields().include("_id");
        return DS.mop.find(query,DBObject.class,"post").stream().map(f->(String)f.get("_id")).collect(Collectors.toList());
    }

    public static List<User> getUsers(int page,Api api, Sort.Direction direction, OrderBy order,List<String> pages,DateTime initDateTime,DateTime endDateTime,String[] searchPost,String name) {
        Query query = new Query();
        query.limit(25);
        query.skip((page - 1) * 25);
        if (pages != null) {
            List<String> pagesTitle = new ArrayList<>();
            for (String id : pages) {
                try {
                    pagesTitle.add(Utils.FacebookPages.getById(id).name());
                } catch (Exception e) {

                }
            }
            query.addCriteria(Criteria.where("pages.title").all(pagesTitle));
        }
        if(name != null){
            query.addCriteria(Criteria.where("name").regex(name));
        }
        switch (api){
            case facebook:
                query.addCriteria(Criteria.where("pages.api").is(Utils.FacebookPages.class.getName()));
                break;
            case instagram:
                query.addCriteria(Criteria.where("pages.api").is(Utils.InstagramPages.class.getName()));
                break;
            default:
                break;
        }

        if(initDateTime!=null&&endDateTime!=null&&searchPost!=null) {
            List<String> postIds = getPostByKeyword(searchPost);
            query.addCriteria(Criteria.where(null).andOperator(
                    Criteria.where(null).orOperator(Criteria.where("likes.postId").in(postIds),(Criteria.where("comments.postId").in(postIds))),
                    Criteria.where(null).orOperator(Criteria.where("likes.createdDate").lte(endDateTime.toDate()).gte(initDateTime.toDate()),
                    Criteria.where("comments.createdDate").lte(endDateTime.toDate()).gte(initDateTime.toDate()))));
        }else{
            if(initDateTime!=null&&endDateTime!=null){
                query.addCriteria(Criteria.where(null).orOperator(Criteria.where("likes.createdDate").lte(endDateTime.toDate()).gte(initDateTime.toDate()),
                        Criteria.where("comments.createdDate").lte(endDateTime.toDate()).gte(initDateTime.toDate())));
            }
            if(searchPost!=null){
                List<String> postIds = getPostByKeyword(searchPost);
                query.addCriteria(Criteria.where(null).orOperator(Criteria.where("likes.postId").in(postIds),(Criteria.where("comments.postId").in(postIds))));
            }
        }
        query.with(new Sort(direction,order.name()));
        query.fields().exclude("likes");
        return DS.mop.find(query,User.class);
    }

    public static long countUsers(Api api, Sort.Direction direction, OrderBy order,List<String> pages,DateTime initDateTime,DateTime endDateTime,String[] searchPost,String name){
        Query query = new Query();
        
        if(pages!=null){
            List<String> pagesTitle = new ArrayList<>();
            for (String id : pages) {
                try{
                    pagesTitle.add(Utils.FacebookPages.getById(id).name());
                }catch (Exception e){

                }
            }
            query.addCriteria(Criteria.where("pages.title").all(pagesTitle));
        }
        if(name != null){
            query.addCriteria(Criteria.where("name").regex(name));
        }
        switch (api){
            case facebook:
                query.addCriteria(Criteria.where("pages.api").is(Utils.FacebookPages.class.getName()));
                break;
            case instagram:
                query.addCriteria(Criteria.where("pages.api").is(Utils.InstagramPages.class.getName()));
                break;
            default:
                break;
        }

        if(initDateTime!=null&&endDateTime!=null&&searchPost!=null) {
            List<String> postIds = getPostByKeyword(searchPost);
            query.addCriteria(Criteria.where(null).andOperator(
                    Criteria.where(null).orOperator(Criteria.where("likes.postId").in(postIds),(Criteria.where("comments.postId").in(postIds))),
                    Criteria.where(null).orOperator(Criteria.where("likes.createdDate").lte(endDateTime.toDate()).gte(initDateTime.toDate()),
                            Criteria.where("comments.createdDate").lte(endDateTime.toDate()).gte(initDateTime.toDate()))));
        }else{
            if(initDateTime!=null&&endDateTime!=null){
                query.addCriteria(Criteria.where(null).orOperator(Criteria.where("likes.createdDate").lte(endDateTime.toDate()).gte(initDateTime.toDate()),
                        Criteria.where("comments.createdDate").lte(endDateTime.toDate()).gte(initDateTime.toDate())));
            }
            if(searchPost!=null){
                List<String> postIds = getPostByKeyword(searchPost);
                query.addCriteria(Criteria.where(null).orOperator(Criteria.where("likes.postId").in(postIds),(Criteria.where("comments.postId").in(postIds))));
            }
        }
        query.with(new Sort(direction,order.name()));
        query.fields().exclude("likes");
        return DS.mop.count(query,User.class);
    }

    public static List<DBObject> getPosts(int page, Sort.Direction direction, OrderBy order,List<String> pages,DateTime initDateTime,DateTime endDateTime,String[] keyword) {
        Query query = new Query();
        if(keyword!=null){
            TextCriteria criteria = TextCriteria.forDefaultLanguage()
                .matchingAny(keyword);
            query = TextQuery.queryText(criteria)
                .sortByScore();
        }

        query.limit(25);
        query.skip((page - 1) * 25);
        query.with(new Sort(direction, order.name()));

        if(pages!=null){
            
            query.addCriteria(Criteria.where("from").all(pages));
        }else{
            query.addCriteria(Criteria.where("from").in(Utils.FacebookPages.getListId()));
        }
        if(initDateTime!=null&&endDateTime!=null){
            query.addCriteria(Criteria.where("createdTime").lte(endDateTime.toDate()).gte(initDateTime.toDate()));
        }
        
        return DS.mop.find(query,DBObject.class,"post");
    }

    public static long countPosts(Sort.Direction direction, OrderBy order,List<String> pages,DateTime initDateTime,DateTime endDateTime,String[] keyword){
        Query query = new Query();
        if(keyword!=null){
            TextCriteria criteria = TextCriteria.forDefaultLanguage()
                    .matchingAny(keyword);
            query = TextQuery.queryText(criteria)
                    .sortByScore();
        }
        query.with(new Sort(direction,order.name()));
        if(pages!=null){
            
            query.addCriteria(Criteria.where("from").all(pages));
        }else{
            query.addCriteria(Criteria.where("from").in(Utils.FacebookPages.getListId()));
        }
        if(initDateTime!=null&&endDateTime!=null){
            query.addCriteria(Criteria.where("createdTime").lte(endDateTime.toDate()).gte(initDateTime.toDate()));
        }
        return DS.mop.count(query,Post.class);
    }
    
    public static Map<String, Float> countWordsPagesPosts(List<String> pages,DateTime initDateTime,DateTime endDateTime) {
        String containsObject = "function (obj, list) {    var i;    for (i = 0; i < list.length; i++) {        if (list[i] === obj) {            return true;        }    } return false;}";
        String mapFunction = "function() {      var message = this.message;    if (message) {   message = message.toLowerCase().split(' ');         for (var i = message.length - 1; i >= 0; i--) { if (message[i]&&stopWords.indexOf(message[i])<=-1)  {  emit(message[i], 1);           }        }    }}";
        String reduceFunction = "function( key, values ) {        var count = 0;        values.forEach(function(v) {                    count +=v;        });    return count;}";
        ExecutableMongoScript echoScript = new ExecutableMongoScript(containsObject);

        Map<String, Object> scopeVariables = new HashMap<String, Object>();
        scopeVariables.put("stopWords", stopWords);

        Query query = new Query();
        if(pages!=null){
            query.addCriteria(Criteria.where("from").all(pages));
        }else{
            query.addCriteria(Criteria.where("from").in(Utils.FacebookPages.getListId()));
        }
        if(initDateTime!=null&&endDateTime!=null){
            query.addCriteria(Criteria.where("createdTime").lte(endDateTime.toDate()).gte(initDateTime.toDate()));
        }

        MapReduceResults<ValueObject> results = DS.mop.mapReduce( query,"post",mapFunction, reduceFunction,
                new MapReduceOptions().scopeVariables(scopeVariables).outputTypeInline(), ValueObject.class);
        Map<String, Float> m = copyToMap(results);
        

        Map<String,Float> result = new LinkedHashMap<>();

         Stream <Entry<String,Float>> st = m.entrySet().stream();

         st.sorted(Comparator.comparing(e -> e.getValue()*-1))
              .forEachOrdered(e ->result.put(e.getKey(),e.getValue()));

        return result;
    }
    public static Map<String, Float> countWordsPagesComments(List<String> pages,DateTime initDateTime,DateTime endDateTime) {
        String containsObject = "function (obj, list) {    var i;    for (i = 0; i < list.length; i++) {        if (list[i] === obj) {            return true;        }    } return false;}";
        String mapFunction = "function() {      var message = this.message;    if (message) {   message = message.toLowerCase().split(' ');         for (var i = message.length - 1; i >= 0; i--) { if (message[i]&&stopWords.indexOf(message[i])<=-1)  {  emit(message[i], 1);           }        }    }}";
        String reduceFunction = "function( key, values ) {        var count = 0;        values.forEach(function(v) {                    count +=v;        });    return count;}";
        ExecutableMongoScript echoScript = new ExecutableMongoScript(containsObject);

        Map<String, Object> scopeVariables = new HashMap<String, Object>();
        scopeVariables.put("stopWords", stopWords);

        Query query = new Query();
        if(pages!=null){
            query.addCriteria(Criteria.where("page._id").all(pages));
        }else{
            query.addCriteria(Criteria.where("page._id").in(Utils.FacebookPages.getListId()));
        }
        if(initDateTime!=null&&endDateTime!=null){
            query.addCriteria(Criteria.where("createdTime").lte(endDateTime.toDate()).gte(initDateTime.toDate()));
        }

        MapReduceResults<ValueObject> results = DS.mop.mapReduce( query,"comment",mapFunction, reduceFunction,
                new MapReduceOptions().scopeVariables(scopeVariables).outputTypeInline(), ValueObject.class);
        Map<String, Float> m = copyToMap(results);
        

        Map<String,Float> result = new LinkedHashMap<>();

         Stream <Entry<String,Float>> st = m.entrySet().stream();

         st.sorted(Comparator.comparing(e -> e.getValue()*-1))
              .forEachOrdered(e ->result.put(e.getKey(),e.getValue()));

        return result;
    }

    public static Map<String, Float> countWordsUserComments(String idUser,DateTime initDateTime,DateTime endDateTime) {
        String containsObject = "function (obj, list) {    var i;    for (i = 0; i < list.length; i++) {        if (list[i] === obj) {            return true;        }    } return false;}";
        String mapFunction = "function() {   var comments = this.comments; comments.forEach(function(comment){ var message = comment.message; if (message) {   message = message.toLowerCase().split(' ');         for (var i = message.length - 1; i >= 0; i--) { if (message[i]&&stopWords.indexOf(message[i])<=-1)  {  emit(message[i], 1);           }        }    } }); }";
        String reduceFunction = "function( key, values ) {        var count = 0;        values.forEach(function(v) {                    count +=v;        });    return count;}";
        ExecutableMongoScript echoScript = new ExecutableMongoScript(containsObject);

        Map<String, Object> scopeVariables = new HashMap<String, Object>();
        scopeVariables.put("stopWords", stopWords);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(idUser));

        if(initDateTime!=null&&endDateTime!=null){
            query.addCriteria(Criteria.where("comments").elemMatch(Criteria.where("createdTime").lte(endDateTime.toDate()).and("createdTime").gte(initDateTime.toDate())));
        }

        MapReduceResults<ValueObject> results = DS.mop.mapReduce( query,"user",mapFunction, reduceFunction,
                new MapReduceOptions().scopeVariables(scopeVariables).outputTypeInline(), ValueObject.class);
        Map<String, Float> m = copyToMap(results);
        

        Map<String,Float> result = new LinkedHashMap<>();

         Stream <Entry<String,Float>> st = m.entrySet().stream();

         st.sorted(Comparator.comparing(e -> e.getValue()*-1))
              .forEachOrdered(e ->result.put(e.getKey(),e.getValue()));

        return result;
    }

    public static Map<String, Float> countWordsUserPosts(String idUser,DateTime initDateTime,DateTime endDateTime) {
        String containsObject = "function (obj, list) {    var i;    for (i = 0; i < list.length; i++) {        if (list[i] === obj) {            return true;        }    } return false;}";
        String mapFunction = "function() {   var likes = this.likes; likes.forEach(function(like){ var message = like.postMessage; if (message) {   message = message.toLowerCase().split(' ');         for (var i = message.length - 1; i >= 0; i--) { if (message[i]&&stopWords.indexOf(message[i])<=-1)  {  emit(message[i], 1);           }        }    } }); }";
        String reduceFunction = "function( key, values ) {        var count = 0;        values.forEach(function(v) {                    count +=v;        });    return count;}";
        ExecutableMongoScript echoScript = new ExecutableMongoScript(containsObject);

        Map<String, Object> scopeVariables = new HashMap<String, Object>();
        scopeVariables.put("stopWords", stopWords);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(idUser));
        if(initDateTime!=null&&endDateTime!=null){
            query.addCriteria(Criteria.where("likes").elemMatch(Criteria.where("createdTime").lte(endDateTime.toDate()).and("createdTime").gte(initDateTime.toDate())));
        }

        MapReduceResults<ValueObject> results = DS.mop.mapReduce( query,"user",mapFunction, reduceFunction,
                new MapReduceOptions().scopeVariables(scopeVariables).outputTypeInline(), ValueObject.class);
        Map<String, Float> m = copyToMap(results);

          Map<String,Float> result = new LinkedHashMap<>();

         Stream <Entry<String,Float>> st = m.entrySet().stream();

         st.sorted(Comparator.comparing(e -> e.getValue()*-1))
              .forEachOrdered(e ->result.put(e.getKey(),e.getValue()));

        return result;
    }

    private static Map<String, Float> copyToMap(MapReduceResults<ValueObject> results) {
        List<ValueObject> valueObjects = new ArrayList<ValueObject>();
        for (ValueObject valueObject : results) {
            valueObjects.add(valueObject);
        }

        Map<String, Float> m = new HashMap<String, Float>();
        for (ValueObject vo : valueObjects) {
            m.put(vo.getId(), vo.getValue());
        }
        return m;
    }
}
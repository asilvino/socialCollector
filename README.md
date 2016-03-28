# Social Media Collector [![Travis](https://img.shields.io/teamcity/codebetter/bt428.svg)](https://github.com/alvarojoao/socialCollector)  [![Travis](https://img.shields.io/badge/Heroku-deploy-green.svg)](https://heroku.com/) 

##For now, only fully working for Facebook.

This project uses **Spring Social Resource**, **Spring Mongodb** and **Play Framework** with **JAVA 8**.

For front-end : **AngularJs**, **RequireJs**

 
 This project will collect Users, Posts and Comments, Cross Users throw Pages, and Interest, for instance, you will be able to see what Pages/Accounts share commun Users or even that Users share interest.
 
 Running project [here](https://facebookcollector.herokuapp.com/#/)
 
 **The features:**
 
 - Collect User and Users' Interations
 - Evaluate Users' Interations, count Users likes, count Users comments
 - Filter Users' Interations by date, pages, key words
 - Evaluate Pages' Posts, count most liked posts, count most commented Posts
 - Filter Pages' Posts by date, pages, key words
 - Search for most cited words by Users and By Pages (new)
 
##Steps to Use

 1. Install [PlayFramework](https://www.playframework.com/documentation/2.5.x/Installing)
 2. Install [MongoDB](https://docs.mongodb.org/manual/installation/)
 3. Go to Root project (where **activator** file is located)

 4. Configure the file **socialCollector/app/models/Utils.java** and **socialCollector/conf/application.conf** as fallows:
 
**Configuring Social Page** -  FILE:**socialCollector/app/models/Utils.java**

*Add Instagram Accounts*

    public class Utils{
        public enum InstagramPages {
            chicorei("20133482"),
            camiseteriasa("374614102"),
            kanuibr("196502375");

*Add Facebook Pages*

     public enum FacebookPages {
          chicorei("162726143745402"),
          br4sileirissimos("404693309619224"),
          king55style("177679275612961"),
          mestredalma("160540370633021"),
          camiseteria("7018060973"),
          kanuibr("197061883680740");



**Configuring .conf (connect database, facebook token, etc)** FILE:**socialCollector/conf/application.conf**

All the `${?}` variables are from system envoriment, more [here](https://www.playframework.com/documentation/2.5.x/ProductionConfiguration).
You can remove than and add the string independently the OS or anyother variables. 

Add a new *security key*:

    play.crypto.secret = ${?SECRET}

If you want to use a **local database** change to:

    mongo.config = "bootstrap.MongoConf"
    
 And configure the *local* connection:
 
    connections {
       mongo{
         name = "social" #name of the database
         slave = false #slave or not
         servers = ["127.0.0.1:27017"] #local servers
       }
    }

However, if You want to configure the **production environment** scenario change to:

    mongo.config = "bootstrap.MongoConfig"
    
Add a Mongodb URI:

     mongo_uri = ${?MONGOLAB_URI}

Add a **Long-term (Long-life)** Facebook Token, more information [here] (https://developers.facebook.com/docs/facebook-login/access-tokens/expiration-and-extension)

     facebook.token = ${?FACEBOOK_TOKEN}
 
 Add a Instagram Client Id,Client Secret, TOKEN, URLs, more information [here](https://www.instagram.com/developer/)
 
       instagram.url.uri = ${?INSTAGRAM_URL_URI}
       instagram.clientId = ${?INSTAGRAM_CLIENTIDN}
       instagram.secret = ${?INSTAGRAM_SECRET}
       instagram.token = ${?INSTAGRAM_TOKEN}
       instagram.url.auth = ${?INSTAGRAM_URL_AUTH}
 
 
 5. Go to console (prompt), navegate to the project's root and type:

 ` #will install the dependencies and the run the project (usually port :9000)`
>  `> activator run `




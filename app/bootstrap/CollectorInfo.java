package bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import akka.actor.UntypedActor;
import controllers.FacebookCollector;
import controllers.InstagramCollector;
import play.Play;
import play.libs.ws.WS;

/**
 * Created by alvaro.joao.silvino on 20/08/2015.
 */
public class CollectorInfo extends UntypedActor {

    public enum Moment {
        ALL("ALL"),
        RECENT("RECENT");

        String name;
        private Moment(String name){
            this.name = name;
        }
        public static List<String> getList() {
            List<String> tags = new ArrayList<>();

            for (Moment tag : Moment.values()) {
                tags.add(tag.name());
            }
            return tags;
        }
    }
    public enum Collector {
        FACEBOOK("FACEBOOK"),
        INSTAGRAM("INSTAGRAM");

        String name;
        private Collector(String name){
            this.name = name;
        }
        public static List<String> getList() {
            List<String> tags = new ArrayList<>();

            for (Collector tag : Collector.values()) {
                tags.add(tag.name());
            }
            return tags;
        }
    }
    public static class CollectorInfoObject{
        private Collector collector;
        private Moment moment;
        private String token;
        public CollectorInfoObject(Collector collector){
            this.collector = collector;
            this.moment = Moment.ALL;
        }
        public CollectorInfoObject(Collector collector,Boolean recent){
            this.collector = collector;
            this.moment = (recent)?Moment.RECENT:Moment.ALL;
        }
        public Collector getCollector() {
            return collector;
        }

        public void setCollector(Collector collector) {
            this.collector = collector;
        }
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Moment getMoment() {
            return moment;
        }

        public void setMoment(Moment moment) {
            this.moment = moment;
        }
    }

    public CollectorInfo(){

    }

    public void onReceive(Object message){
        if (message instanceof CollectorInfoObject) {
            CollectorInfoObject objectInfo = (CollectorInfoObject) message;
            Moment moment = objectInfo.getMoment();
            switch (objectInfo.getCollector()){
                case FACEBOOK:
                    FacebookCollector.collect(moment);

                    break;
                case INSTAGRAM:

                    String url = Play.application().configuration().getString("instagram.url.auth");
                    String cientId = Play.application().configuration().getString("instagram.clientId");
                    String uri = Play.application().configuration().getString("instagram.url.uri");
                    String token = Play.application().configuration().getString("instagram.token");

                    if(token!=null){
                        InstagramCollector.collect(moment,token);
                    }else{

                        WS.url(url).setQueryParameter("client_id",cientId)
                                .setQueryParameter("redirect_uri",uri)
                                .setQueryParameter("response_type","code").get().get(10000);
                    }

                    break;
                default:
                    break;
            }

            getSender().tell(message, getSelf());
        } else {
            unhandled(message);
        }
    }
}

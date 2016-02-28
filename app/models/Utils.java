package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvaro.joao.silvino on 29/08/2015.
 */
public class Utils{
    public enum InstagramPages {
        chicorei("20133482"),
        camiseteriasa("374614102"),
        kanuibr("196502375");

        public String id;
        private InstagramPages(String id){
            this.id = id;
        }
        public static List<String> getList() {
            List<String> tags = new ArrayList<>();

            for (InstagramPages tag : InstagramPages.values()) {
                tags.add(tag.name());
            }
            return tags;
        }
        public static InstagramPages getById(String id){
            for (InstagramPages tag : InstagramPages.values()) {
                if(tag.id.equals(id))
                    return tag;
            }
            return null;
        }
        public static List<String> getListId() {
            List<String> tags = new ArrayList<>();

            for (InstagramPages tag : InstagramPages.values()) {
                tags.add(tag.id);
            }
            return tags;
        }
    }

    public enum FacebookPages {
        br4sileirissimos("404693309619224"),
        camiseteria("7018060973"),
        chicorei("162726143745402"),
        king55style("177679275612961"),
        mestredalma("160540370633021"),
        kanuibr("197061883680740");

        public String id;
        private FacebookPages(String id){
            this.id = id;
        }
        public static List<String> getList() {
            List<String> tags = new ArrayList<>();

            for (FacebookPages tag : FacebookPages.values()) {
                tags.add(tag.name());
            }
            return tags;
        }
        public static FacebookPages getById(String id){
            for (FacebookPages tag : FacebookPages.values()) {
                if(tag.id.equals(id))
                    return tag;
            }
            return null;
        }
        public static List<String> getListId() {
            List<String> tags = new ArrayList<>();

            for (FacebookPages tag : FacebookPages.values()) {
                tags.add(tag.id);
            }
            return tags;
        }
    }

}

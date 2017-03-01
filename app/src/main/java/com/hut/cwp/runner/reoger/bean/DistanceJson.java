package com.hut.cwp.runner.reoger.bean;

/**
 * Created by 24540 on 2017/2/28.
 */

public class DistanceJson {

    /**
     * status : 0
     * total : 3
     * entity_name : 865983021403077
     * distance : 42.981959620369
     * toll_distance : 0
     * start_point : {"longitude":113.11094294268,"latitude":27.821655154026,"coord_type":3,"loc_time":1488270226}
     * end_point : {"longitude":113.11063814804,"latitude":27.821927152273,"coord_type":3,"loc_time":1488270291}
     * message : 成功
     */

    private int status;
    private int total;
    private String entity_name;
    private double distance;
    private int toll_distance;
    private StartPointBean start_point;
    private EndPointBean end_point;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getEntity_name() {
        return entity_name;
    }

    public void setEntity_name(String entity_name) {
        this.entity_name = entity_name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getToll_distance() {
        return toll_distance;
    }

    public void setToll_distance(int toll_distance) {
        this.toll_distance = toll_distance;
    }

    public StartPointBean getStart_point() {
        return start_point;
    }

    public void setStart_point(StartPointBean start_point) {
        this.start_point = start_point;
    }

    public EndPointBean getEnd_point() {
        return end_point;
    }

    public void setEnd_point(EndPointBean end_point) {
        this.end_point = end_point;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class StartPointBean {
        /**
         * longitude : 113.11094294268
         * latitude : 27.821655154026
         * coord_type : 3
         * loc_time : 1488270226
         */

        private double longitude;
        private double latitude;
        private int coord_type;
        private int loc_time;

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public int getCoord_type() {
            return coord_type;
        }

        public void setCoord_type(int coord_type) {
            this.coord_type = coord_type;
        }

        public int getLoc_time() {
            return loc_time;
        }

        public void setLoc_time(int loc_time) {
            this.loc_time = loc_time;
        }
    }

    public static class EndPointBean {
        /**
         * longitude : 113.11063814804
         * latitude : 27.821927152273
         * coord_type : 3
         * loc_time : 1488270291
         */

        private double longitude;
        private double latitude;
        private int coord_type;
        private int loc_time;

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public int getCoord_type() {
            return coord_type;
        }

        public void setCoord_type(int coord_type) {
            this.coord_type = coord_type;
        }

        public int getLoc_time() {
            return loc_time;
        }

        public void setLoc_time(int loc_time) {
            this.loc_time = loc_time;
        }
    }
}

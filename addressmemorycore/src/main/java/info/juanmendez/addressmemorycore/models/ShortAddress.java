package info.juanmendez.addressmemorycore.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Juan Mendez on 6/25/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */


public class ShortAddress extends RealmObject{

    @PrimaryKey
    private long addressId;

    @Index
    String name = "";


    String address1;
    String address2;

    @Index
    int timesVisited;

    @Index
    Date dateUpdated;

    //this is a google id if we were to get it from autocomplete
    String mapId;

    double lat;
    double lon;
    String url;
    String photoLocation = "";

    private Commute commute = new Commute();

    public ShortAddress() {
        if( dateUpdated == null ){
            dateUpdated = new Date();
        }
    }

    public ShortAddress(long addressId) {
        this.addressId = addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public long getAddressId() {
        return addressId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = SubmitError.emptyOrNull(name)?"":name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = SubmitError.emptyOrNull(address1)?"":address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 =  SubmitError.emptyOrNull(address2)?"":address2;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotoLocation() {
        return photoLocation;
    }

    public void setPhotoLocation(String photoLocation) {
        this.photoLocation = photoLocation;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = SubmitError.emptyOrNull(mapId)?"":mapId;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void updateDateModified() {
        this.dateUpdated = new Date();
    }

    public int getTimesVisited() {
        return timesVisited;
    }

    public void setTimesVisited(int timesVisited) {
        this.timesVisited = timesVisited;
    }

    public Commute getCommute() {
        return commute;
    }

    public void setCommute(Commute commute) {
        this.commute = commute;
    }
}

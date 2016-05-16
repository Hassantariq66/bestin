package com.example.hamza.bestin;

import java.io.Serializable;
import java.security.PrivateKey;

/**
 * Created by hamza on 6/15/2015.
 */
public class TopMovies implements Serializable {

    private int id;
    private String  idIMDB;
    private int ranking;
    private double rating;
    private String title;
    private String urlPoster;
    private String Year;
    private String simplePlot;
    private String votes;

    public TopMovies(String idIMDB, int ranking, double rating, String title, String urlPoster, String year,String simplePlot,String votes) {
        this.idIMDB = idIMDB;
        this.ranking = ranking;
        this.rating = rating;
        this.title = title;
        this.urlPoster = urlPoster;
        Year = year;
        this.simplePlot=simplePlot;
        this.votes=votes;
    }
    public TopMovies() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdIMDB() {
        return idIMDB;
    }

    public void setIdIMDB(String idIMDB) {
        this.idIMDB = idIMDB;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlPoster() {
        return urlPoster;
    }

    public void setUrlPoster(String urlPoster) {
        this.urlPoster = urlPoster;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getSimplePlot() {
        return simplePlot;
    }

    public void setSimplePlot(String simplePlot) {
        this.simplePlot = simplePlot;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }
}
package com.example.flo.hocklines.service.Firebase.model;

/**
 * Created by Flo on 27/10/2017.
 */

public class MatchInfo {


    private String adversaire;
    private Integer myScore;
    private Integer scoreAdverse;

    public MatchInfo() {
    }

    public MatchInfo(String adversaire, Integer myScore, Integer scoreAdverse) {
        this.adversaire = adversaire;
        this.myScore = myScore;
        this.scoreAdverse = scoreAdverse;
    }


    public String getAdversaire() {
        return adversaire;
    }

    public void setAdversaire(String adversaire) {
        this.adversaire = adversaire;
    }

    public Integer getMyScore() {
        return myScore;
    }

    public void setMyScore(Integer myScore) {
        this.myScore = myScore;
    }

    public Integer getScoreAdverse() {
        return scoreAdverse;
    }

    public void setScoreAdverse(Integer scoreAdverse) {
        this.scoreAdverse = scoreAdverse;
    }
}

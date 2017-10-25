package com.example.flo.hocklines.service.Firebase.model;

/**
 * Created by Flo on 20/10/2017.
 */

public class InfoJoueur {

    private int numeroMaillotNoir;
    private int numeroMaillotBlanc;
    private int numLicence;

    public InfoJoueur(int numeroMaillotNoir, int numeroMaillotBlanc, int numLicence) {
        this.numeroMaillotNoir = numeroMaillotNoir;
        this.numeroMaillotBlanc = numeroMaillotBlanc;
        this.numLicence = numLicence;
    }

    public InfoJoueur() {
    }

    public int getNumLicence() {
        return numLicence;
    }

    public void setNumLicence(int numLicence) {
        this.numLicence = numLicence;
    }

    public int getNumeroMaillotNoir() {
        return numeroMaillotNoir;
    }

    public void setNumeroMaillotNoir(int numeroMaillotNoir) {
        this.numeroMaillotNoir = numeroMaillotNoir;
    }

    public int getNumeroMaillotBlanc() {
        return numeroMaillotBlanc;
    }

    public void setNumeroMaillotBlanc(int numeroMaillotBlanc) {
        this.numeroMaillotBlanc = numeroMaillotBlanc;
    }
}

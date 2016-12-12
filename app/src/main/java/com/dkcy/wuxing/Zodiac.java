package com.dkcy.wuxing;

/**
 * Created by danielyeo on 12/12/2016.
 */

public class Zodiac {
    // Private variables
    private int nominal_year;
    private String[] zodiac;

    public Zodiac(){

    }

    public void setNominal_year(int year){ this.nominal_year = year; }
    public int getNominal_year(){ return this.nominal_year; }

    public void setZodiac(String[] zodiac){ this.zodiac = zodiac; }
    public String[] getZodiac(){ return this.zodiac; }

}


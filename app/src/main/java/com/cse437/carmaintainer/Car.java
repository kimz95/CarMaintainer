package com.cse437.carmaintainer;


import android.content.Context;

import java.util.Calendar;
import java.util.Date;

public class Car {
    public static double AVERAGE_RATE = 1;// 6.11111 * Math.pow(10,-4) ; // km/sec

    private Context context;

    public int carID;
    public String make, model;
    public int year, volume;
    public double estMileage, myMileageRate;
    public int estDistanceToMaint;
    public int maintState;
    boolean maintDone;
    public Date lastUpdateDate;

    Car(int carID, String make, String model, int year, int volume, double estMileage, double myMileageRate,
        int estDistanceToMaint, int maintState, boolean maintDone, Date lastUpdateDate, Context context){
        this.carID = carID;
        this.make = make;
        this.model = model;
        this.year = year;
        this.volume = volume;
        this.estMileage = estMileage;
        this.myMileageRate = myMileageRate;
        this.estDistanceToMaint = estDistanceToMaint;
        this.maintState = maintState;
        this.maintDone = maintDone;
        this.lastUpdateDate = lastUpdateDate;
        this.context = context;
    }

    Car(int carID, String make, String model, int year, int volume, int estMileage, Context context){
        this.carID = carID;
        this.make = make;
        this.model = model;
        this.year = year;
        this.volume = volume;
        this.estMileage = estMileage;

        int rounded = ((estMileage + 4999) / 5000 ) * 5000;
        this.estDistanceToMaint = rounded - estMileage;
        if(this.estDistanceToMaint < 500)
            this.maintState = 2;
        else if(this.estDistanceToMaint < 2000)
            this.maintState = 1;
        else
            this.maintState = 0;
        myMileageRate = AVERAGE_RATE;
        lastUpdateDate = Calendar.getInstance().getTime();
        maintDone = false;
        this.context = context;
    }

    public void maintenanceDone(){
        if(maintDone == false){
            maintState = 0;
            maintDone = true;
        }
    }

    public void updateMileage(){
        Date currentDate = Calendar.getInstance().getTime();
        long currentTime = currentDate.getTime();
        long lastUpdateTime = lastUpdateDate.getTime();
        long elapsedTime = (currentTime - lastUpdateTime) / 1000;
        double extraMileage = elapsedTime * myMileageRate;

        this.lastUpdateDate = currentDate;
        estMileage += extraMileage;

        int rounded = ((( (int)estMileage) + 4999) / 5000) * 5000;
        int distanceToMaint = rounded - (int)estMileage;

        if(this.estDistanceToMaint == 2000){
            maintDone = false;
        }

        this.estDistanceToMaint = distanceToMaint;

        if(this.estDistanceToMaint < 500 && !maintDone){
            maintState = 2;
        }else if(this.estDistanceToMaint < 2000 && !maintDone && maintState == 0) {
            maintState = 1;
        }

        DBAdapter dbAdapter = new DBAdapter(this.context);
        dbAdapter.open();
        dbAdapter.updateRow(this.carID, this.make, this.model, this.year, this.volume, this.estMileage, this.myMileageRate,
                this.estDistanceToMaint, this.maintState, this.maintDone, this.lastUpdateDate.getTime());
        dbAdapter.close();
    }

}

package com.cse437.carmaintainer;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ListAdapter carAdapter;
    ListView carsListView;
    ArrayList<Car> cars;
    Car a;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        Button newCarButton = findViewById(R.id.newCarBtn);
        carsListView = findViewById(R.id.carsListView);

        ActionBar bar = this.getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,R.color.myColor)));

        newCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewCarActivity.class);
                intent.putExtra("Source",0);
                startActivity(intent);
            }
        });

        carsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Log.d("Click",String.valueOf(pos));
                StartCarActivity(pos);

            }});

        carsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                startDialog(pos);
                return true;
            }
        });

        handler = new Handler();
        handler.postDelayed(updateRunnable,1000);

        initCarList();

    }



    private void StartCarActivity(int pos){
        Intent intent = new Intent(this,NewCarActivity.class);
        Car selectedCar = cars.get(pos);

        intent.putExtra("Source",1);

        intent.putExtra("carID",selectedCar.carID);
        intent.putExtra("make",selectedCar.make);
        intent.putExtra("model",selectedCar.model);
        intent.putExtra("year",selectedCar.year);
        intent.putExtra("volume",selectedCar.volume);
        intent.putExtra("mileage",(int)selectedCar.estMileage);

        startActivity(intent);
    }

    private void startDialog(final int pos){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

        dlgAlert.setMessage("Maintenance done?");
        dlgAlert.setCancelable(true);

        dlgAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Car selectedCar = cars.get(pos);
                selectedCar.maintenanceDone();
                carAdapter.notifyDataSetChanged();
            }
        });

        dlgAlert.setNegativeButton("No", null);
        dlgAlert.create().show();
    }


    private void initCarList(){
        cars = new ArrayList<>();
        DBAdapter dbAdapter = new DBAdapter(this);

        dbAdapter.open();
        Cursor cursor =  dbAdapter.getAllRows();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int carId = cursor.getInt(1);
                    String make = cursor.getString(2);
                    String model = cursor.getString(3);
                    int year = cursor.getInt(4);
                    int volume = cursor.getInt(5);
                    double estMileage = cursor.getDouble(6);
                    double myMileageRate = cursor.getDouble(7);
                    int estDistanceToMaint = cursor.getInt(8);
                    int maintState = cursor.getInt(9);
                    boolean maintDone = cursor.getInt(10) == 1;
                    Date date = new Date(cursor.getLong(11));
                    cars.add(new Car(carId,make, model, year, volume, estMileage, myMileageRate, estDistanceToMaint, maintState, maintDone, date, this));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }else{
            Log.d("myLog","Empty cursor!");
        }
        dbAdapter.close();

        carAdapter= new ListAdapter(this, R.layout.listitem, cars);
        carsListView.setAdapter(carAdapter);

    }

    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            for(Car c:cars) {
                c.updateMileage();
                carAdapter.notifyDataSetChanged();
            }
            handler.postDelayed(this, 1000);
        }
    };

}

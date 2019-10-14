package com.cse437.carmaintainer;


import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class NewCarActivity extends AppCompatActivity {
    int spinnerwWidth;
    EditText idEditText;
    Spinner makeSpinner,modelSpinner,yearSpinner;
    EditText distanceEditText, volumeEditText;
    Button donebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);
        init();
    }

    private void init(){

        makeSpinner = findViewById(R.id.makeSpinner);
        modelSpinner = findViewById(R.id.modelSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        volumeEditText = findViewById(R.id.volumeEditText);
        distanceEditText = findViewById(R.id.distanceEditText);
        donebtn = findViewById(R.id.doneBtn);
        idEditText = findViewById(R.id.idView);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,R.color.myColor)));

        setMakeSpinner();
        setYearSpinner();

        if(getIntent().getExtras().getInt("Source") == 1){
            setSpinnerValues();
            idEditText.setEnabled(false);
        }

        donebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneBtnClick();

            }
        });

    }

    private void setMakeSpinner(){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Makes, R.layout.spinneritem);
        makeSpinner.setAdapter(adapter);

        spinnerwWidth = makeSpinner.getWidth();
        makeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                setModelSpinner(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setModelSpinner(int pos){
        TypedArray carResources = getResources().obtainTypedArray(R.array.car_pointers);

        int resID = carResources.getResourceId(pos,-1);
        if (resID < 0) {
            return;
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                resID, R.layout.spinneritem);
        modelSpinner.setAdapter(adapter);
        modelSpinner.setMinimumWidth(spinnerwWidth);
    }

    private void setYearSpinner(){
        ArrayList<String> years = new ArrayList<>();

        for(int i = 2018 ; i >= 1950 ; i--){
            years.add(String.valueOf(i));
        }

        ArrayAdapter<String> yearsAdapter = new ArrayAdapter(this, R.layout.spinneritem, years);
        yearSpinner.setAdapter(yearsAdapter);
        yearSpinner.setMinimumWidth(spinnerwWidth);
    }

    private void setSpinnerValues(){

        Bundle extras = getIntent().getExtras();
        String ID = String.valueOf(extras.getInt("carID"));
        String make = extras.getString("make");
        String model = extras.getString("model");
        String year = String.valueOf(extras.getInt("year"));
        String volume = String.valueOf(extras.getInt("volume"));
        String mileage = String.valueOf(extras.getInt("mileage"));


        idEditText.setText(ID);
        makeSpinner.setSelection(getIndex(makeSpinner, make));
        setModelSpinner(makeSpinner.getSelectedItemPosition());
        modelSpinner.setSelection(getIndex(modelSpinner, model),false);
        yearSpinner.setSelection(getIndex(yearSpinner,year));
        volumeEditText.setText(volume);
        distanceEditText.setText(mileage);

    }

    private int getIndex(Spinner mySpinner, String myString){

        int index = 0;

        for (int i=0;i < mySpinner.getCount();i++){
            if (mySpinner.getItemAtPosition(i).toString().equals(myString)){
                Log.d("myLog", myString);
                index = i;
            }
        }
        Log.d("myLog", String.valueOf(index));
        return index;
    }

    private void doneBtnClick(){
        if( idEditText.getText().toString().equals("") ||
                volumeEditText.getText().toString().equals("") ||
                distanceEditText.getText().toString().equals(""))
        {
            Toast.makeText(this, "One or more fields are empty", Toast.LENGTH_SHORT).show();
            return;
        }
        DBAdapter dbAdapter = new DBAdapter(getBaseContext());
        Car a = new Car(Integer.parseInt(idEditText.getText().toString()), makeSpinner.getSelectedItem().toString(),
                modelSpinner.getSelectedItem().toString(), Integer.parseInt(yearSpinner.getSelectedItem().toString()),
                Integer.parseInt(volumeEditText.getText().toString()),
                Integer.parseInt(distanceEditText.getText().toString()),getBaseContext());

        dbAdapter.open();

        if(getIntent().getExtras().getInt("Source") == 0){
            dbAdapter.insertRow(a.carID, a.make, a.model, a.year, a.volume, a.estMileage, a.myMileageRate, a.estDistanceToMaint, a.maintState, a.maintDone, a.lastUpdateDate.getTime());
        }else if(getIntent().getExtras().getInt("Source") == 1){
            dbAdapter.updateRow(a.carID, a.make, a.model, a.year, a.volume, a.estMileage, a.myMileageRate, a.estDistanceToMaint, a.maintState, a.maintDone, a.lastUpdateDate.getTime());
        }
        dbAdapter.close();


        Intent intent = new Intent(getBaseContext(),MainActivity.class);
        startActivity(intent);

    }
}

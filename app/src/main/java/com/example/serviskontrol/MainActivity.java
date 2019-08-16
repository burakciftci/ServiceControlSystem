package com.example.serviskontrol;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText etNumber;
    Button btNumber;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etNumber = findViewById(R.id.et_number);
        btNumber = findViewById(R.id.bt_onay);

        btNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = etNumber.getText().toString();

                if (tel.length()<11 || tel.length()>11 ){
                    Toast.makeText(context,"Numarayı eksik yada fazla tusladınız..",Toast.LENGTH_LONG).show();



                }else{

                    Intent intent = new Intent(context,BtActivity.class);

                    startActivity(intent);
                }

            }
        });


    }
}

package com.example.user.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CollapsingToolbar extends AppCompatActivity {

    EditText pName,pDescription,pQuantity,pPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collapsing_toolbar);

        pName=(EditText)findViewById(R.id.pName);
        pDescription=(EditText)findViewById(R.id.pDescription);
        pQuantity=(EditText)findViewById(R.id.pQuantity);
        pPrice=(EditText)findViewById(R.id.pPrice);

    }

    public void productInsertBtn(View view){
        pName.setText("");
        pDescription.setText("");
        pQuantity.setText("");
        pPrice.setText("");
        Toast.makeText(this,"Product Successfully Inserted!!!",Toast.LENGTH_LONG).show();
    }
}
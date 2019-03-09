package com.example.administrator.filebased;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CalendarEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_edit);
        final EditText etName = (EditText)findViewById(R.id.etCalendarName1);
        final EditText etDes = (EditText)findViewById(R.id.etCalendarDes1);
        Button btnEdit = (Button)findViewById(R.id.btnCalendarAdd1);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String des = intent.getStringExtra("des");
        etName.setText(name);
        etDes.setText(des);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(CalendarEdit.this,MainActivity.class);
                intent1.putExtra("name",etName.getText().toString());
                intent1.putExtra("des",etDes.getText().toString());
                setResult(2,intent1);
                finish();
            }
        });
    }
}

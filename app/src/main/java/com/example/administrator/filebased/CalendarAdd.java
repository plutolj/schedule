package com.example.administrator.filebased;

import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class CalendarAdd extends AppCompatActivity {
    public int year, month, day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_add);
        final EditText etName = (EditText)findViewById(R.id.etCalendarName);
        final EditText etDes = (EditText)findViewById(R.id.etCalendarDes);
        DatePicker datePicker = (DatePicker)findViewById(R.id.dpDate);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                year = i;
                month = i1;
                day = i2;
            }
        });
        Button btnAdd = (Button)findViewById(R.id.btnCalendarAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarAdd.this, MainActivity.class);
                intent.putExtra("name", etName.getText().toString());
                intent.putExtra("des", etDes.getText().toString());
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("day", day);
                setResult(1, intent);
                finish();
            }
        });
    }
}

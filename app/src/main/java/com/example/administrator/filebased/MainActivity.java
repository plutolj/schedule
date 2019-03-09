package com.example.administrator.filebased;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public int index = -1;
    public String fileName = "test";
    public ListView listView;
    public SimpleAdapter adapter;
//    public String[] titles = new String[]{"zhangsan", "lisi", "wangwu", "zhaoliu"};
//    public String[] ages = new String[]{"18", "20", "19", "30"};
    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<String> ages = new ArrayList<String>();
    public int[] images = new int[]{R.drawable.jpg1, R.drawable.jpg2,
                        R.drawable.jpg3, R.drawable.jpg4};
    public List<Map<String, Object>> data;
    public  void  getAllCals(){
        String content = readFile();
        if(content.length()>0){
            try {
                JSONObject root = new JSONObject(content);
                JSONArray array = root.getJSONArray("root");
                titles.clear();
                ages.clear();
                data.clear();
                for (int i=0;i<array.length();i++){
                    JSONObject obj = array.getJSONObject(i);
                    String name = obj.getString("name");
                    String des = obj.getString("des");
                    Map<String,Object> map = new HashMap<String,Object>();
                    map.put("title",name);
                    map.put("age",des);
                    map.put("image",R.drawable.jpg1);
                    titles.add(name);
                    ages.add(des);
                    data.add(map);
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listview);
        data = new ArrayList<Map<String, Object>>();
//        for(int i=0; i<4; i++){
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("title", titles[i]);
//            map.put("age", ages[i]);
//            map.put("image", images[i]);
//            data.add(map);
//        }

        adapter = new SimpleAdapter(this, data, R.layout.item,
                new String[]{"title", "age", "image"},
                new int[]{R.id.title, R.id.age, R.id.image});
        listView.setAdapter(adapter);
        getAllCals();
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 1, 0, "添加日程");
        menu.add(0, 2, 0, "修改日程");
        menu.add(0, 3, 0, "删除日程");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        index = menuInfo.position;
        switch (item.getItemId()){
            case 1:
                Intent intent = new Intent(this, CalendarAdd.class);
                startActivityForResult(intent, 1);
                break;
            case 2:
                Intent intent1 = new Intent(this,CalendarEdit.class);
                intent1.putExtra("name",titles.get(index));
                intent1.putExtra("des",ages.get(index));
                startActivityForResult(intent1,2);
                break;
            case 3:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("删除日程");
                builder.setMessage("确定要删除"+titles.get(index)+"?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String content = readFile();
                        try {
                            JSONObject root = new JSONObject(content);
                            JSONArray array = root.getJSONArray("root");
                            array.remove(index);
                            write2File(root.toString());
                            getAllCals();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel",null);
                builder.create().show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            String name = data.getStringExtra("name");
            String des = data.getStringExtra("des");
            int year = data.getIntExtra("year", -1);
            int month = data.getIntExtra("month", -1);
            int day = data.getIntExtra("day", -1);
            JSONObject obj = new JSONObject();
            try {
                obj.put("name",name);
                obj.put("des",des);
                obj.put("year",year);
                obj.put("month",month);
                obj.put("day",day);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            insertOneObj2File(obj);
            getAllCals();

        }
        if(resultCode==2){
            String name = data.getStringExtra("name");
            String des = data.getStringExtra("des");
            String content = readFile();
            try {
                JSONObject root = new JSONObject(content);
                JSONArray array = root.getJSONArray("root");
                JSONObject obj = array.getJSONObject(index);
                obj.put("name",name);
                obj.put("des",des);
                write2File(root.toString());
                getAllCals();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertOneObj2File(JSONObject obj) {
        String content = readFile();
        JSONObject root = null;
        if(content.length()>0){
            try {
                root = new JSONObject(content);
                JSONArray array = root.getJSONArray("root");
                array.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            root = new JSONObject();
            JSONArray array = new JSONArray();
            array.put(obj);
            try {
                root.put("root",array);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        write2File(root.toString());
    }

    public  void write2File(String content){
        try{
            FileOutputStream fos = openFileOutput(fileName,MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public String readFile(){
        String result = "";
        try {
            FileInputStream fis = openFileInput(fileName);
            int count=0;
            byte[] buffer = new byte[1024];
            count = fis.read(buffer);
            while(count>0){
                result += new String(buffer);
                count = fis.read(buffer);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  result;
    }
}

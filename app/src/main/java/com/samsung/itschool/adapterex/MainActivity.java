package com.samsung.itschool.adapterex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Button createButton;
    LinearLayout linearLayout;
    EditText enter_title, enter_author, enter_date;
    Button enter_button;
    Button search;
    int count = 1;
    int userID = 9999;

    MyOpenHelper myOpenHelper;
    SQLiteDatabase sdb;

    LinkedList<HashMap<String, Object>> mapBooks = new LinkedList<>();
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myOpenHelper =new MyOpenHelper(this);
        sdb = myOpenHelper.getWritableDatabase();

        listView = findViewById(R.id.bookList);
        createButton = findViewById(R.id.createButton);
        linearLayout = findViewById(R.id.my_layout);
        enter_title = findViewById(R.id.enter_title);
        enter_author = findViewById(R.id.enter_author);
        enter_date = findViewById(R.id.enter_date);
        enter_button = findViewById(R.id.enter_button);
        search = findViewById(R.id.search);

        //Подготовка данных - 1 этап создание списка объектов
        final LinkedList<Book> books = new LinkedList<>();
        books.add(new Book("Война и мир", "Лев Толстой", "2004", R.drawable.book));
        books.add(new Book("Основание", "Айзек Азимов", "2017", R.drawable.osnovanie));
        books.add(new Book("Преступление и наказание", "Федор Достоевский", "1986", R.drawable.prestuplenie));
        books.add(new Book("Шинель", "Николай Гоголь", "2008", R.drawable.shinel));
        books.add(new Book("Зерцалия", "Евгений Гоглоев", "2019", R.drawable.zertsalia));
        books.add(new Book("Феникс Сапиенс", "Борис Штерн", "2020", R.drawable.book));

        //Добавим список книг в базу данных
        for (int i = 0; i < books.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MyOpenHelper.COLUMN_TITLE, books.get(i).title);
            contentValues.put(MyOpenHelper.COLUMN_AUTHOR, books.get(i).author);
            contentValues.put(MyOpenHelper.COLUMN_YEAR, books.get(i).year);
            contentValues.put(MyOpenHelper.COLUMN_COVER, books.get(i).cover);
            sdb.insert(MyOpenHelper.TABLE_NAME, null, contentValues);
        }

        enter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MyOpenHelper.COLUMN_TITLE, enter_title.getText().toString());
                contentValues.put(MyOpenHelper.COLUMN_AUTHOR, enter_author.getText().toString());
                contentValues.put(MyOpenHelper.COLUMN_YEAR, enter_date.getText().toString());
                contentValues.put(MyOpenHelper.COLUMN_COVER, R.drawable.book);
                sdb.insert(MyOpenHelper.TABLE_NAME, null, contentValues);
            }
        });

        //Найти книги в базе данных
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String findAuthor  = enter_author.getText().toString();
                String findTitle  = enter_title.getText().toString();
                String findDate  = enter_date.getText().toString();
                String query = "SELECT * FROM " + MyOpenHelper.TABLE_NAME;
                if(!findAuthor.equals("")){
                    query += " WHERE author =  " + " \""  + findAuthor + "\"" + ";";
                }
                else if(!findTitle.equals("")){
                    query += " WHERE title =  " + " \""  + findTitle + "\"" + ";";
                }
                else if(!findDate.equals("")){
                    query += " WHERE year =  " + " \""  + findDate + "\"" + ";";
                } else {
                    query += ";";
                }
                Cursor cursor = sdb.rawQuery(query, null);
                mapBooks.clear();
                cursor.moveToFirst();
                while (cursor.moveToNext()){
                    findAuthor = cursor.getString(cursor.getColumnIndex(MyOpenHelper.COLUMN_AUTHOR));
                    findTitle = cursor.getString(cursor.getColumnIndex("title"));
                    findDate = cursor.getString(cursor.getColumnIndex(MyOpenHelper.COLUMN_YEAR));
                    int cover = cursor.getInt(cursor.getColumnIndex(MyOpenHelper.COLUMN_COVER));
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("author", findAuthor);
                    map.put("title", findTitle);
                    map.put("year", findDate);
                    map.put("cover", cover);
                    mapBooks.add(map);
                }
                cursor.close();
                simpleAdapter.notifyDataSetChanged();
            }
        });


        //Подготовка данных 2 этап: список с ключами

        for (int i = 0; i < books.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("author", books.get(i).author);
            map.put("title", books.get(i).title);
            map.put("year", books.get(i).year);
            map.put("cover", books.get(i).cover);
            mapBooks.add(map);
        }

        //подготовка 3 этап: вспомогательные массивы
        String[] keyFrom = {"author", "title", "year", "cover"};
        int [] idTo = {R.id.author, R.id.title, R.id.year, R.id.cover};
        //Создание адаптера
        //ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.list_item, books);
        simpleAdapter = new SimpleAdapter(this, mapBooks, R.layout.list_item,
                keyFrom, idTo);
        //установка адаптера на ListView



        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), i + ") " + books.get(i), Toast.LENGTH_SHORT)
                        .show();
            }
        });


        //создание новых кнопок
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //объявление новой кнопки
                Button newButton = new Button(getApplicationContext());
                //создание обязательных настроек
                newButton.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                //необязательные настройки
                newButton.setText("Кнопка №" + count);
                newButton.setId(userID + count);
                newButton.setBackgroundColor(Color.GREEN);
                newButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        linearLayout.removeView(view);
                    }
                });
                //установка новой кнопки в контейнер
                linearLayout.addView(newButton);
                count++;
            }
        });
    }
}

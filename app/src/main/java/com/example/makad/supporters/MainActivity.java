package com.example.makad.supporters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import android.content.Context;





public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FragmentManager fragmentManager = getSupportFragmentManager();
    //各画面のインスタンスを生成
    ScheduleFragment _schedule = new ScheduleFragment();//スケジュール管理画面
    DocumentsFragment _documents = new DocumentsFragment();//資料管理画面
    PresentationFragment _presentation = new PresentationFragment();//発表管理画面

    //音声管理用クラスのインスタンスを宣言
    private SoundManager mSoundManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 効果音管理インスタンスを生成
        mSoundManager = new SoundManager(this);

        loadFile();//セーブ読み込み

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if(savedInstanceState == null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, _schedule).commit();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.nav_schedule:
                    // インスタンスに対して張り付け方を指定する
                   fragmentTransaction.replace(R.id.container, _schedule).commit();
                    return true;
                case R.id.nav_documents:
                    // インスタンスに対して張り付け方を指定する
                  fragmentTransaction.replace(R.id.container,_documents).commit();
                    return true;
                case R.id.nav_presentation:
                    fragmentTransaction.replace(R.id.container, _presentation).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //ファイル保存
    public void saveFile(){
        Properties prop;
        prop = new Properties();
        try (FileOutputStream  fileOutputstream = openFileOutput("config.properties",
                Context.MODE_PRIVATE);){
            prop.setProperty("minute", ("" + _presentation.getMin()));
            prop.setProperty("second", ("" + _presentation.getSec()));
            prop.setProperty("daihon", (_presentation.getDaihon()));

            List<DocumentItem> documents = new ArrayList<DocumentItem>();
            documents = _documents.getDocuments();
            String documents_str = "",documents_prog = "";
            for (int i = 0;i <documents.size();i++){
                documents_str = documents_str + documents.get(i).str;
                documents_prog = documents_prog + documents.get(i).prog;
                if (i < ((documents.size() - 1))){
                    documents_str = documents_str + ",";
                    documents_prog = documents_prog + ",";
                }
            }
            if (documents_prog != ""){
                prop.setProperty("documents_str", documents_str);
                prop.setProperty("documents_prog", "" + documents_prog);
            }

            ScheduleItem schedule = new ScheduleItem();
            schedule = _schedule.list;
            String name_str = "";
            //人名を保存
            for (int i = 0;i <schedule.person_name_list.size();i++){
                name_str = name_str + schedule.person_name_list.get(i);
                if (i < ((schedule.person_name_list.size() - 1))){
                    name_str = name_str + ",";
                }
            }
            if (name_str != "") {
                prop.setProperty("name_str", name_str);
            }

            String date_str = "";
            //候補日を保存
            for (int i = 0;i <schedule.schedule.size();i++){
                date_str = date_str + schedule.schedule.get(i).name;
                if (i < ((schedule.schedule.size() - 1))){
                    date_str = date_str + ",";
                }
            }
            if (date_str != "") {
                prop.setProperty("date_str", date_str);
                String buf_str;
                //全員の予定を保存
                for (int j = 0;j < schedule.schedule.size();j++){
                    buf_str = "";
                    for (int i = 0;i <schedule.person_name_list.size();i++){
                        buf_str = buf_str + schedule.schedule.get(j).list.get(i);
                        if (i < ((schedule.person_name_list.size() - 1))){
                            buf_str = buf_str + ",";
                        }
                    }
                    prop.setProperty("schedule_str_" + (j), buf_str);
                }
            }
            if (_schedule.happyoubi != null){
                prop.setProperty("happyoubi", _schedule.happyoubi);
            }
            prop.store( fileOutputstream, "Comments");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ファイルの読み出し
    public void loadFile(){
        Properties prop;
        prop = new Properties();
        //prop.setProperty("name", "Coco");
        //prop.store(new FileOutputStream("config.properties"), "Comments");
        try (FileInputStream fileInputStream = openFileInput("config.properties")){

            prop.load(new InputStreamReader(fileInputStream,"UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        //資料の読み込み
        if ((prop.getProperty("documents_str") != null)&&(prop.getProperty("documents_str") != "")){
            String[] documents_str = prop.getProperty("documents_str").split(",",0);
            String[] documents_prog = prop.getProperty("documents_prog").split(",",0);
            List<DocumentItem> documents = new ArrayList<DocumentItem>();

            for (int i = 0;i < (documents_str.length);i++) {
                documents.add(new DocumentItem(documents_str[i],Integer.valueOf(documents_prog[i])));
            }
            _documents.setDocuments(documents);

        }

        if (prop.getProperty("minute") != null){
            //タイマーの読み込み
            int min = Integer.parseInt(prop.getProperty("minute"));
            int sec = Integer.parseInt(prop.getProperty("second"));
            String str = prop.getProperty("daihon");
            _presentation.inputData(min,sec,str);
        }

        //int name_count = 0;
        //名前をバッファに読み込み
        ArrayList<String> name_buf = new ArrayList<String>();
        if (prop.getProperty("name_str") != null){
            String[] name_str = prop.getProperty("name_str").split(",",0);
            for (int i = 0;i < name_str.length;i++){
                name_buf.add(name_str[i]);
            }
        }


        //全スケジュール読み込み
        if (prop.getProperty("date_str") != null){
            _schedule.happyoubi = prop.getProperty("happyoubi");
            String[] date_str = prop.getProperty("date_str").split(",",0);
            for (int i = 0;i < (date_str.length);i++) {
                _schedule.list.addDate(date_str[i]);
            }
            //全員の予定を読み込み+参加者の読み込み
            if (prop.getProperty("schedule_str_0") != null) {
                String schedule_str[][];
                String buf[];
                schedule_str = new String[name_buf.size()][date_str.length];
                for (int j = 0; j < (date_str.length); j++) {
                    buf = prop.getProperty("schedule_str_" + j).split(",", 0);
                    for (int i = 0; i < (name_buf.size()); i++) {
                        schedule_str[i][j] = buf[i];
                        System.out.println("実行" + buf);
                    }
                }

                for (int i = 0;i < name_buf.size();i++){
                    ArrayList<Integer> sanka = new ArrayList<Integer>();
                    for (int j = 0; j < (date_str.length); j++) {
                        sanka.add(Integer.valueOf(schedule_str[i][j]));
                    }
                    _schedule.list.addPerson(name_buf.get(i), sanka);
                }
            }
        }


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final Activity activity = this;
        Intent intent = null;

        if (id == R.id.nav_camera) {
            //Save機能
            new AlertDialog.Builder(this)
                    .setTitle("Save All")
                    .setMessage("データを保存します")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveFile();
                            System.out.println("Save Ok");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        } else if (id == R.id.yc_yoyaku) {
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://supply3.itc.tcu.ac.jp/reserve/pub/index_.php"));
        }


        if (intent != null){
            activity.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        // 効果音管理インスタンスを破棄
        mSoundManager.release();
        super.onDestroy();
    }

    public void playSe(int se_num) {
        switch (se_num){
            case 0:
                mSoundManager.play(SoundManager.SOUND_ALERT, 100);
                break;
            case 1:
                mSoundManager.play(SoundManager.SOUND_ALERT2, 100);
                break;
            case 2:
                mSoundManager.play(SoundManager.SOUND_END, 100);
                break;
        }
    }



}

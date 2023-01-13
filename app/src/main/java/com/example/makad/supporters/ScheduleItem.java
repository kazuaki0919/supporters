package com.example.makad.supporters;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//例(疑似二次元リスト)
//      |田中|鈴木|渡辺 →person_name_list　<String>
//11/02 |可  |可  |不可 →schedule <Date>
//11/04 |不可|可  |可
//...

//二次元リストの管理
public class ScheduleItem {

    public ArrayList<String> person_name_list = new ArrayList<String>();//人の名前

    public ArrayList<Date> schedule = new ArrayList<Date>();//スケジュール

    public class Date{
        String name;//年月日時分
        public ArrayList<Integer> list = new ArrayList<Integer>();//参加の不可避
        int sum = 0;
        Date(String str,ArrayList<Integer> list){
            this.name = str;
            this.list = list;
        }
    }


    //候補日を追加
    public void addDate(String str){
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0;i < person_name_list.size();i++){
            list.add(0);//0初期化
        }
        Date buf = new Date(str,list);
        schedule.add(buf);
    }

    public void addPerson(String str,ArrayList<Integer> sankaList){
        person_name_list.add(str);
        for (int i = 0;i < sankaList.size();i++){//リストの値で初期化
            schedule.get(i).list.add(sankaList.get(i));
            schedule.get(i).sum = schedule.get(i).sum + sankaList.get(i);
        }
    }

    //候補日(とその参加予定)削除
    public void delDate(int position){
        schedule.remove(position);
    }

    //position番の参加者およびその人の予定をリストから消去、
    public void delPerson(int position){
        person_name_list.remove(position);
        for (int i = 0;i < schedule.size();i++){//要素を順に消去
            schedule.get(i).sum = schedule.get(i).sum - schedule.get(i).list.get(position);
            schedule.get(i).list.remove(position);
        }
    }

    //position番の人の予定を新しいリストで上書き
    public void setSchedule(int position,String rename,ArrayList<Integer> list){
        person_name_list.set(position,rename);
        System.out.println("これは" + list);
        for (int i = 0;i < list.size();i++){
            schedule.get(i).sum = schedule.get(i).sum - schedule.get(i).list.get(position);//まず引く
            schedule.get(i).list.set(position,list.get(i));//リストを順に書き換え
            schedule.get(i).sum = schedule.get(i).sum + schedule.get(i).list.get(position);//つぎにたす
        }
    }

    public void sortSchedule() {
        Collections.sort(
                schedule,
                new Comparator<Date>() {
                    @Override
                    public int compare(Date o1, Date o2) {
                        return o1.name.compareTo(o2.name);
                    }
                }
        );
    }
}

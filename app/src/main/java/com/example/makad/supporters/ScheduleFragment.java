package com.example.makad.supporters;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.widget.AdapterView;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleFragment extends Fragment {

    TextView s_text;
    ListView listView,personListView;

    ScheduleItem list = new ScheduleItem();

    public String happyoubi;//発表日

    ScheduleAdapter adapter;
    PersonAdapter person_adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.schedule_fragment, container, false);
        Button dialogBtn = view.findViewById(R.id.add_schedule);
        Button addPersonBtn = view.findViewById(R.id.add_person);
        Button mail = view.findViewById(R.id.mail);
        s_text = view.findViewById(R.id.s_fragment_text);
        s_text.setText(happyoubi);

        listView = view.findViewById(R.id.list_schedule);
        adapter = new ScheduleAdapter(getActivity().getApplicationContext(), R.layout.schedule_list, list.schedule);
        listView.setAdapter(adapter);

        personListView = view.findViewById(R.id.list_person);//参加者リストビュー
        person_adapter = new PersonAdapter(getActivity().getApplicationContext(), R.layout.person_list, list.person_name_list);
        personListView.setAdapter(person_adapter);

        // clickイベント追加
        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            // クリックしたらダイアログを表示する処理
            public void onClick(View v) {
                callScheduleDialog();
            }
        });

        addPersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            // クリックしたらダイアログを表示する処理
            public void onClick(View v) {
                callPersonDialog();
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            // クリックしたらダイアログを表示する処理
            public void onClick(View v) {
                callMailEvent();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (view.getId()) {
                    case R.id.decide:
                        s_text.setText(list.schedule.get(position).name);
                        happyoubi = list.schedule.get(position).name;
                        adapter.notifyDataSetChanged();
                        break;

                    case R.id.delete:
                        list.delDate(position);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        personListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (view.getId()) {

                    case R.id.set_person:
                        callPersonSetDialog(position);
                        adapter.notifyDataSetChanged();
                        break;

                    case R.id.delete:
                        list.delPerson(position);
                        adapter.notifyDataSetChanged();
                        person_adapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        return view;
    }

    public void callMailEvent(){
        final Fragment activity = this;
        Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_TEXT,happyoubi);
        activity.startActivity(intent);
    }

    //候補日追加ダイアログ生成
    public void callScheduleDialog(){
        // ダイアログクラスをインスタンス化
        ScheduleDialog dialog = ScheduleDialog.newInstance(this, 1);
        // 表示  getFagmentManager()は固定、sampleは識別タグ
        dialog.show(getFragmentManager(), "sample");
    }

    //参加者追加ダイアログ生成
    public void callPersonDialog(){
        // ダイアログクラスをインスタンス化
        PersonDialog dialog = PersonDialog.newInstance(this, 2);
        dialog.inputDateList(list.schedule);
        // 表示  getFagmentManager()は固定、sampleは識別タグ
        dialog.show(getFragmentManager(), "sample");
    }

    //参加者編集ダイアログ生成
    public void callPersonSetDialog(int position){
        // ダイアログクラスをインスタンス化
        PersonDialog dialog = PersonDialog.newInstance(this, position + 3);
        dialog.inputDateList(list.schedule);
        // 表示  getFagmentManager()は固定、sampleは識別タグ
        dialog.show(getFragmentManager(), "sample");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1://候補日の処理
                String str = data.getStringExtra("str");
                list.addDate(str);
                list.sortSchedule();
                adapter.notifyDataSetChanged();
                break;
            case 2://参加者の追加
                String buf = data.getStringExtra("str");
                list.addPerson(buf,data.getIntegerArrayListExtra("bufList"));
                adapter.notifyDataSetChanged();
                person_adapter.notifyDataSetChanged();
                break;
            default:
                if (requestCode >= 3){
                    //参加者の編集
                        String rename = data.getStringExtra("str");
                        int position = requestCode - 3;
                        list.setSchedule(position,rename,data.getIntegerArrayListExtra("bufList"));
                        adapter.notifyDataSetChanged();
                        person_adapter.notifyDataSetChanged();
                        break;
                }
        }

    }

    //スケジュール追加用ダイアログ
    public static class ScheduleDialog extends DialogFragment {

        NumberPicker numpichour;
        NumberPicker numpicmin;
        TextView day;
        int hour,min;
        String str;


        public static ScheduleFragment.ScheduleDialog newInstance(Fragment target, int requestCode) {
            ScheduleFragment.ScheduleDialog fragment = new ScheduleFragment.ScheduleDialog();
            fragment.setTargetFragment(target, requestCode);

            Bundle args = new Bundle();
            fragment.setArguments(args);

            return fragment;
        }

        public void setDialog(){
            DateDialog dialog = DateDialog.newInstance(this, 1);
            dialog.show(getFragmentManager(), "dialog");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = inflater.inflate(R.layout.schedule_dialog, null);


            numpichour = content.findViewById(R.id.numberPickerHour);
            numpicmin = content.findViewById(R.id.numberPickerMin);
            Button addDateButton = content.findViewById(R.id.add_date_button);
            Button dataPicker =  content.findViewById(R.id.datepicker_button);
            day =  content.findViewById(R.id.day_text);
            numpichour.setMaxValue(23);
            numpichour.setMinValue(0);
            numpicmin.setMaxValue(59);
            numpicmin.setMinValue(0);

            builder.setView(content);

            dataPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDialog();
                }
            });

            addDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    hour = numpichour.getValue();
                    min = numpicmin.getValue();

                    Dialog dialog = getDialog();
                    Intent result = new Intent();
                    str = str + hour + "時" + min + "分";
                    result.putExtra("str",str);

                    // 呼び出し元がFragmentの場合
                    if (getTargetFragment() != null) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
                    }
                    dismiss();
                }
            });

            // Create the AlertDialog object and return it
            return builder.create();
        }

        //DateFragmentからの返信をキャッチするメソッド
        public void onActivityResult(int requestCode, int resultCode, Intent data) {

            String str = "";
            str = str + data.getIntExtra("year",0) + "年";
            str = str + data.getIntExtra("month",0) + "月";
            str = str + data.getIntExtra("day",0) + "日";
            this.str = str;
            this.day.setText(str);


        }

    }

    public static class DateDialog extends DialogFragment {

                // ダイアログが生成された時に呼ばれるメソッド ※必須
                public static ScheduleFragment.DateDialog newInstance(Fragment target, int requestCode) {
                    ScheduleFragment.DateDialog fragment = new ScheduleFragment.DateDialog();
                    fragment.setTargetFragment(target, requestCode);

            Bundle args = new Bundle();
            fragment.setArguments(args);

            return fragment;
        }


        public Dialog onCreateDialog(Bundle savedInstanceState){
            // 今日の日付のカレンダーインスタンスを取得
            final Calendar calendar = Calendar.getInstance();

            // ダイアログ生成  DatePickerDialogのBuilderクラスを指定してインスタンス化します
            DatePickerDialog dateBuilder = new DatePickerDialog(
                    getActivity(),
                    new DatePickerDialog.OnDateSetListener(){
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            // 選択された年・月・日を整形 ※月は0-11なので+1している
                            //String dateStr = year + "年" + (month + 1) + "月" + dayOfMonth + "日";
                            Intent result = new Intent();
                            result.putExtra("year",year);
                            result.putExtra("month",month + 1);
                            result.putExtra("day",dayOfMonth);
                            // 呼び出し元がFragmentの場合
                            if (getTargetFragment() != null) {
                                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
                            }
                        }
                    },
                    calendar.get(Calendar.YEAR), // 初期選択年
                    calendar.get(Calendar.MONTH), // 初期選択月
                    calendar.get(Calendar.DAY_OF_MONTH) // 初期選択日
            );

            // dateBulderを返す
            return dateBuilder;
        }
    }

    //参加者追加用ダイアログ
    public static class PersonDialog extends DialogFragment {

        TextView day;
        EditText name_edit;
        int hour,min;
        String str;
        ListView selectListView;
        scheduleSelectAdapter adapter;
        ArrayList<ScheduleItem.Date> selectList = new ArrayList<ScheduleItem.Date>();

        public void inputDateList(ArrayList<ScheduleItem.Date> list){
            this.selectList = list;
        }

        public static ScheduleFragment.PersonDialog newInstance(Fragment target, int requestCode) {
            ScheduleFragment.PersonDialog fragment = new ScheduleFragment.PersonDialog();
            fragment.setTargetFragment(target, requestCode);

            Bundle args = new Bundle();
            fragment.setArguments(args);

            return fragment;
        }

        public void setDialog(){
            PersonDialog dialog = PersonDialog.newInstance(this, 2);
            dialog.show(getFragmentManager(), "dialog");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = inflater.inflate(R.layout.person_dialog, null);

            Button addPersonButton = content.findViewById(R.id.add_person_button);
            name_edit = content.findViewById(R.id.name_edit);

            selectListView = content.findViewById(R.id.listView);//候補日選択リストビュー
            adapter = new scheduleSelectAdapter(getActivity().getApplicationContext(), R.layout.schedule_select_list, selectList);
            selectListView.setAdapter(adapter);

            addPersonButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent result = new Intent();
                    str = name_edit.getText().toString();
                    ArrayList<Integer> bufList = new ArrayList<Integer>();//チェックボックスの結果を保存
                    bufList = adapter.checkList;
                    System.out.println(bufList);
                    result.putExtra("str",str);
                    result.putExtra("bufList",bufList);

                    // 呼び出し元がFragmentの場合
                    if (getTargetFragment() != null) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
                    }
                    dismiss();
                }
            });

            builder.setView(content);

            // Create the AlertDialog object and return it
            return builder.create();
        }

    }


}


class ScheduleAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int resourcedId;
    private List<ScheduleItem.Date> items;
    public int ave = 0;

    ViewHolder holder;

    static class ViewHolder {
        TextView textView,document_prog;
        Button deleteButton;
        Button decideButton;
    }

    ScheduleAdapter(Context context, int resourcedId, List<ScheduleItem.Date> items) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resourcedId = resourcedId;
        this.items = items;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resourcedId, parent, false);

            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.text);
            holder.decideButton = convertView.findViewById(R.id.decide);
            holder.deleteButton = convertView.findViewById(R.id.delete);
            holder.document_prog = convertView.findViewById(R.id.documents_prog);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(items.get(position).name + "..." + items.get(position).sum + "人");


        holder.decideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view, position, R.id.decide);
            }
        });


        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view, position, R.id.delete);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

class PersonAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int resourcedId;
    private List<String> items;

    ViewHolder holder;

    static class ViewHolder {
        TextView textView;
        Button delete;
        Button setPerson;
    }

    PersonAdapter(Context context, int resourcedId, List<String> items) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resourcedId = resourcedId;
        this.items = items;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resourcedId, parent, false);

            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.name);
            holder.setPerson =convertView.findViewById(R.id.set_person);
            holder.delete =convertView.findViewById(R.id.delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(items.get(position));

        holder.setPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view, position, R.id.set_person);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view, position, R.id.delete);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

class scheduleSelectAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int resourcedId;
    private List<ScheduleItem.Date> items;
    public ArrayList<Integer> checkList = new ArrayList<Integer>();

    ViewHolder holder;

    static class ViewHolder {
        TextView textView;
        CheckBox check;
    }

    scheduleSelectAdapter(Context context, int resourcedId, List<ScheduleItem.Date> items) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resourcedId = resourcedId;
        this.items = items;
        // 初期値を設定する
        for(int i=0; i<this.items.size();i++){
            checkList.add(i,0);
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resourcedId, parent, false);

            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.text);
            holder.check = convertView.findViewById(R.id.checkbox);

            // チェックの状態が変化した場合はマップに記憶する
            holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        checkList.set(position,1);
                    }else{
                        checkList.set(position,0);
                    }
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(items.get(position).name);

        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}



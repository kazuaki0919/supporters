package com.example.makad.supporters;

import android.os.Bundle;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;

import java.util.ArrayList;
import java.util.List;



public class DocumentsFragment extends Fragment {



    private static List<DocumentItem> documents = new ArrayList<DocumentItem>();//資料を管理するリスト

    CustomAdapter adapter;//リストビュー用
    ProgressBar doc_progbar;//資料の累計完成度
    TextView average_txt;//%表示よう


    public List<DocumentItem> getDocuments(){
        return this.documents;
    }

    public void setDocuments(List<DocumentItem> documents){
        this.documents = documents;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.documents_fragment, container, false);

        Button addButton = view.findViewById(R.id.add_document_button);
        Button aveButton = view.findViewById(R.id.calc_average);
        average_txt = view.findViewById(R.id.all_progress);
        ListView listView = view.findViewById(R.id.listView);
        adapter = new CustomAdapter(getActivity().getApplicationContext(), R.layout.document_list, documents);
        listView.setAdapter(adapter);
        doc_progbar = view.findViewById(R.id.document_bar);

        //平均値算出
        int ave = adapter.getAve();
        doc_progbar.setProgress(ave);
        average_txt.setText(ave + "%");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (view.getId()) {
                    case R.id.delete:
                        documents.remove(position);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ドキュメントリストへの項目の追加
                callDialog();
            }
        });

        aveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //平均値算出
                int ave = adapter.getAve();
                doc_progbar.setProgress(ave);
                average_txt.setText(ave + "%");
            }
        });

        return  view;
    }

    //資料追加用ダイアログ
    private void callDialog(){
        final EditText editView = new EditText(getActivity());
        editView.setInputType(InputType.TYPE_CLASS_TEXT);//改行を禁止
        new AlertDialog.Builder(getActivity())
                .setTitle("Document Name")
                .setView(editView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // OKボタンの処理
                        documents.add(new DocumentItem(editView.getText().toString(),2));
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }
}

//リストビューを管理するアダプター
class CustomAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int resourcedId;
    private List<DocumentItem> items;

    ViewHolder holder;

    //全資料の完成度から平均を出すメソッド
    public int getAve(){
        if (getCount() == 0){
            return 0;
        }
        int x = 0;
        for (int i = 0;i < items.size();i++){
            x = x + items.get(i).prog;
        }
        x = x * 10 / items.size();
        return x;
    }

    static class ViewHolder {
        TextView textView,document_prog;
        Button deleteButton;
        SeekBar document_seekbar;
    }

    CustomAdapter(Context context, int resourcedId, List<DocumentItem> items) {
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
            holder.deleteButton = convertView.findViewById(R.id.delete);
            holder.document_prog = convertView.findViewById(R.id.documents_prog);
            holder.document_seekbar =convertView.findViewById(R.id.SeekBar00);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(items.get(position).str);
        //System.out.println(items.get(position).prog + " " + position);
        holder.document_seekbar.setProgress(items.get(position).prog);
        holder.document_prog.setText(items.get(position).prog * 10 + "%");


        holder.document_seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    //ツマミがドラッグされると呼ばれる
                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar, int progress, boolean fromUser) {
                        //holder.document_prog.setText(progress + "%");
                    }

                    //ツマミがタッチされた時に呼ばれる
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    //ツマミがリリースされた時に呼ばれる
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        items.get(position).prog = seekBar.getProgress();
                        for (int i = 0;i < items.size();i++){
                            System.out.println(items.get(i).prog + " " + position);
                        }
                        notifyDataSetChanged();
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

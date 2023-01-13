package com.example.makad.supporters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ProgressBar;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Intent;
import android.app.Activity;
import android.widget.ToggleButton;


public class PresentationFragment extends Fragment {

    private TextView timerText,maxTimerText,daihon;
    private ProgressBar progressBar;//タイマーの経過時間表示用
    private boolean is_count = false;//カウントダウン中か否か
    private int timer_flag = 0;//タイマーの効果音フラグ
    private int countMillis = 120000;//タイマーの現在値
    int countNumber = 120000;//タイマーの最大値
    private int minute = 2,second = 0;
    private String str = "だいほん";//台本
    // インターバル msec
    long interval = 100;
    CountDown countDown = new CountDown(countNumber, interval);
    MainActivity maActivity;

    private SimpleDateFormat dataFormat =
            new SimpleDateFormat("mm:ss", Locale.US);

    public void inputData(int min,int sec,String str){
        this.minute = min;
        this.second = sec;
        //this.daihon.setText(str);
        this.str = str;
        countNumber = (min * 60 + sec) * 1000;
        countMillis = countNumber;
        //maxTimerText.setText("/ " + dataFormat.format(countNumber));
        //progressBar.setMax(countNumber);//プログレスバーの最大値を変更
        //stopTimer();
    }

    public int getMin(){return this.minute;}

    public int getSec(){return this.second;}

    public String getDaihon() {return str;}

    //DialogFragmentからの返信をキャッチするメソッド
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.minute = data.getIntExtra("min",0);
        this.second = data.getIntExtra("sec",0);
        this.str = data.getStringExtra("str");
        daihon.setText(this.str);
        countNumber = (minute * 60 + second) * 1000;
        maxTimerText.setText("/ " + dataFormat.format(countNumber));
        progressBar.setMax(countNumber);//プログレスバーの最大値を変更
        stopTimer();
    }

    public void stopTimer(){
        countDown.cancel();
        timerText.setText(dataFormat.format(countNumber));
        countMillis = countNumber;//時間を初期化
        is_count = false;
        timer_flag = 0;//タイマーの進行度を初期化
        progressBar.setProgress(countMillis);//プログレスバーを初期化
        timerText.setTextColor(Color.BLUE);//タイマーの色を戻す
    }

    //DialogFragmentを呼ぶ際に使う
    public void setDialog(){
        TimerDialogFragment dialog = TimerDialogFragment.newInstance(this, 1);
        dialog.show(getFragmentManager(), "dialog");
    }

    //カウントダウンタイマー
    class CountDown extends CountDownTimer {

        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 完了
            timerText.setText(dataFormat.format(0));
        }

        // インターバルで呼ばれる
        @Override
        public void onTick(long millisUntilFinished) {
            countMillis = (int) millisUntilFinished;
            timerText.setText(dataFormat.format(countMillis));
            progressBar.setProgress(countMillis);
            if ((timer_flag == 2)&&(countMillis <= 999)) {
                timerText.setTextColor(Color.RED);
                timer_flag=3;
                maActivity = (MainActivity) getActivity();
                maActivity.playSe(1);
            }else if ((timer_flag == 1)&&(countMillis < 15999)){
                timerText.setTextColor(Color.rgb(255,120,0));
                timer_flag=2;
                maActivity = (MainActivity) getActivity();
                maActivity.playSe(0);
            }else if ((timer_flag == 0)&&(countMillis < 29999)){
                timer_flag=1;
                maActivity = (MainActivity) getActivity();
                maActivity.playSe(0);
            }else if (timer_flag == 0){
                timerText.setTextColor(Color.BLUE);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.presentation_fragment, container, false);

        // findViewByIdの前に「view」を付けてIdの取得
        final ToggleButton startButton = view.findViewById(R.id.start_button);
        Button stopButton = view.findViewById(R.id.stop_button);
        Button settingButton = view.findViewById(R.id.setting_button);
        daihon = view.findViewById(R.id.daihon);

        progressBar = view.findViewById(R.id.progressBar4);
        progressBar.setMax(countNumber);
        progressBar.setProgress(countNumber);

        timerText = view.findViewById(R.id.timer);
        timerText.setText(dataFormat.format(countNumber));

        maxTimerText = view.findViewById(R.id.timer_max);
        maxTimerText.setText("/ "+ dataFormat.format(countNumber));

        daihon.setText(str);


        // インスタンス生成
        // CountDownTimer(long millisInFuture, long countDownInterval)

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //タイマー設定用ダイアログの呼び出し
                setDialog();
            }
        });


        //再生～一時停止ボタン
        startButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener(){
                    public void onCheckedChanged(CompoundButton comButton, boolean isChecked){
                if (is_count == false){
                    // 開始
                    countDown = new CountDown(countMillis, 100);//countMillisを残り時間にセット
                    countDown.start();
                    timerText.setText(dataFormat.format(countMillis));
                    is_count = true;
                }else{
                    // 一時停止
                    countDown.cancel();
                    timerText.setText(dataFormat.format(countMillis));
                    is_count = false;
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //タイマーリセット
                startButton.setChecked(false);
                stopTimer();
            }
        });

        return view;
    }

    //タイマーの設定用ダイアログ
    public static class TimerDialogFragment extends DialogFragment {

        NumberPicker numpicmin;
        NumberPicker numpicsec;
        EditText daihon;
        int min,sec;


        public static TimerDialogFragment newInstance(Fragment target, int requestCode) {
            TimerDialogFragment fragment = new TimerDialogFragment();
            fragment.setTargetFragment(target, requestCode);

            Bundle args = new Bundle();
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = inflater.inflate(R.layout.timer_dialog, null);


            numpicmin = content.findViewById(R.id.numberPickerMin);
            numpicsec = content.findViewById(R.id.numberPickerSec);
            daihon = content.findViewById(R.id.editText02);
            Button setTimeButton = content.findViewById(R.id.settime_button);
            numpicmin.setMaxValue(59);
            numpicmin.setMinValue(0);
            numpicsec.setMaxValue(59);
            numpicsec.setMinValue(0);

            builder.setView(content);

            setTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    min = numpicmin.getValue();
                    sec = numpicsec.getValue();

                    if ((min * 60 + sec) < 30){
                        sec = 30;//30秒以下を排除
                    }

                    Dialog dialog = getDialog();
                    Intent result = new Intent();
                    result.putExtra("min",min);
                    result.putExtra("sec",sec);
                    result.putExtra("str",daihon.getText().toString());
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
    }
}

package com.example.makad.supporters;


import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * SoundManagerクラス　
 * 音源を管理するためのクラス
 * 　　音源は、res/raw　の中に入れておきます。
 * 　　このサンプルでは、
 * 　　　start.mp3、stop.mp3、end.mp3　を用意しています。
 *
 */

public class SoundManager {
    public static final int SOUND_ALERT = 0;
    public static final int SOUND_ALERT2 = 1;
    public static final int SOUND_END = 2;

    // 利用する効果音ファイルのリストを作ります（res/raw　内のファイル名と一致すること）
    private static final int[] SoundList = {
            //　形式：　R.raw.(ファイル名　拡張子除く)
            R.raw.cursor28,R.raw.bell02,R.raw.end
    };

    // 効果音を鳴らすSoundPool　音源を蓄えておくクラスのインスタンスを用意。
    private SoundPool mSoundPool;

    // 効果音のテーブル　　（サウンドファイルをIDEAの番号で管理する表です）
    private int mSoundTable[] = new int[SoundList.length];

    /***
     * 効果音の読み込み
     *
     * @param context
     */
    public SoundManager(Context context) {
        // SoundPoolの初期化をします。
        mSoundPool = new SoundPool(SoundList.length, AudioManager.STREAM_MUSIC,
                0);
        // SoundPoolを使って効果音をロードし、戻り値のIDをmSoundTableに保存する
        for (int i = 0; i < SoundList.length; i++) {
            // 具体的な音声ファイルを、IDで呼び出せるように、mSoundTableに登録します
            mSoundTable[i] = mSoundPool.load(context, SoundList[i], 1);
        }
    }

    /**
     * 効果音の再生
     *
     * @param no　テーブル内の音声ファイル番号
     * @param vol  音量   (0-100%)
     */

    public void play(int no, int vol) {
        if (no < 0 || no >= mSoundTable.length) {
            return;
        }
        float fvol = vol / 100;
        //
        // SoundPool　のメソッド　playへの引数の意味
        // 引数１:鳴らす効果音のID
        // 引数２:左のボリューム(0.0-1.0)
        // 引数３:右のボリューム(0.0-1.0)
        // 引数４　優先順位０が一番低い
        // 引数５　ループ回数（-1の場合は無限にループ、0の場合はループしない）
        // 引数６　再生速度（0.5?2.0：1.0で通常の速度）
        // 効果音の再生を再生する、ここではVolを左右同じにして再生しています。
        mSoundPool.play(mSoundTable[no], fvol, fvol, 0, 0, 1.0f);
    }

    /***
     * 効果音の解放
     */
    public void release() {
        // アプリ終了時には、読み込んでいた効果音の利用を解放します。
        // 保持すると、スマホ本体のメモリを圧迫することがあります。
        for (int i = 0; i < mSoundTable.length; i++) {
            mSoundPool.unload(mSoundTable[i]);
        }
        mSoundPool.release();
    }
}
package zoo.hymn.views.AudioRecorder;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.wb.R;

import java.util.Date;

import zoo.hymn.base.ui.BaseActivity;
import zoo.hymn.utils.AudioRecorderUtils;
import zoo.hymn.utils.DateUtil;

/**
 * ClassName: AudioRecorderDemo
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/23
 */

public class AudioRecorderDemo extends BaseActivity {
    Button btn,button;
    ImageView iv_record;
    TextView tv_time;
    AudioRecorderUtils audioRecorderUtils;
    String filePath = "";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        addChildView(R.layout.audio_record_demo);
    }

    @Override
    protected void initView() {
        button = (Button) findViewById(R.id.button1);
        btn = (Button) findViewById(R.id.button);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        tv_time = (TextView) findViewById(R.id.tv_time);

        button.setOnClickListener(this);
        audioRecorderUtils = new AudioRecorderUtils();

        //录音回调
        audioRecorderUtils.setOnAudioStatusUpdateListener(new AudioRecorderUtils.OnAudioStatusUpdateListener() {

            @Override
            public void onStart() {

            }

            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {
                Log.e("db", "db:" + db);
                //根据分贝值来设置录音时话筒图标的上下波动，下面有讲解
                iv_record.getDrawable().setLevel(Math.abs((int)db-38));
//                iv_record.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                tv_time.setText(DateUtil.dateToStr(new Date(time),"mm:ss"));
            }

            //录音结束，filePath为保存路径
            @Override
            public void onStop(String path, long time) {
                filePath = path;
                Toast.makeText(AudioRecorderDemo.this, "录音保存在：" + filePath, Toast.LENGTH_SHORT).show();
                iv_record.getDrawable().setLevel(1);
                tv_time.setText(DateUtil.dateToStr(new Date(0),"mm:ss"));
            }
        });

        //Button的touch监听
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:

                        audioRecorderUtils.startRecord();

                        break;

                    case MotionEvent.ACTION_UP:

                        audioRecorderUtils.stopRecord();        //结束录音（保存录音文件）

                        break;

                    case MotionEvent.ACTION_MOVE:

//                        audioRecorderUtils.cancelRecord();    //取消录音（不保存录音文件）

                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {

    }
}

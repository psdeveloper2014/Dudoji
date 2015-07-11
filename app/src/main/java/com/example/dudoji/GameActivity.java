package com.example.dudoji;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;


public class GameActivity extends ActionBarActivity {

    TextView score_tv, time_tv;

    // 0 : 없음, 1 : 두더지, 2 : 차장, 3 : 부장
    ImageButton button[] = new ImageButton[9];
    int status[] = new int[9];

    int score = 0;

    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameview);

        score_tv = (TextView) findViewById(R.id.score_tv);
        time_tv = (TextView) findViewById(R.id.time_tv);

        // 시간을 측정할 쓰레드 시작
        TimeCountThread timeCountThread = new TimeCountThread();
        timeCountThread.start();

        button[0] = (ImageButton) findViewById(R.id.button00);
        button[1] = (ImageButton) findViewById(R.id.button01);
        button[2] = (ImageButton) findViewById(R.id.button02);
        button[3] = (ImageButton) findViewById(R.id.button03);
        button[4] = (ImageButton) findViewById(R.id.button04);
        button[5] = (ImageButton) findViewById(R.id.button05);
        button[6] = (ImageButton) findViewById(R.id.button06);
        button[7] = (ImageButton) findViewById(R.id.button07);
        button[8] = (ImageButton) findViewById(R.id.button08);

        // 무작위로 아이템을 배열한다
        setRandomItem();

        // 버튼을 클릭했을 때 작동할 이벤트 설정
        for (i=0; i<9; i++) {
            button[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageScoreWhenClicks(i);
                }
            });
        }
    }

    // 무작위로 아이템을 배치하는 함수
    private void setRandomItem() {
        // 위치 정보 초기화
        for (i=0; i<9; i++) {
            status[i] = 0;
        }

        // 아이템이 나올 위치를 0에서 8까지 총 9개를 랜덤으로 정하기
        Random rand = new Random();
        int dudojiPos = rand.nextInt(9);
        int chajangPos = rand.nextInt(9);
        int bujangPos = rand.nextInt(9);

        // 두더지의 위치 저장
        status[dudojiPos] = 1;

        // 차장의 위치가 두더지의 위치와 겹치지 않는다면 차장의 위치 저장하기
        if (chajangPos != dudojiPos) {
            status[chajangPos] = 2;
        }

        // 부장의 위치가 두더지의 위치와 차장의 위치와 겹치지 않는다면 부장의 위치 저장하기
        if (bujangPos != dudojiPos && bujangPos != chajangPos) {
            status[bujangPos] = 3;
        }

        // 저장된 위치 표시하기
        for (i=0; i<9; i++) {
            // 빈칸
            if (status[i] == 0) {
                button[i].setImageDrawable(getResources().getDrawable(R.drawable.none));
            }
            // 두더지
            else if (status[i] == 1) {
                button[i].setImageDrawable(getResources().getDrawable(R.drawable.dudoji));
            }
            // 차장
            else if (status[i] == 2) {
                button[i].setImageDrawable(getResources().getDrawable(R.drawable.chajang));
            }
            // 부장
            else {
                button[i].setImageDrawable(getResources().getDrawable(R.drawable.bujang));
            }
        }
    }

    // 버튼을 클릭했을 때 작동하며 점수를 관리하는 함수
    private void manageScoreWhenClicks(int n) {
        // 빈칸을 클릭하면
        if (status[n] == 0) {
            return;
        }
        // 두더지를 클릭하면
        else if (status[n] == 1) {
            score = score + 5;
        }
        // 차장을 클릭하면
        else if (status[n] == 2) {
            score = score - 10;
        }
        // 부장을 클릭하면
        else {
            score = 0;
        }

        // 점수를 화면에 표시한다
        score_tv.setText(getResources().getString(R.string.score) + score);

        // 무작위로 아이템 배치하기
        setRandomItem();
    }

    // 시간을 관리하는 클래스
    class TimeCountThread extends Thread {

        int time = 30;

        public TimeCountThread() {
            // TimeCountThread 를 등록할 때 쓰임
        }

        public void run() {
            while (time >= 0) {
                if (time == 0) {
                    // MainActivity 에서 점수를 표시하는 다이얼로그를
                    // 띄울 수 있도록 점수 값 전달하고 액티비티 종료
                    Intent intent = getIntent();
                    intent.putExtra("score", score);
                    setResult(0, intent);
                    finish();
                } else {
                    // 시간을 화면에 표시하도록 하는 핸들러 호출
                    timeCountHandler.sendEmptyMessage(time);
                }

                // 시간을 1초 보낸다
                try {
                    Thread.sleep(1000);
                    time--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // 시간을 화면에 표시하는 핸들러
        private Handler timeCountHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                // 시간 표시
                time_tv.setText(getResources().getString(R.string.time) + msg.what);
            }
        };
    }
}
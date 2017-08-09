package com.example.munseongsu.preypredator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    Activity activity;                                                          //엑티비티 Context
    int dungeonWidth, dungeonHeight, divDungeonWidth, divDungeonHeight;         //디바이스 가로세로 크기 관련 변수
    FrameLayout dungeon;                                                        //배경 레이아웃 (던전)
    ImageView preg;                                                             //먹잇감
    ImageView[] predator;                                                       //몬스터들
    int[] horizontalPosition, verticalPosition;                                 //x, y Position
    Button startBtn, resetBtn;                                                  //시작, 리셋 버튼
    int width, height;                                                          //preg, predator 크기
    int pregWay;                                                                //돼지 이동 방향
    int pregX, pregY;                                                           //돼지 X,Y
    int[] predX = new int[4];                                                   //사냥꾼 X
    int[] predY = new int[4];                                                   //사냥꾼 Y
    boolean first_condition;                                                    //첫번쨰 종료조건을 위한 변수
    boolean second_condition;                                                   //두번쨰 종료조건을 위한 변수
    boolean third_condition;                                                    //세번쨰 종료조건을 위한 변수
    boolean fourth_condition;                                                   //네번쨰 종료조건을 위한 변수
    boolean condition_iscomplete;                                               //첫번쨰 종료조건함수와 두번째 종료조건함수가 참일경우에만 참인 변수
    boolean three_condition_iscomplete;                                         //1,2,3의 종료조건함수가 모두다 참일경우 참인 변수
    boolean four_condition_iscomplete;                                          //1,2,3,4의 종료조건 함수가 모두 다 참일경우 참인 변수
    static final int RIGHT = 0;
    static final int UP = 1;
    static final int LEFT = 2;
    static final int DOWN = 3;
    static final int STAY = 4;
    double Compare_right[];                                                     // 각각의 predator에서 오른쪽으로 +1 한 위치에서 prey 까지의 거리가 들어가는 변수
    double Compare_left[];                                                      // 각각의 predator에서 오른쪽으로 +1 한 위치에서 prey 까지의 거리가 들어가는 변수
    double Compare_up[];                                                        // 각각의 predator에서 위쪽으로 +1 한 위치에서 prey 까지의 거리가 들어가는 변수
    double Compare_down[];                                                      // 각각의 predator에서 아래쪽으로 +1 한 위치에서 prey 까지의 거리가 들어가는 변수
    double Compare_total[];                                                     // 위의 4가지 변수들의 거리값중 최단거리부터 마지막 제일 느린 거리까지를 비교하기위한 변수
    int the_shortest_index[],second_short_index[],third_short_index[],fourth_short_index[]; // Compare_total 배열에서 가장 작은것과 그다음, 또 그다음 마지막 까지의 배열변수들의 인덱스를 각각 넣어주기위한 배열들
    boolean is_start = false;                                                   //시작, 종료 조건
    TextView preyMoveCntTv, predMoveCntTv;                                      //이동 횟수들 텍뷰
    int preyMoveCnt, predMoveCnt;                                               //이동한 횟수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SET Resource
        activity = this;
        dungeon = (FrameLayout) findViewById(R.id.dungeon);
        preg = (ImageView) findViewById(R.id.preg);
        predator = new ImageView[4];
        predator[0] = (ImageView) findViewById(R.id.predator1);
        predator[1] = (ImageView) findViewById(R.id.predator2);
        predator[2] = (ImageView) findViewById(R.id.predator3);
        predator[3] = (ImageView) findViewById(R.id.predator4);
        horizontalPosition = new int[11];
        verticalPosition = new int[11];
        startBtn = (Button) findViewById(R.id.start_btn);
        resetBtn = (Button) findViewById(R.id.reset_btn);
        preyMoveCntTv = (TextView) findViewById(R.id.prey_move_cnt);
        predMoveCntTv = (TextView) findViewById(R.id.pred_move_cnt);
        preyMoveCnt = predMoveCnt = 0;

        //SET dungeon, preg, predators
        //OnGlobalLayoutListener는 화면에 뷰가 그려진 이후에 실행되는 이벤트
        dungeon.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                dungeon.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initGame();

            }
        });

        //게임리셋
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn.setVisibility(View.VISIBLE);
                startBtn.setText("Start!");
                resetBtn.setVisibility(View.GONE);
                initGame();
            }
        });

        //게임시작
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if(!is_start) {
                    is_start = true;
                    startBtn.setText("Hunting..."); //버튼 비활성화 (클릭 불가능한 상태)
                    startBtn.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (is_start) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException ignore) {
                                    Log.i("InterruptedExceoption", ignore + "");
                                } finally {
                                    Log.i("Game", "thread dead");
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if ((pregX == 0 && pregY == 0) || (pregX == 0 && pregY == 10) || (pregX == 10 && pregY == 10) || (pregX == 10 && pregY == 0)) { //prey가 제일끝 모서리 4군데에 있을경우 종료조건
                                            if ((pregX == 0 && pregY == 0)) {
                                                for (int i = 0; i <= predX.length - 1; i++) {
                                                    if (predX[i] == 1 && predY[i] == 0) {
                                                        first_condition = true;
                                                    }
                                                    if (predX[i] == 0 && predY[i] == 1) {
                                                        second_condition = true;
                                                    }
                                                }
                                                condition_iscomplete = (first_condition && second_condition);
                                                if (condition_iscomplete) {
                                                    Log.i("condition_iscomplete", "true1");
                                                    preg.setImageResource(R.drawable.preg_cook);
                                                    startBtn.setEnabled(true);
                                                    startBtn.setVisibility(View.GONE);
                                                    resetBtn.setVisibility(View.VISIBLE);
                                                    is_start = false;
                                                } else {
                                                    first_condition = false;
                                                    second_condition = false;
                                                }
                                            } else if ((pregX == 0 && pregY == 10)) {
                                                for (int i = 0; i <= predX.length - 1; i++) {
                                                    if (predX[i] == 1 && predY[i] == 10) {
                                                        first_condition = true;
                                                    }
                                                    if (predX[i] == 0 && predY[i] == 9) {
                                                        second_condition = true;
                                                    }
                                                }
                                                condition_iscomplete = (first_condition && second_condition);
                                                if (condition_iscomplete) {
                                                    Log.i("condition_iscomplete", "true2");
                                                    preg.setImageResource(R.drawable.preg_cook);
                                                    startBtn.setEnabled(true);
                                                    startBtn.setVisibility(View.GONE);
                                                    resetBtn.setVisibility(View.VISIBLE);
                                                    is_start = false;
                                                } else {
                                                    first_condition = false;
                                                    second_condition = false;
                                                }
                                            } else if ((pregX == 10 && pregY == 10)) {
                                                for (int i = 0; i <= predX.length - 1; i++) {
                                                    if (predX[i] == 9 && predY[i] == 10) {
                                                        first_condition = true;
                                                    }
                                                    if (predX[i] == 10 && predY[i] == 9) {
                                                        second_condition = true;
                                                    }
                                                }
                                                condition_iscomplete = (first_condition && second_condition);
                                                if (condition_iscomplete) {
                                                    Log.i("condition_iscomplete", "true3");
                                                    preg.setImageResource(R.drawable.preg_cook);
                                                    startBtn.setEnabled(true);
                                                    startBtn.setVisibility(View.GONE);
                                                    resetBtn.setVisibility(View.VISIBLE);
                                                    is_start = false;
                                                } else {
                                                    first_condition = false;
                                                    second_condition = false;
                                                }
                                            } else if ((pregX == 10 && pregY == 0)) {
                                                for (int i = 0; i <= predX.length - 1; i++) {
                                                    if (predX[i] == 10 && predY[i] == 1) {
                                                        first_condition = true;
                                                    }
                                                    if (predX[i] == 9 && predY[i] == 0) {
                                                        second_condition = true;
                                                    }
                                                }
                                                condition_iscomplete = (first_condition && second_condition);
                                                if (condition_iscomplete) {
                                                    Log.i("condition_iscomplete", "true4");
                                                    preg.setImageResource(R.drawable.preg_cook);
                                                    startBtn.setEnabled(true);
                                                    startBtn.setVisibility(View.GONE);
                                                    resetBtn.setVisibility(View.VISIBLE);
                                                    is_start = false;
                                                } else {
                                                    first_condition = false;
                                                    second_condition = false;
                                                }
                                            }
                                        } else if ((pregX == 0 && pregY == 1) || (pregX == 0 && pregY == 2) || (pregX == 0 && pregY == 3) || (pregX == 0 && pregY == 4) || (pregX == 0 && pregY == 5) || (pregX == 0 && pregY == 6) || (pregX == 0 && pregY == 7) || (pregX == 0 && pregY == 8) || (pregX == 0 && pregY == 9)) {//prey가 왼쪽 끝 (0,1)~(0,9) 까지의 위치에 있을 경우의 종료조건
                                            for (int i = 0; i <= predX.length - 1; i++) {
                                                if (predX[i] == pregX + 1 && predY[i] == pregY) {
                                                    first_condition = true;
                                                }
                                                if (predX[i] == pregX && predY[i] == pregY + 1) {
                                                    second_condition = true;
                                                }
                                                if (predX[i] == pregX && predY[i] == pregY - 1) {
                                                    third_condition = true;
                                                }
                                            }
                                            three_condition_iscomplete = (first_condition && second_condition && third_condition);
                                            if (three_condition_iscomplete) {
                                                Log.i("three_condition_iscomplete", "true1");
                                                preg.setImageResource(R.drawable.preg_cook);
                                                startBtn.setEnabled(true);
                                                startBtn.setVisibility(View.GONE);
                                                resetBtn.setVisibility(View.VISIBLE);
                                                is_start = false;
                                            } else {
                                                first_condition = false;
                                                second_condition = false;
                                                third_condition = false;
                                            }
                                        } else if ((pregX == 1 && pregY == 10) || (pregX == 2 && pregY == 10) || (pregX == 3 && pregY == 10) || (pregX == 4 && pregY == 10) || (pregX == 5 && pregY == 10) || (pregX == 6 && pregY == 10) || (pregX == 7 && pregY == 10) || (pregX == 8 && pregY == 10) || (pregX == 9 && pregY == 10)) {//prey가 제일 위쪽 끝 (1,10)~(9,10) 까지의 위치에 있을 경우의 종료조건
                                            for (int i = 0; i <= predX.length - 1; i++) {
                                                if (predX[i] == pregX + 1 && predY[i] == pregY) {
                                                    first_condition = true;
                                                }
                                                if (predX[i] == pregX - 1 && predY[i] == pregY) {
                                                    second_condition = true;
                                                }
                                                if (predX[i] == pregX && predY[i] == pregY - 1) {
                                                    third_condition = true;
                                                }
                                            }
                                            three_condition_iscomplete = (first_condition && second_condition && third_condition);
                                            if (three_condition_iscomplete) {
                                                Log.i("three_condition_iscomplete", "true2");
                                                preg.setImageResource(R.drawable.preg_cook);
                                                startBtn.setEnabled(true);
                                                startBtn.setVisibility(View.GONE);
                                                resetBtn.setVisibility(View.VISIBLE);
                                                is_start = false;
                                            } else {
                                                first_condition = false;
                                                second_condition = false;
                                                third_condition = false;
                                            }
                                        } else if ((pregX == 10 && pregY == 1) || (pregX == 10 && pregY == 2) || (pregX == 10 && pregY == 3) || (pregX == 10 && pregY == 4) || (pregX == 10 && pregY == 5) || (pregX == 10 && pregY == 6) || (pregX == 10 && pregY == 7) || (pregX == 10 && pregY == 8) || (pregX == 10 && pregY == 9)) { //prey가 오른쪽 끝 (10,1)~(10,9) 까지의 위치에 있을 경우의 종료조건
                                            for (int i = 0; i <= predX.length - 1; i++) {
                                                if (predX[i] == pregX && predY[i] == pregY - 1) {
                                                    first_condition = true;
                                                } if (predX[i] == pregX && predY[i] == pregY + 1) {
                                                    second_condition = true;
                                                } if (predX[i] == pregX - 1 && predY[i] == pregY) {
                                                    third_condition = true;
                                                }
                                            }
                                            three_condition_iscomplete = (first_condition && second_condition && third_condition);
                                            if (three_condition_iscomplete) {
                                                Log.i("three_condition_iscomplete", "true3");
                                                preg.setImageResource(R.drawable.preg_cook);
                                                startBtn.setEnabled(true);
                                                startBtn.setVisibility(View.GONE);
                                                resetBtn.setVisibility(View.VISIBLE);
                                                is_start = false;
                                            } else {
                                                first_condition = false;
                                                second_condition = false;
                                                third_condition = false;
                                            }
                                        } else if ((pregX == 1 && pregY == 0) || (pregX == 2 && pregY == 0) || (pregX == 3 && pregY == 0) || (pregX == 4 && pregY == 0) || (pregX == 5 && pregY == 0) || (pregX == 6 && pregY == 0) || (pregX == 7 && pregY == 0) || (pregX == 8 && pregY == 0) || (pregX == 9 && pregY == 0)) {//prey가 제일 아래쪽 (1,0)~(9,0) 까지의 위치에 있을 경우의 종료조건
                                            for (int i = 0; i <= predX.length - 1; i++) {
                                                if (predX[i] == pregX + 1 && predY[i] == pregY) {
                                                    first_condition = true;
                                                }
                                                if (predX[i] == pregX - 1 && predY[i] == pregY) {
                                                    second_condition = true;
                                                }
                                                if (predX[i] == pregX && predY[i] == pregY + 1) {
                                                    third_condition = true;
                                                }
                                            }
                                            three_condition_iscomplete = (first_condition && second_condition && third_condition);
                                            if (three_condition_iscomplete) {
                                                Log.i("three_condition_iscomplete", "true4");
                                                preg.setImageResource(R.drawable.preg_cook);
                                                startBtn.setEnabled(true);
                                                startBtn.setVisibility(View.GONE);
                                                resetBtn.setVisibility(View.VISIBLE);
                                                is_start = false;
                                            } else {
                                                first_condition = false;
                                                second_condition = false;
                                                third_condition = false;
                                            }
                                        } else { //나머지 모든 가운데에 있을경우의 종료조건
                                            for (int i = 0; i <= predX.length - 1; i++) {
                                                if (predX[i] == pregX + 1 && predY[i] == pregY) {
                                                    first_condition = true;
                                                }
                                                if (predX[i] == pregX && predY[i] == pregY + 1) {
                                                    second_condition = true;
                                                }
                                                if (predX[i] == pregX && predY[i] == pregY - 1) {
                                                    third_condition = true;
                                                }
                                                if (predX[i] == pregX - 1 && predY[i] == pregY) {
                                                    fourth_condition = true;
                                                }
                                            }
                                            four_condition_iscomplete = (first_condition && second_condition && third_condition && fourth_condition);
                                            if (four_condition_iscomplete) {
                                                Log.i("four_condition_iscomplete", "true");
                                                preg.setImageResource(R.drawable.preg_cook);
                                                startBtn.setEnabled(true);
                                                startBtn.setVisibility(View.GONE);
                                                resetBtn.setVisibility(View.VISIBLE);
                                                is_start = false;
                                            } else {
                                                first_condition = false;
                                                second_condition = false;
                                                third_condition = false;
                                                fourth_condition = false;
                                            }
                                        }//여기까지가 종료조건

                                        if (is_start) {
                                            //돼지를 실제로 움직이기 전에 방향별로 한칸 이동했을 때 사냥꾼들과의 충돌을 검사
                                            while (true) {
                                                pregWay = (int) (Math.random() * 5); //돼지의 움직임은 총 5가지
                                                if (pregWay == RIGHT) {
                                                    for (int i = 0; i < predX.length; i++) {
                                                        if (pregX + 1 == predX[i] && pregY == predY[i])
                                                            continue;
                                                    }
                                                    break;
                                                } else if (pregWay == UP) {
                                                    for (int i = 0; i < predX.length; i++) {
                                                        if (pregX == predX[i] && pregY + 1 == predY[i])
                                                            continue;
                                                    }
                                                    break;
                                                } else if (pregWay == LEFT) {
                                                    for (int i = 0; i < predX.length; i++) {
                                                        if (pregX - 1 == predX[i] && pregY == predY[i])
                                                            continue;
                                                    }
                                                    break;
                                                } else if (pregWay == DOWN) {
                                                    for (int i = 0; i < predX.length; i++) {
                                                        if (pregX == predX[i] && pregY - 1 == predY[i])
                                                            continue;
                                                    }
                                                    break;
                                                } else {
                                                    break;
                                                }
                                            }
                                            Log.i("way", pregWay + "");

                                            //돼지 실질적 이동
                                            FrameLayout.LayoutParams pregParams = new FrameLayout.LayoutParams(preg.getLayoutParams());
                                            switch (pregWay) {
                                                case RIGHT:
                                                    moveRightPreg(pregParams);
                                                    break;
                                                case UP:
                                                    moveUpPreg(pregParams);
                                                    break;
                                                case LEFT:
                                                    moveLeftPreg(pregParams);
                                                    break;
                                                case DOWN:
                                                    moveDownPreg(pregParams);
                                                    break;
                                                case STAY:
                                                    break;
                                            }

                                            //사냥꾼
                                            FrameLayout.LayoutParams predator1Params = new FrameLayout.LayoutParams(predator[0].getLayoutParams());
                                            FrameLayout.LayoutParams predator2Params = new FrameLayout.LayoutParams(predator[1].getLayoutParams());
                                            FrameLayout.LayoutParams predator3Params = new FrameLayout.LayoutParams(predator[2].getLayoutParams());
                                            FrameLayout.LayoutParams predator4Params = new FrameLayout.LayoutParams(predator[3].getLayoutParams());

                                            for (int i = 0; i <= 3; i++) {
                                                Compare_right[i] = Math.sqrt(Math.pow((predX[i] + 1) - pregX, 2) + (Math.pow(predY[i] - pregY, 2)));
                                                Compare_up[i] = Math.sqrt(Math.pow((predX[i]) - pregX, 2) + (Math.pow((predY[i] + 1) - pregY, 2)));
                                                Compare_left[i] = Math.sqrt(Math.pow((predX[i] - 1) - pregX, 2) + (Math.pow(predY[i] - pregY, 2)));
                                                Compare_down[i] = Math.sqrt(Math.pow((predX[i]) - pregX, 2) + (Math.pow((predY[i] - 1) - pregY, 2)));
                                            }

                                            int jj;
                                            for (jj = 0; jj <= 3; jj++) { // 동서남북중 가장 작은거리 4곳을 걸러낸다.
                                                Compare_total[0] = Compare_right[jj];
                                                Compare_total[1] = Compare_up[jj];
                                                Compare_total[2] = Compare_left[jj];
                                                Compare_total[3] = Compare_down[jj];
                                                double the_smallest_value = 999999999;
                                                double second_small_value = 999999999;
                                                double third_small_value = 99999999;
                                                double fourth_small_value = 999999999;
                                                int tmp1 = 0;
                                                int tmp2 = 0;
                                                int tmp3 = 0;
                                                int tmp4 = 0;
                                                for (int i = 0; i <= Compare_total.length - 1; i++) { //가장 작은값의 거리 인덱스 구하기
                                                    if (the_smallest_value > Compare_total[i]) {
                                                        the_smallest_value = Compare_total[i];
                                                        tmp1 = i;
                                                    }
                                                }
                                                the_shortest_index[jj] = tmp1;
                                                for (int i = 0; i < Compare_total.length - 1; i++) // 두번쨰로 작은값의 거리 인덱스 구하기
                                                {
                                                    if (second_small_value > Compare_total[i] && i != the_shortest_index[jj]) {
                                                        second_small_value = Compare_total[i];
                                                        tmp2 = i;
                                                    }
                                                }
                                                second_short_index[jj] = tmp2;
                                                for (int i = 0; i < Compare_total.length - 1; i++) // 세번째 ''
                                                {
                                                    if (third_small_value > Compare_total[i] && i != the_shortest_index[jj] && i != second_short_index[jj]) {
                                                        third_small_value = Compare_total[i];
                                                        tmp3 = i;
                                                    }
                                                }
                                                third_short_index[jj] = tmp3;
                                                for (int i = 0; i < Compare_total.length - 1; i++) // 네번째 ''
                                                {
                                                    if (fourth_small_value > Compare_total[i] && i != the_shortest_index[jj] && i != second_short_index[jj] && i != third_short_index[jj]) {
                                                        fourth_small_value = Compare_total[i];
                                                        tmp4 = i;
                                                    }
                                                }
                                                fourth_short_index[jj] = tmp4;
                                            }

                                            //파라미터 : 사냥꾼번호, 최단거리 순서대로
                                            movePredator(0, the_shortest_index[0], second_short_index[0], third_short_index[0], fourth_short_index[0], predator1Params);
                                            movePredator(1, the_shortest_index[1], second_short_index[1], third_short_index[1], fourth_short_index[1], predator2Params);
                                            movePredator(2, the_shortest_index[2], second_short_index[2], third_short_index[2], fourth_short_index[2], predator3Params);
                                            movePredator(3, the_shortest_index[3], second_short_index[3], third_short_index[3], fourth_short_index[3], predator4Params);
                                        }
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });
    };


    //사냥꾼 이동
    void movePredator(int predatorNumber, int the_shortest,int second_short,int third_short,int fourth_short, FrameLayout.LayoutParams predatorParams){
        if(Math.sqrt(Math.pow(predX[predatorNumber]-pregX,2)+
            Math.sqrt(Math.pow(predY[predatorNumber]-pregY,2)))==Math.sqrt(2)) { //대각선에 있을경우우
            if (predatorNumber == 0) {
                if(pregX==predX[predatorNumber]+1 && pregY==predY[predatorNumber])
                {moveDownPred(predatorParams, predatorNumber);}
                moveRightPred(predatorParams, predatorNumber);
            }
            else if(predatorNumber == 1){
                if(pregX==predX[predatorNumber] && pregY==predY[predatorNumber]-1)
                {moveLeftPred(predatorParams, predatorNumber);}
                moveDownPred(predatorParams, predatorNumber);
            }
            else if(predatorNumber == 2){
                if(pregX==predX[predatorNumber] && pregY==predY[predatorNumber]+1)
                {moveRightPred(predatorParams, predatorNumber);}
                moveUpPred(predatorParams, predatorNumber);
            }
            else {
                if(pregX==predX[predatorNumber]-1 && pregY==predY[predatorNumber])
                {moveUpPred(predatorParams, predatorNumber);}
                moveLeftPred(predatorParams, predatorNumber);
            }
        }
        if (Math.sqrt(Math.pow(predX[predatorNumber]-pregX,2)+ Math.sqrt(Math.pow(predY[predatorNumber]-pregY,2)))!=1 &&
                Math.sqrt(Math.pow(predX[predatorNumber]-pregX,2)+ Math.sqrt(Math.pow(predY[predatorNumber]-pregY,2)))!=Math.sqrt(2)){
            int way_array[]=new int[4];
            way_array[0]=the_shortest;
            way_array[1]=second_short;
            way_array[2]=third_short;
            way_array[3]=fourth_short;
            int order=0;                //4개의 거리(way_array) 중 실제로 이동에 쓰일 배열의 인덱스 번호
            int way=way_array[order];   //처음엔 당연히 우선적으로 가장 빠른 거리로 초기화
            boolean Loop=true;
            finalLoop:  while(Loop==true){
              Loop=false;
              otherLoop:switch(way) {
                case RIGHT:
                    //사냥꾼끼리 겹치지 않도록
                    for(int i=0;i<=predX.length-1;i++){
                        if(i!=predatorNumber && predX[i]==predX[predatorNumber]+1 && predY[i]==predY[predatorNumber]) {
                            if(order==3)
                               break finalLoop;
                            order=order+1;  //ex) 배열의 첫번째에 들어있는 최단거리로 가면 겹치므로 두번째로 인덱스 +1
                            way=way_array[order];
                            Loop=true;
                            break otherLoop;
                        }
                        if(pregX==predX[predatorNumber]+1&&pregY==predY[predatorNumber])
                        {
                            if(order==3)
                                break finalLoop;
                            order=order+1;  //ex) 배열의 첫번째에 들어있는 최단거리로 가면 겹치므로 두번째로 인덱스 +1
                            way=way_array[order];
                            Loop=true;
                            break otherLoop;
                        }
                    }
                    //moveRightPred 파라미터 : 움직일 사냥꾼의 layoutParams와 사냥꾼의 인덱스번호.
                    //즉, 아래는 n번쨰 사냥꾼이 움직이도록 호출한 메서드
                    moveRightPred(predatorParams, predatorNumber);
                    break;
                case UP:
                    for(int i=0;i<=predX.length-1;i++){
                        if(i!=predatorNumber && predX[i]==predX[predatorNumber] && predY[i]==predY[predatorNumber]+1) {
                            if(order==3)
                                break finalLoop;
                            order=order+1;
                            way=way_array[order];
                            Loop=true;
                            break otherLoop;
                        }
                        if(pregX==predX[predatorNumber]&&pregY==predY[predatorNumber]+1)
                        {
                            if(order==3)
                                break finalLoop;
                            order=order+1;  //ex) 배열의 첫번째에 들어있는 최단거리로 가면 겹치므로 두번째로 인덱스 +1
                            way=way_array[order];
                            Loop=true;
                            break otherLoop;
                        }
                    }
                    moveUpPred(predatorParams, predatorNumber);
                    break;
                case LEFT:
                    for(int i=0;i<=predX.length-1;i++){
                        if(i!=predatorNumber && predX[i]==predX[predatorNumber]-1 && predY[i]==predY[predatorNumber]) {
                            if(order==3)
                                break finalLoop;
                            order=order+1;
                            way=way_array[order];
                            Loop=true;
                            break otherLoop;
                        }
                        if(pregX==predX[predatorNumber]-1&&pregY==predY[predatorNumber])
                        {
                            if(order==3)
                                break finalLoop;
                            order=order+1;  //ex) 배열의 첫번째에 들어있는 최단거리로 가면 겹치므로 두번째로 인덱스 +1
                            way=way_array[order];
                            Loop=true;
                            break otherLoop;
                        }
                    }
                    moveLeftPred(predatorParams, predatorNumber);
                    break;
                case DOWN:
                    for(int i=0;i<=predX.length-1;i++){
                        if(i!=predatorNumber && predX[i]==predX[predatorNumber] && predY[i]==predY[predatorNumber]-1) {
                            if(order==3)
                                break finalLoop;
                            order=order+1;
                            way=way_array[order];
                            Loop=true;
                            break otherLoop;
                        }
                        if(pregX==predX[predatorNumber]&&pregY==predY[predatorNumber]-1)
                        {
                            if(order==3)
                                break finalLoop;
                            order=order+1;  //ex) 배열의 첫번째에 들어있는 최단거리로 가면 겹치므로 두번째로 인덱스 +1
                            way=way_array[order];
                            Loop=true;
                            break otherLoop;
                        }
                    }
                    moveDownPred(predatorParams, predatorNumber);
                    break;
                }
            }
        }

    }

    //돼지 무브 함수들
    void moveRightPreg(FrameLayout.LayoutParams pregParams){
        pregX++;
        if(pregX < 11){ //배열 인덱스 넘지 않게
            pregParams.leftMargin = horizontalPosition[pregX];
            pregParams.topMargin = verticalPosition[pregY];
            preg.setLayoutParams(pregParams);
            preyMoveCnt++;
            preyMoveCntTv.setText(String.valueOf(preyMoveCnt));
        } else { //맨 끝에 도달하면
            pregX--;
        }
    }
    void moveUpPreg(FrameLayout.LayoutParams pregParams){
        pregY++;
        if(pregY < 11){
            pregParams.leftMargin = horizontalPosition[pregX];
            pregParams.topMargin = verticalPosition[pregY];
            preg.setLayoutParams(pregParams);
            preyMoveCnt++;
            preyMoveCntTv.setText(String.valueOf(preyMoveCnt));
        } else {
            pregY--;
        }
    }
    void moveLeftPreg(FrameLayout.LayoutParams pregParams){
        pregX--;
        if(pregX >=0){
            pregParams.leftMargin = horizontalPosition[pregX];
            pregParams.topMargin = verticalPosition[pregY];
            preg.setLayoutParams(pregParams);
            preyMoveCnt++;
            preyMoveCntTv.setText(String.valueOf(preyMoveCnt));
        } else {
            pregX++;
        }
    }
    void moveDownPreg(FrameLayout.LayoutParams pregParams){
        pregY--;
        if(pregY >= 0){
            pregParams.leftMargin = horizontalPosition[pregX];
            pregParams.topMargin = verticalPosition[pregY];
            preg.setLayoutParams(pregParams);
            preyMoveCnt++;
            preyMoveCntTv.setText(String.valueOf(preyMoveCnt));
        } else {
            pregY++;
        }
    }


    //사냥꾼들 무브 함수들
    void moveRightPred(FrameLayout.LayoutParams pregParams, int predNumber) {
        predX[predNumber]++;
        if(predX[predNumber] < 11){ //배열 인덱스 넘지 않게
            pregParams.leftMargin = horizontalPosition[predX[predNumber]];
            pregParams.topMargin = verticalPosition[predY[predNumber]];
            predator[predNumber].setLayoutParams(pregParams);
            predMoveCnt++;
            predMoveCntTv.setText(String.valueOf(predMoveCnt));
        } else { //맨 끝에 도달하면
            predX[predNumber]--;
        }
    }
    void moveUpPred(FrameLayout.LayoutParams pregParams, int predNumber) {
        predY[predNumber]++;
        if(predY[predNumber] < 11){
            pregParams.leftMargin = horizontalPosition[predX[predNumber]];
            pregParams.topMargin = verticalPosition[predY[predNumber]];
            predator[predNumber].setLayoutParams(pregParams);
            predMoveCnt++;
            predMoveCntTv.setText(String.valueOf(predMoveCnt));
        } else {
            predY[predNumber]--;
        }
    }
    void moveLeftPred(FrameLayout.LayoutParams pregParams, int predNumber) {
        predX[predNumber]--;
        if(predX[predNumber] >= 0){
            pregParams.leftMargin = horizontalPosition[predX[predNumber]];
            pregParams.topMargin = verticalPosition[predY[predNumber]];
            predator[predNumber].setLayoutParams(pregParams);
            predMoveCnt++;
            predMoveCntTv.setText(String.valueOf(predMoveCnt));
        } else {
            predX[predNumber]++;
        }
    }
    void moveDownPred(FrameLayout.LayoutParams pregParams, int predNumber) {
        predY[predNumber]--;
        if(predY[predNumber] >= 0){
            pregParams.leftMargin = horizontalPosition[predX[predNumber]];
            pregParams.topMargin = verticalPosition[predY[predNumber]];
            predator[predNumber].setLayoutParams(pregParams);
            predMoveCnt++;
            predMoveCntTv.setText(String.valueOf(predMoveCnt));
        } else {
            predY[predNumber]++;
        }
    }



    /**
     * 게임 초기화
     * 던전, 주인공, 몬스터 사이즈 및 위치 값 초기값으로..
     */
    void initGame(){
        dungeonWidth = dungeon.getMeasuredWidth();
        dungeonHeight = dungeon.getMeasuredHeight();
        divDungeonWidth = dungeonWidth / 11;
        divDungeonHeight = dungeonHeight / 11;
        Log.i("dungeon size", dungeonWidth + "x" + dungeonHeight);
        Log.i("dungeon size after div", divDungeonWidth + "x" + divDungeonHeight);

        //Paint line
        for (int i = 0; i <= 11; i++) {
            View view = new View(activity);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(2, dungeonWidth - 7);
            params.leftMargin = divDungeonWidth * i;
            if (i < 11) horizontalPosition[i] = params.leftMargin;
            view.setLayoutParams(params);
            view.setBackgroundColor(Color.RED);
            dungeon.addView(view);
        }
        Log.i("dungeon size", dungeonWidth + "x" + dungeonHeight);
        int j = 0;
        for (int i = 11; i >= 0; i--) {
            View view = new View(activity);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dungeonHeight - 7, 2);
            params.topMargin = divDungeonHeight * i;
            view.setLayoutParams(params);
            view.setBackgroundColor(Color.BLACK);
            dungeon.addView(view);
            if (i < 11) {
                verticalPosition[j] = params.topMargin;
                j++;
            }
        }

        initValues();
    }

    void initValues(){
        //먹잇감, 몬스터들 사이즈 및 위치 초기화
        preyMoveCnt = predMoveCnt = 0;
        predMoveCntTv.setText(String.valueOf(predMoveCnt));
        preyMoveCntTv.setText(String.valueOf(preyMoveCnt));
        preg.setImageResource(R.drawable.preg);
        width = horizontalPosition[0] + horizontalPosition[1];
        height = verticalPosition[0] - verticalPosition[1];
        FrameLayout.LayoutParams pregParams = new FrameLayout.LayoutParams(preg.getLayoutParams());
        pregParams.width = width;
        pregParams.height = height;
        pregX = 5;
        pregY = 5;
        pregParams.leftMargin = horizontalPosition[pregX];
        pregParams.topMargin = verticalPosition[pregY];
        preg.setLayoutParams(pregParams);
        FrameLayout.LayoutParams predator1Params = new FrameLayout.LayoutParams(predator[0].getLayoutParams());
        predator1Params.width = width;
        predator1Params.height = height;
        predX[0] = 0;
        predY[0] = 10;
        predator1Params.leftMargin = horizontalPosition[predX[0]];
        predator1Params.topMargin = verticalPosition[predY[0]];
        predator[0].setLayoutParams(predator1Params);
        FrameLayout.LayoutParams predator2Params = new FrameLayout.LayoutParams(predator[0].getLayoutParams());
        predator2Params.width = width;
        predator2Params.height = height;
        predX[1] = 10;
        predY[1] = 10;
        predator2Params.leftMargin = horizontalPosition[predX[1]];
        predator2Params.topMargin = verticalPosition[predY[1]];
        predator[1].setLayoutParams(predator2Params);
        FrameLayout.LayoutParams predator3Params = new FrameLayout.LayoutParams(predator[0].getLayoutParams());
        predator3Params.width = width;
        predator3Params.height = height;
        predX[2] = 0;
        predY[2] = 0;
        predator3Params.leftMargin = horizontalPosition[predX[2]];
        predator3Params.topMargin = verticalPosition[predY[2]];
        predator[2].setLayoutParams(predator3Params);
        FrameLayout.LayoutParams predator4Params = new FrameLayout.LayoutParams(predator[0].getLayoutParams());
        predator4Params.width = width;
        predator4Params.height = height;
        predX[3] = 10;
        predY[3] = 0;
        predator4Params.leftMargin = horizontalPosition[predX[3]];
        predator4Params.topMargin = verticalPosition[predY[3]];
        predator[3].setLayoutParams(predator4Params);


        the_shortest_index=new int[4];
        second_short_index=new int[4];
        third_short_index=new int[4];
        fourth_short_index=new int[4];

        Compare_right=new double[4];
        Compare_left=new double[4];
        Compare_up=new double[4];
        Compare_down=new double[4];
        Compare_total=new double[4];

        first_condition=false;
        second_condition=false;
        third_condition=false;
        fourth_condition=false;
        condition_iscomplete=(first_condition && second_condition);
        three_condition_iscomplete=(first_condition && second_condition&&third_condition);
        four_condition_iscomplete=(first_condition && second_condition&&third_condition&&fourth_condition);
    }
}

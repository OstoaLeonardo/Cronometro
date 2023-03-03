package com.example.cronometro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tvTime, tvFlags;
    private Button btnPlay, btnReplay, btnFlag;
    private ScrollView svFlags;
    private boolean isPaused = false, isStart = false;
    private long startTime = 0L;
    private int counter = 1;
    private Handler timerHandler = new Handler();
    private StringBuilder flagsText = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int currentTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(currentTheme == Configuration.UI_MODE_NIGHT_YES) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.md_theme_dark_background));
        } else {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.md_theme_light_background));
        }

        tvTime = (TextView) findViewById(R.id.tvTime);
        tvFlags = (TextView) findViewById(R.id.tvFlags);
        svFlags = (ScrollView) findViewById(R.id.svFlags);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnReplay = (Button) findViewById(R.id.btnReplay);
        btnFlag = (Button) findViewById(R.id.btnFlag);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStart == false) {
                    startTime = System.currentTimeMillis();
                    timerHandler.post(timerRunnable);
                    isStart = true;
                    btnPlay.setText("Pausar");
                    btnReplay.setVisibility(View.VISIBLE);
                    btnFlag.setVisibility(View.VISIBLE);

                    Animation slideLeftAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_left);
                    Animation slideRightAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_right);

                    btnReplay.startAnimation(slideLeftAnimation);
                    btnFlag.startAnimation(slideRightAnimation);

                } else {
                    if(isPaused == false) {
                        timerHandler.removeCallbacks(timerRunnable);
                        btnPlay.setText("Reanudar");
                        isPaused = true;
                    } else {
                        startTime = System.currentTimeMillis() - (tvTime.getText().toString().equalsIgnoreCase("0:00") ? 0 : getTimeInMillis(tvTime.getText().toString()));
                        timerHandler.post(timerRunnable);
                        btnPlay.setText("Pausar");
                        isPaused = false;
                    }
                }
            }
        });

        btnReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerHandler.removeCallbacks(timerRunnable);
                tvTime.setText("0:00:00");
                btnPlay.setText("Iniciar");
                btnReplay.setVisibility(View.GONE);
                btnFlag.setVisibility(View.GONE);
                isStart = false;

                flagsText.setLength(0);
                counter = 1;
                tvFlags.setText("");
            }
        });

        btnFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStart) {
                    svFlags.setVisibility(View.VISIBLE);
                    flagsText.append("#" + counter + "\t\t\t" + tvTime.getText() + "\n");
                    counter++;
                    tvFlags.setText(flagsText.toString());
                }
            }
        });
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int milliseconds = (int) (millis % 1000) / 10;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            tvTime.setText(String.format("%d:%02d:%02d", minutes, seconds, milliseconds));

            timerHandler.postDelayed(this, 50);
        }
    };

    private long getTimeInMillis(String time) {
        String[] parts = time.split(":");
        long minutes = Long.parseLong(parts[0]);
        long seconds = Long.parseLong(parts[1]);
        return (minutes * 60 + seconds) * 1000;
    }
}
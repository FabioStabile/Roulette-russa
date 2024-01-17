package com.example.rouletterussa;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ImageView gun, player1, player2, bulletLeft, bulletRight;
    private TextView tvPlayer1, tvPlayer2, tvWinner;
    // Count is used to count the number of bullets already shotted
    private int count = 1, pointsPlayer1 = 0, pointsPlayer2 = 0;
    // Used to see which player is dead
    private boolean side = false;
    private Button shootBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Change the color of the bar to black
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        shootBtn = findViewById(R.id.button);

        startGame();
    }

    // Function used to start the game
    private void startGame() {
        // Initializing the variables
        gun = findViewById(R.id.Gun);
        player1 = findViewById(R.id.Player1);
        player2 = findViewById(R.id.Player2);
        bulletLeft = findViewById(R.id.BulletLeft);
        bulletLeft.setVisibility(View.INVISIBLE);
        bulletRight = findViewById(R.id.BulletRight);
        bulletRight.setVisibility(View.INVISIBLE);
        tvPlayer1 = findViewById(R.id.pointsPlayer1);
        tvPlayer2 = findViewById(R.id.pointsPlayer2);
        tvWinner = findViewById(R.id.winner);
        tvWinner.setVisibility(View.INVISIBLE);
        // Set the next of the button, i've putted it because i need to change it when we start another game/round
        shootBtn.setText("Shoot");
        shootBtn.setEnabled(false);
        shootBtn.setTextColor(getResources().getColor(R.color.black));

        int isBullet = new Random().nextInt(6) + 1;
        MediaPlayer reload = MediaPlayer.create(this, R.raw.revolverreload);
        reload.setVolume(2.0f, 2.0f);
        reload.start();

        reload.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                shootBtn.setEnabled(true);
                shootBtn.setTextColor(getResources().getColor(R.color.white));
            }
        });

        shootBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable the bullet while it's shooting or misfires
                shootBtn.setEnabled(false);
                shootBtn.setTextColor(getResources().getColor(R.color.black));

                // See if the bullet shot will misfire or will kill a player
                if(count == isBullet){
                    MediaPlayer shot = MediaPlayer.create(MainActivity.this, R.raw.shot);
                    shot.setVolume(2.0f, 2.0f);
                    shot.start();


                    // Player 1 is dead
                    if (!side) {
                        bulletLeft.setVisibility(View.VISIBLE);

                        float startBias = 0.399f;
                        float endBias = 0.0f;
                        ValueAnimator animator = ValueAnimator.ofFloat(startBias, endBias);
                        animator.setDuration(100);

                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                // set the params of the bullet
                                float animatedValue = (float) animation.getAnimatedValue();
                                // Take the params of the bullet to modify it for the animation
                                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bulletLeft.getLayoutParams();
                                // Edit the current bias of the bullet in the current progress of the animation
                                params.horizontalBias = animatedValue;
                                // Take the params of the bullet to modify it for the animation
                                bulletLeft.setLayoutParams(params);
                            }
                        }); 

                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                bulletLeft.setVisibility(View.INVISIBLE);

                                MediaPlayer deathScream = MediaPlayer.create(MainActivity.this, R.raw.deathscream);
                                deathScream.setVolume(2.0f, 2.0f);
                                deathScream.start();

                                // Change the image of player 1
                                player1.setImageResource(R.drawable.dead);
                            }
                        });

                        animator.start();

                        pointsPlayer2++;
                        tvPlayer2.setText("" + pointsPlayer2);

                        shootBtn.setEnabled(true);
                        shootBtn.setTextColor(getResources().getColor(R.color.white));

                        if(pointsPlayer2 == 3){
                            tvWinner.setText("The winner of the game is Player 2");
                            tvWinner.setVisibility(View.VISIBLE);

                            shootBtn.setText("Another game");
                            shootBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    restartGame();
                                }
                            });
                        }else {
                            tvWinner.setText("The winner of this round is Player 2");
                            tvWinner.setVisibility(View.VISIBLE);

                            shootBtn.setText("Play Again");
                            shootBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    nextRound();
                                }
                            });
                        }
                    // Player 2 is dead
                    } else if (side) {
                        bulletRight.setVisibility(View.VISIBLE);

                        // Variables used in the animation of the bullet the starting and arriving position
                        float startBias = 0.603f;
                        float endBias = 1.0f;
                        ValueAnimator animator = ValueAnimator.ofFloat(startBias, endBias);
                        animator.setDuration(100);

                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                // Used to see the progress of the animation
                                float animatedValue = (float) animation.getAnimatedValue();
                                // Take the params of the bullet to modify it for the animation
                                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bulletRight.getLayoutParams();
                                // Edit the current bias of the bullet in the current progress of the animation
                                params.horizontalBias = animatedValue;
                                // set the params of the bullet
                                bulletRight.setLayoutParams(params);
                            }
                        });

                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                bulletRight.setVisibility(View.INVISIBLE);

                                MediaPlayer deathScream = MediaPlayer.create(MainActivity.this, R.raw.deathscream);
                                deathScream.setVolume(2.0f, 2.0f);
                                deathScream.start();

                                // Change the image of player 2
                                player2.setImageResource(R.drawable.dead);
                            }
                        });

                        animator.start();

                        pointsPlayer1++;
                        tvPlayer1.setText("" + pointsPlayer1);

                        shootBtn.setEnabled(true);
                        shootBtn.setTextColor(getResources().getColor(R.color.white));

                        if(pointsPlayer1 == 3){
                            tvWinner.setText("The winner of the game is Player 1");
                            tvWinner.setVisibility(View.VISIBLE);

                            shootBtn.setText("Another game");
                            shootBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    restartGame();
                                }
                            });
                        }else {
                            tvWinner.setText("The winner of this round is Player 1");
                            tvWinner.setVisibility(View.VISIBLE);

                            shootBtn.setText("Play Again");
                            shootBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    nextRound();
                                }
                            });
                        }
                    }
                // If the current shot is not the one with the bullet in, it will misfire
                } else {
                    MediaPlayer misfire = MediaPlayer.create(MainActivity.this, R.raw.misfire);
                    misfire.setVolume(2.0f, 2.0f);
                    misfire.start();

                    misfire.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            shootBtn.setEnabled(true);
                            shootBtn.setTextColor(getResources().getColor(R.color.white));
                        }
                    });

                    count++;

                    // If the gun was pointed to player 1 it will now point to player 2
                    if (!side) {
                        gun.setImageResource(R.drawable.revolverright);
                        side = true;

                        // If the gun was pointed to player 2 it will now point to player 1
                    } else if (side) {
                        gun.setImageResource(R.drawable.revolverleft);
                        side = false;
                    }
                }
            }
        });
    }

    // Function used to start another round
    public void nextRound(){
        // Setting the various variables to start another round
        player1.setImageResource(R.drawable.stickman);
        player2.setImageResource(R.drawable.stickman);
        gun.setImageResource(R.drawable.revolverleft);
        count = 1;
        side = false;

        startGame();
    }

    // Function used to start another game
    public void restartGame(){
        // Setting the variables to start another game
        pointsPlayer1 = 0;
        pointsPlayer2 = 0;

        tvPlayer1.setText("0");
        tvPlayer2.setText("0");

        nextRound();
    }
}
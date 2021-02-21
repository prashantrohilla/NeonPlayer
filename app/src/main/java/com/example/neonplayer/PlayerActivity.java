package com.example.neonplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import static com.example.neonplayer.AlbumDetailsAdapter.albumFiles;
import static com.example.neonplayer.MainActivity.musicFiles;
import static com.example.neonplayer.MainActivity.repeatBoolean;
import static com.example.neonplayer.MainActivity.shuffleBoolean;
import static com.example.neonplayer.MusicAdapter.mFiles;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView song_name, artist_name, duration_played, duration_total,settings;
    ImageView cover_art, nextBtn, prevBtn, shuffleBtn, repeatBtn,mic;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    int position=-1;
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler=new Handler();
    private Thread playThread, prevThread, nextThread;
    static ArrayList<MusicFiles> listSongs=new ArrayList<>();
    private SpeechRecognizer speechRecognizer;////////////
    private Intent speechRecognizerIntent;////////////
    private String keeper="";//////////
    Boolean mode=true;
    CountDownTimer ctimer;
    boolean blue=true;
    boolean aqua=false;
    boolean yellow=false;
    boolean red=false;
    boolean pink=false;
    boolean orange=false;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);

        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(PlayerActivity.this);//////////
        speechRecognizerIntent=new Intent((RecognizerIntent.ACTION_RECOGNIZE_SPEECH));////////////
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);/////////
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());/////////

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {/////////

            }

            @Override
            public void onBeginningOfSpeech() {/////////

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {/////////

            }/////////

            @Override
            public void onResults(Bundle bundle)///////////////////////////
            {
                ArrayList<String>matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(matchesFound!=null)
                {
                    keeper=matchesFound.get(0);
                    if(keeper.equals("pause"))
                    {
                        playPauseBtnClicked();
                        Toast.makeText(PlayerActivity.this,"Song Paused.",Toast.LENGTH_LONG).show();
                    }

                    else if(keeper.equals("play"))
                    {
                        playPauseBtnClicked();
                        Toast.makeText(PlayerActivity.this,"Song Playing. ",Toast.LENGTH_LONG).show();
                    }

                    else if(keeper.equals("next"))
                    {
                        nextBtnClicked();
                        Toast.makeText(PlayerActivity.this,"Playing Next Song. ",Toast.LENGTH_LONG).show();
                    }

                    else if(keeper.equals("back"))
                    {
                        prevBtnClicked();
                        Toast.makeText(PlayerActivity.this,"Previous Song Playing. ",Toast.LENGTH_LONG).show();
                    }

                    else if(keeper.equals("repeat on"))
                    {
                        repeatBoolean=true;
                        Toast.makeText(PlayerActivity.this, "Repeat on", Toast.LENGTH_SHORT).show();
                        repeatBtn.setImageResource(R.drawable.repeaton);
                    }

                    else if(keeper.equals("repeat off"))
                    {
                        if(repeatBoolean) {
                            repeatBoolean = false;
                            Toast.makeText(PlayerActivity.this, "Repeat off", Toast.LENGTH_SHORT).show();
                            colorchanger();
                        }
                    }

                    else if(keeper.equals("shuffle off"))
                    {
                        if(shuffleBoolean)
                        {
                            shuffleBoolean=false;
                            Toast.makeText(PlayerActivity.this, "Shuffle off", Toast.LENGTH_SHORT).show();
                            colorchanger();
                        }
                    }

                    else if(keeper.equals("shuffle on"))
                    {
                        shuffleBoolean=true;
                        Toast.makeText(PlayerActivity.this, "Shuffle on", Toast.LENGTH_SHORT).show();
                        shuffleBtn.setImageResource(R.drawable.shuffleon);
                    }

                    else if(keeper.equals("red"))
                    {
                        red=true; blue=false; aqua=false; yellow=false; pink=false; orange=false;
                        colorchanger();
                    }

                    else if(keeper.equals("yellow"))
                    {
                        red=false; blue=false; aqua=false;  yellow=true; pink=false; orange=false;
                        colorchanger();
                    }

                    else if(keeper.equals("blue"))
                    {
                        red=false; blue=true; aqua=false; yellow=false; pink=false; orange=false;
                        colorchanger();
                    }

                    else if(keeper.equals("lime"))
                    {
                        red=false; blue=false; aqua=true; yellow=false; pink=false; orange=false;
                        colorchanger();
                    }

                    else if(keeper.equals("pink"))
                    {
                        red=false; blue=false; aqua=false; yellow=false; pink=true; orange=false;
                        colorchanger();
                    }

                    else if(keeper.equals("orange"))
                    {
                        red=false; blue=false; aqua=false; yellow=false; pink=false; orange=true;
                        colorchanger();
                    }

                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });////////////////////////

        mic.setOnClickListener(new View.OnClickListener() {///////
            @Override
            public void onClick(View v)
            {
                if(mode)
                {
                    mode=false;
                    keeper="";
                    Toast.makeText(PlayerActivity.this, "Say Something. ", Toast.LENGTH_SHORT).show();

                    mic.setImageResource(R.drawable.micw);
                    speechRecognizer.startListening(speechRecognizerIntent);
                    starttimer((long) 36003);
                }
                else
                {
                    mode=true;
                    colorchanger();
                    Toast.makeText(PlayerActivity.this, "Mic Off. ", Toast.LENGTH_SHORT).show();
                    starttimer((long) 0);

                }



            }
        });///////////




        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser)
                {
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null)
                {
                    int mCurrentPosition= mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });

        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleBoolean)
                {
                    shuffleBoolean=false;
                    Toast.makeText(PlayerActivity.this, "Shuffle Off", Toast.LENGTH_SHORT).show();
                    colorchanger();
                }
                else
                {
                    shuffleBoolean=true;
                    shuffleBtn.setImageResource(R.drawable.shuffleon);
                    Toast.makeText(PlayerActivity.this, "Shuffle On", Toast.LENGTH_SHORT).show();
                }
            }
        });

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatBoolean)
                {
                    repeatBoolean=false;
                    colorchanger();
                    Toast.makeText(PlayerActivity.this, "Repeat Off", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    repeatBoolean=true;
                    repeatBtn.setImageResource(R.drawable.repeaton);
                    Toast.makeText(PlayerActivity.this, "Repeat On", Toast.LENGTH_SHORT).show();
                    colorchanger();
                }
            }
        });

    }

    @Override
    protected void onResume() {

        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();

        super.onResume();
    }

    private void nextThreadBtn() {
        nextThread=new Thread(){
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    public void starttimer(Long milli)
    {

        ctimer=new CountDownTimer(milli*1000,1000) {
            @Override
            public void onTick(long l) {
                int d=1000;
                long value=l/d;
                l=value;
                 if(mode==true)
                 {
                    l=0;
                    d=0;
                    speechRecognizer.stopListening();
                 }
               else if(l%3==0 && !mode)
                {
                    speechRecognizer.stopListening();
                    colorchanger();
                    speechRecognizer.startListening(speechRecognizerIntent);
                }

            }

            @Override
            public void onFinish() {

            }
        } ;
        ctimer.start();
    }



    private void nextBtnClicked() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position=((position+1)%listSongs.size());
            }
            else if(repeatBoolean && !shuffleBoolean)
            {
                position=((position)%listSongs.size());
            }
            // else position will be position..
            uri= Uri.parse(listSongs.get(position).getPath());
            mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition= mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.pause);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position=((position+1)%listSongs.size());
            }
            else if(repeatBoolean && !shuffleBoolean)
            {
                position=((position)%listSongs.size());
            }
            uri= Uri.parse(listSongs.get(position).getPath());
            mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition= mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.play);
        }
    }

    private int getRandom(int i) {
        Random random=new Random();
        return random.nextInt(i+1);

    }

    private void prevThreadBtn() {
        prevThread=new Thread(){
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevBtnClicked() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position= ((position-1)<0? (listSongs.size()-1): (position-1));
            }
            else if(repeatBoolean && !shuffleBoolean)
            {
                position= ((position-1)<0? (listSongs.size()-1): (position-1));
            }

            uri= Uri.parse(listSongs.get(position).getPath());
            mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition= mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.pause);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position= ((position-1)<0? (listSongs.size()-1): (position-1));
            }
            else if(repeatBoolean && !shuffleBoolean)
            {
                position= ((position-1)<0? (listSongs.size()-1): (position-1));
            }
            uri= Uri.parse(listSongs.get(position).getPath());
            mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition= mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.play);
        }
    }

    private void playThreadBtn() {
        playThread=new Thread(){
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void playPauseBtnClicked() {
        if(mediaPlayer.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition= mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else
        {
            playPauseBtn.setImageResource(R.drawable.pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition= mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    private String formattedTime(int mCurrentPosition) {
        String totalout= "";
        String totalNew= "";
        String seconds =String.valueOf(mCurrentPosition%60);
        String minutes =String.valueOf(mCurrentPosition/60);
        totalout= minutes+":"+seconds;
        totalNew= minutes+":"+"0"+seconds;
        if(seconds.length()==1)
        {
            return totalNew;
        }
        else
        {
            return totalout;
        }
    }

    private void getIntentMethod()   // playing songs
    {
        position=getIntent().getIntExtra("position", -1);
        String sender=getIntent().getStringExtra("sender");
        if(sender!=null && sender.equals("albumDetails"))
        {
            listSongs=albumFiles;
        }
        else {
            listSongs = mFiles;
        }
        if(listSongs!= null)
        {
            playPauseBtn.setImageResource(R.drawable.pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration()/1000);   // song duration
        metaData(uri);
    }

    private void initViews()
    {
        song_name= findViewById(R.id.song_name);
        artist_name= findViewById(R.id.song_artist);
        duration_played= findViewById(R.id.durationPlayed);
        duration_total= findViewById(R.id.durationTotal);
        cover_art= findViewById(R.id.cover_art);
        nextBtn= findViewById(R.id.id_next);
        prevBtn= findViewById(R.id.id_prev);
        shuffleBtn= findViewById(R.id.id_shuffle);
        repeatBtn= findViewById(R.id.id_repeat);
        playPauseBtn= findViewById(R.id.play_pause);
        seekBar= findViewById(R.id.seekBar);
        mic= findViewById(R.id.mic);//
        settings= findViewById(R.id.settings);
    }

    private void metaData(Uri uri)
    {
        MediaMetadataRetriever retriever= new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal= Integer.parseInt(listSongs.get(position).getDuration())/1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[]art = retriever.getEmbeddedPicture();

        if(art!=null)
        {
            bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAnimation(this,cover_art,bitmap);
        }
        else
        {
            if(blue)
            {
                Glide.with(this).load(R.drawable.music_note).into(cover_art);

            }
            if(red)
            {
                Glide.with(this).load(R.drawable.rmusic_note).into(cover_art);
            }
            if(yellow)
            {
                Glide.with(this).load(R.drawable.ymusic_note).into(cover_art);
            }
            if(aqua)
            {
                Glide.with(this).load(R.drawable.lmusic_note).into(cover_art);
            }
            if(pink)
            {
                Glide.with(this).load(R.drawable.pmusic_note).into(cover_art);
            }
            if(orange)
            {
                Glide.with(this).load(R.drawable.omusic_note).into(cover_art);
            }
        }

    }

    public void ImageAnimation(final Context context, final ImageView imageView, final Bitmap bitmap)
    {
        Animation animOut= AnimationUtils.loadAnimation(context,android.R.anim.fade_out);
        final Animation animIn= AnimationUtils.loadAnimation(context,android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if(mediaPlayer!=null)
        {
            mediaPlayer= MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    @SuppressLint("Range")
    public void colorchanger()
    {
        if(blue)
        {
            song_name.setTextColor(getResources().getColor(R.color.sblue));
            artist_name.setTextColor(getResources().getColor(R.color.sblue));
            duration_played.setTextColor(getResources().getColor(R.color.sblue));
            duration_total.setTextColor(getResources().getColor(R.color.sblue));
            settings.setTextColor(getResources().getColor(R.color.sblue));
            nextBtn.setImageResource(R.drawable.next3);
            prevBtn.setImageResource(R.drawable.back2);
            if(!mode)
            {
                mic.setImageResource(R.drawable.micw);
            }
            else if(mode)
            {
                mic.setImageResource(R.drawable.bmicw);
            }


            playPauseBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.sblue));
            if(!repeatBoolean)
            {
                repeatBtn.setImageResource(R.drawable.repeatoff);
            }
            if(!shuffleBoolean)
            {
                shuffleBtn.setImageResource(R.drawable.shuffleoff);
            }
            if(repeatBoolean)
            {
                repeatBtn.setImageResource(R.drawable.repeaton);
            }
            if(shuffleBoolean)
            {
                shuffleBtn.setImageResource(R.drawable.shuffleon);
            }
            seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.sblue), PorterDuff.Mode.SRC_ATOP);
            metaData(uri);
        }


        else if(red)
        {
            song_name.setTextColor(getResources().getColor(R.color.red));
            artist_name.setTextColor(getResources().getColor(R.color.red));
            duration_played.setTextColor(getResources().getColor(R.color.red));
            duration_total.setTextColor(getResources().getColor(R.color.red));
            settings.setTextColor(getResources().getColor(R.color.red));
            nextBtn.setImageResource(R.drawable.rnext3);
            prevBtn.setImageResource(R.drawable.rback2);

            if(mode)
            {
                mic.setImageResource(R.drawable.rmicw);
            }
            else if(!mode)
            {
                mic.setImageResource(R.drawable.micw);
            }

            playPauseBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
            seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
            if(!repeatBoolean)
            {
                repeatBtn.setImageResource(R.drawable.rrepeatoff);
            }
            if(!shuffleBoolean)
            {
                shuffleBtn.setImageResource(R.drawable.rshuffle);
            }
            if(repeatBoolean)
            {
                repeatBtn.setImageResource(R.drawable.repeaton);
            }
            if(shuffleBoolean)
            {
                shuffleBtn.setImageResource(R.drawable.shuffleon);
            }
            metaData(uri);
        }


        else if(aqua)
        {
            song_name.setTextColor(getResources().getColor(R.color.aqua));
            artist_name.setTextColor(getResources().getColor(R.color.aqua));
            duration_played.setTextColor(getResources().getColor(R.color.aqua));
            duration_total.setTextColor(getResources().getColor(R.color.aqua));
            settings.setTextColor(getResources().getColor(R.color.aqua));
            nextBtn.setImageResource(R.drawable.lnext3);
            prevBtn.setImageResource(R.drawable.lback2);
            if(mode)
            {
                mic.setImageResource(R.drawable.lmicw);
            }
            else if(!mode)
            {
                mic.setImageResource(R.drawable.micw);
            }
            playPauseBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.aqua));
            if(!repeatBoolean)
            {
                repeatBtn.setImageResource(R.drawable.lrepeatoff);
            }
            if(!shuffleBoolean)
            {
                shuffleBtn.setImageResource(R.drawable.lshuffle);
            }
            if(repeatBoolean)
            {
                repeatBtn.setImageResource(R.drawable.repeaton);
            }
            if(shuffleBoolean)
            {
                shuffleBtn.setImageResource(R.drawable.shuffleon);
            }
            seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.aqua), PorterDuff.Mode.SRC_ATOP);
            metaData(uri);
        }


        else if(orange)
        {
            song_name.setTextColor(getResources().getColor(R.color.orange));
            artist_name.setTextColor(getResources().getColor(R.color.orange));
            duration_played.setTextColor(getResources().getColor(R.color.orange));
            duration_total.setTextColor(getResources().getColor(R.color.orange));
            settings.setTextColor(getResources().getColor(R.color.orange));
            nextBtn.setImageResource(R.drawable.onext3);
            prevBtn.setImageResource(R.drawable.oback2);
            if(mode)
            {
                mic.setImageResource(R.drawable.omicw);
            }
            else if(!mode)
            {
                mic.setImageResource(R.drawable.micw);
            }
            playPauseBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.orange));
            if(!repeatBoolean)
            {
                repeatBtn.setImageResource(R.drawable.orepeatoff);
            }
            if(!shuffleBoolean)
            {
                shuffleBtn.setImageResource(R.drawable.oshuffle);
            }
            if(repeatBoolean)
            {
                repeatBtn.setImageResource(R.drawable.repeaton);
            }
            if(shuffleBoolean)
            {
                shuffleBtn.setImageResource(R.drawable.shuffleon);
            }
            seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
            metaData(uri);
        }


        else if(pink)
        {
            song_name.setTextColor(getResources().getColor(R.color.pink));
            artist_name.setTextColor(getResources().getColor(R.color.pink));
            duration_played.setTextColor(getResources().getColor(R.color.pink));
            duration_total.setTextColor(getResources().getColor(R.color.pink));
            settings.setTextColor(getResources().getColor(R.color.pink));
            nextBtn.setImageResource(R.drawable.pnext3);
            prevBtn.setImageResource(R.drawable.pback2);
            if(mode)
            {
                mic.setImageResource(R.drawable.pmicw);
            }
            else if(!mode)
            {
                mic.setImageResource(R.drawable.micw);
            }
            playPauseBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.pink));
            if(!repeatBoolean)
            {
                repeatBtn.setImageResource(R.drawable.prepeatoff);
            }
            if(!shuffleBoolean)
            {
                shuffleBtn.setImageResource(R.drawable.pshuffle);
            }
            if(repeatBoolean)
            {
                repeatBtn.setImageResource(R.drawable.repeaton);
            }
            if(shuffleBoolean)
            {
                shuffleBtn.setImageResource(R.drawable.shuffleon);
            }
            seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.pink), PorterDuff.Mode.SRC_ATOP);
            metaData(uri);
        }


        else if(yellow)
        {

            song_name.setTextColor(getResources().getColor(R.color.yellow));
            artist_name.setTextColor(getResources().getColor(R.color.yellow));
            duration_played.setTextColor(getResources().getColor(R.color.yellow));
            duration_total.setTextColor(getResources().getColor(R.color.yellow));
            settings.setTextColor(getResources().getColor(R.color.yellow));
            nextBtn.setImageResource(R.drawable.ynext3);
            prevBtn.setImageResource(R.drawable.yback2);
            if(mode)
            {
                mic.setImageResource(R.drawable.ymicw);
            }
            else if(!mode)
            {
                mic.setImageResource(R.drawable.micw);
            }
            playPauseBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.yellow));
            if(!repeatBoolean)
            {
                repeatBtn.setImageResource(R.drawable.yrepeatoff);
            }
            if(!shuffleBoolean)
            {
                shuffleBtn.setImageResource(R.drawable.yshuffle);
            }
            if(repeatBoolean)
            {
                repeatBtn.setImageResource(R.drawable.repeaton);
            }
            if(shuffleBoolean)
            {
                shuffleBtn.setImageResource(R.drawable.shuffleon);
            }
            seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
            metaData(uri);
        }

    }


}




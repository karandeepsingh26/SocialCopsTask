package karan.socialcopstask;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.rtp.RtpStream;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.InCallService;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.webkit.URLUtil;
import android.widget.MediaController;
import android.widget.VideoView;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    final int REQUEST_CODE=123;
    VideoView videoView;
    SurfaceView surfaceView;
    CommonBytes commonBytes=new CommonBytes();
    MediaPlayer mediaPlayer;
    SurfaceHolder surfaceHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer=new MediaPlayer();
        videoView= (VideoView) findViewById(R.id.videoView);
        surfaceView= (SurfaceView) findViewById(R.id.sufaceView);
        surfaceHolder=surfaceView.getHolder();
        surfaceHolder.addCallback(this);



        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            requestPermission();
        }
        else
        {
            new DownloadThread("https://socialcops.com/video/main.mp4",commonBytes).start();
            Timer timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {


                    try {
                        HashMap<String,String> header=new HashMap<String, String>();
                        mediaPlayer.setDataSource(MainActivity.this,Uri.parse("http://localhost:8090"));
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            },1000);

        }



    }
    void requestPermission()
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            new DownloadThread(" https://socialcops.com/video/main.mp4",commonBytes).start();
            new PlayThread(commonBytes).start();
            videoView.setVideoURI(Uri.parse("localhost:8080"));
            videoView.start();



        }
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        mediaPlayer.setDisplay(holder);



    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

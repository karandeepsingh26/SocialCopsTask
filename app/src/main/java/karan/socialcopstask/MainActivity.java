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
import android.widget.VideoView;

import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    final int REQUEST_CODE=123;
    VideoView videoView;
    SurfaceView surfaceView;
    CommonBytes commonBytes=new CommonBytes();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView= (VideoView) findViewById(R.id.videoView);
        surfaceView= (SurfaceView) findViewById(R.id.sufaceView);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            requestPermission();
        }
        else
        {
            new DownloadThread(" https://socialcops.com/video/main.mp4",commonBytes).start();
            new PlayThread(commonBytes).start();
            new DownloadThread(" https://socialcops.com/video/main.mp4",commonBytes).start();

            final MediaPlayer mediaPlayer=new MediaPlayer();
            try {

                mediaPlayer.setDataSource("localhost:8090/");

                SurfaceHolder surfaceHolder=surfaceView.getHolder();
                surfaceHolder.setFixedSize(176, 144);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//                mediaPlayer.setDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            videoView.setVideoURI(Uri.parse("rtsp://localhost:8090/"));
            Timer timer=new Timer();
            RtpStream stream;



            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mediaPlayer.start();

                    Log.d("VIDEO","STARTED");
                }
            },5000);



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
}

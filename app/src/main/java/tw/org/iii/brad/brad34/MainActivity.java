package tw.org.iii.brad.brad34;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;

public class MainActivity extends AppCompatActivity {
    private AudioManager audioManager;
    private SeekBar seekBar;
    private SoundPool soundPool;
    private int SOUND1, SOUND2;
    private File sdroot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private void init(){
        seekBar = findViewById(R.id.seekbar);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));

        //soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .build();
        SOUND1 = soundPool.load(this, R.raw.effect_005, 1);
        SOUND2 = soundPool.load(this, R.raw.effect_006, 1);

        sdroot = Environment.getExternalStorageDirectory();
    }

    public void test1(View view) {
        audioManager.playSoundEffect(
                AudioManager.FX_KEYPRESS_INVALID,1);
    }
    public void test2(View view) {
        audioManager.playSoundEffect(
                AudioManager.FX_KEYPRESS_STANDARD,1);
    }
    public void test3(View view) {
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM,
                AudioManager.ADJUST_RAISE, 0);
        seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
    }
    public void test4(View view) {
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM,
                AudioManager.ADJUST_LOWER, 0);
        seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
    }


    public void test5(View view) {
        soundPool.play(SOUND1,1f,1f, 1,0,1);
    }

    public void test6(View view) {
        soundPool.play(SOUND2,1f,1f, 1,0,1);
    }

    public void test7(View view) {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(sdroot, "brad2020041901.amr")));
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                Uri uri = data.getData();
                String file = getAudioPathFromUri(this, uri);
                Log.v("brad", file);
                try {
                    copyFile(file, new File(sdroot, "brad041904.amr"));
                }catch (Exception e){
                    Log.v("brad", e.toString());
                }

            }else if (resultCode == RESULT_CANCELED){
                Log.v("brad", "cancel");
            }
        }
    }

    public void copyFile(String fileName, File target) throws IOException {
        File source = new File(fileName);
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(source));
        byte[] buf = new byte[(int)source.length()];
        bin.read(buf);
        bin.close();

        BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(target));
        bout.write(buf);
        bout.flush();
        bout.close();

    }

    public static String getAudioPathFromUri(Context c, Uri uri){
        Cursor cursor =
                c.getContentResolver().query(uri,
                        null, null, null, null);
        cursor.moveToNext();
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        String ret = cursor.getString(index);
        cursor.close();
        return ret;
    }



}

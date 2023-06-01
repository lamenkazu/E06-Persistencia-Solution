package com.example.e06_persistencia;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final int PHOTO_CODE = 0;
    private static final int REQUEST_PERMISSION_CODE = 1;
    private final String SHARE_PREF_ID = "SharedPrefId";

    Button btnTake ;
    Button btnSave ;
    ImageView ivPhoto ;
    MaterialTextView lblAccess, lblTime;

    SimpleDateFormat sdf ;
    String actualDate ;

    private void initComponents(){
        btnTake = findViewById(R.id.btn_take);
        btnSave = findViewById(R.id.btn_save);
        ivPhoto = findViewById(R.id.img);
        lblAccess = findViewById(R.id.lbl_access);
        lblTime = findViewById(R.id.lbl_time);

        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));

        actualDate = sdf.format(new Date());

    }
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent data = result.getData();

                if(data != null){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ImageView ivPhoto = findViewById(R.id.img);
                    ivPhoto.setImageBitmap(photo);
                }

            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        saveAccessTime();

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                launcher.launch(photoIntent);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ivPhoto.getDrawable() != null){
                    BitmapDrawable bitmapDrawable = ((BitmapDrawable) ivPhoto.getDrawable());
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    String path = Environment.getExternalStorageDirectory().toString();
                    File file = new File(path, "image.jpg");

                    if(Environment.getExternalStorageState(file).equals(Environment.MEDIA_MOUNTED))
                            savePhoto(bitmap, file);
                    else showToast("Armazenamento Externo Indisponível");

                }else showToast("Não há foto para salvar");

            }
        });
    }

    private void savePhoto(Bitmap bitmap, File file){
        try {
            if (Environment.getExternalStorageState(file).equals(Environment.MEDIA_MOUNTED)) {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                showToast("Foto Salva");

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showToast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveAccessTime(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARE_PREF_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        String value = pref.getString("last_access", "");

        if(value != ""){
            lblAccess.setText("Ultimo acesso: ");
            lblTime.setText(value);
        }else{
            lblAccess.setText("Primeiro acesso: ");
        }
        editor.putString("last_access", actualDate);
        editor.commit();
    }
}
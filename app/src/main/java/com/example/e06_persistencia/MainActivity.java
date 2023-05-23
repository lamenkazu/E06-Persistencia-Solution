package com.example.e06_persistencia;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PHOTO_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTake = findViewById(R.id.btn_take);
        Button btnSave = findViewById(R.id.btn_save);
        ImageView ivPhoto = findViewById(R.id.img);


        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(photoIntent, PHOTO_CODE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ivPhoto.getDrawable() != null){





                    try{
                        String path = Environment.getExternalStorageDirectory().toString();
                        OutputStream fOut = null;
                        Integer counter = 0;
                        File file = new File(path, "image"+counter+".jpg");

                        BitmapDrawable bitmapDrawable = ((BitmapDrawable) ivPhoto.getDrawable());
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        byte[] imageInByte = stream.toByteArray();
//                        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);

                        stream.close();

                        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());



                        Toast.makeText(MainActivity.this, "Foto Salva", Toast.LENGTH_SHORT).show();


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Nao há foto", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PHOTO_CODE && resultCode == RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            ImageView ivPhoto = findViewById(R.id.img);
            ivPhoto.setImageBitmap(photo);

        }
    }
}
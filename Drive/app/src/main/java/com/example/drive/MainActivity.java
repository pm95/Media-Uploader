package com.example.drive;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity
{

    ImageView imageToUpload;
    VideoView videoToUpload;
    Button imageUploadButton, videoUploadButton;

    String path = null;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private static final String IPHOST = "10.0.2.2";
    private static final int PORT = 10000;
    public Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Image handling
        imageToUpload = (ImageView) findViewById(R.id.viewImage);
        imageUploadButton = (Button) findViewById(R.id.bUploadImage);

        // Video handling
        videoToUpload = (VideoView) findViewById(R.id.viewVideo);
        videoUploadButton = (Button) findViewById(R.id.bUploadVideo);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Image Handling
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() != null)
        {
            Uri selection = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selection);
                imageToUpload.setImageBitmap(bitmap);

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selection, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                path = cursor.getString(columnIndex);

                cursor.close();

                Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Video Handling
        else if(requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data.getData() != null)
        {
            Uri selection = data.getData();
            try {

                String[] filePathColumn = {MediaStore.Video.Media.DATA};
                Cursor cursor = getContentResolver().query(selection, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                path = cursor.getString(columnIndex);

                cursor.close();

                videoToUpload.setVideoPath(selection.toString());
                videoToUpload.isLayoutRequested();
                videoToUpload.start();

                Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void selectThisImage(View view)
    {
        Toast.makeText(getApplicationContext(), "Image Gallery opened", Toast.LENGTH_LONG).show();
        Intent intent  = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void uploadThisImage(View view)
    {
        UploadFile uploadMe = new UploadFile();
        uploadMe.execute();
    }

    public void selectThisVideo(View view)
    {
        Toast.makeText(getApplicationContext(), "Video Gallery opened", Toast.LENGTH_LONG).show();
        Intent intent  = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    public void uploadThisVideo(View view)
    {
        UploadFile uploadMe = new UploadFile();
        uploadMe.execute();
    }

    public class UploadFile extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            try
            {
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                OutputStream os = null;

                // CHANGES:
                InetAddress serverAddress = InetAddress.getByName(IPHOST);
                Socket sock = new Socket(serverAddress, PORT);

                System.out.println("Socket creation successful!");

                File myFile = new File (path); // file can be used with URI
                byte [] mybytearray  = new byte [(int)myFile.length()];
                fis = new FileInputStream(myFile);
                bis = new BufferedInputStream(fis);
                bis.read(mybytearray,0,mybytearray.length);
                os = sock.getOutputStream();
                System.out.println("Sending media");
                os.write(mybytearray,0,mybytearray.length);
                os.flush();
                System.out.println("Done!");

                bis.close();
                os.close();
                sock.close();

            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("!!! Connection refused !!!");
            }
            return null;
        }
    }
}

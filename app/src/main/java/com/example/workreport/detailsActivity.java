package com.example.workreport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Date;

public class detailsActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE =1000 ;


    SQLiteDatabase database;
    EditText nameE,addressE,phoneE;
    String name,address,phone;
    TextView dateAndTime;
    ImageView imageView;
    Button addImg;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        dateAndTime = (TextView) findViewById(R.id.date);
        imageView = (ImageView) findViewById(R.id.imageview);
        nameE=(EditText)findViewById(R.id.name);
        addressE=findViewById(R.id.address);
        phoneE=findViewById(R.id.phone);
        addImg=(Button)findViewById(R.id.addImg);


        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());

        // textView is the TextView view that should display it
        dateAndTime.setText(currentDateTimeString);

        try {

            database = this.openOrCreateDatabase("Report", MODE_PRIVATE, null);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error in creation", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.done) {
            // do something here
            try {

                name=nameE.getText().toString().trim();

                address=addressE.getText().toString().trim();
                phone=phoneE.getText().toString().trim();

                database.execSQL("CREATE TABLE IF NOT EXISTS report1 (name VARCHAR,address VARCHAR ,phone VARCHAR )");

               // database.execSQL("INSERT INTO report1 (name,address,phone) VALUES ('name' ,'india','1234')");
                Cursor c = database.rawQuery("SELECT * FROM report1", null);
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)){
                    database.execSQL("INSERT INTO report1 (name,address,phone) VALUES ('" + name + "' ,'" + address + "','" + phone + "')");
                int nameIndex = c.getColumnIndex("name");
                int addressIndex = c.getColumnIndex("address");
                int phoneIndex = c.getColumnIndex("phone");
                c.moveToFirst();

                while (c != null) {
                    Log.i("name", c.getString(nameIndex));
                    Log.i("address", c.getString(addressIndex));
                    Log.i("phone", c.getString(phoneIndex));
                    c.moveToNext();
                }
                    finish();
                }
                else {
                    Toast.makeText(this, "Required fields", Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e){
            e.printStackTrace();
            finish();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addImage(View view)
    {
       /* if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==PackageManager.PERMISSION_DENIED|| checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.CAMERA) ==PackageManager.PERMISSION_DENIED)
        {
            String[] permission={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission,PERMISSION_CODE);
        }*/
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
      else {
            getPhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }


    public void getPhoto() {


        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImg = data.getData();

        if (requestCode == 1 && data != null && resultCode == RESULT_OK) {

            try {
                Glide.with(getApplicationContext()).asBitmap().load(selectedImg).into(imageView);
               /* Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImg);
                imageView.setImageBitmap(bitmap);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
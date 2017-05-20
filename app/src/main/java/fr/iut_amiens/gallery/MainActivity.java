package fr.iut_amiens.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CAMERA = 1;

    private static final int PERMISSION_REQUEST = 2;

    private DatabaseOpenHelper databaseOpenHelper;

    private PictureAdapter pictureAdapter;

    private File lastPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseOpenHelper = new DatabaseOpenHelper(this);

        pictureAdapter = new PictureAdapter(this);

        findViewById(R.id.button).setOnClickListener(this);
        ((ListView) findViewById(R.id.listView)).setAdapter(pictureAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public void onClick(View v) {
        Log.d("BUTTON", "start camera");

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);

        } else {
            takePicture();
        }
    }

    private void takePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        lastPicture = new File(picturesDir(), System.currentTimeMillis() + ".jpg");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", lastPicture));
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    private File picturesDir() {
        File picturesDir = null;
        final File[] dirs = ContextCompat.getExternalFilesDirs(this, Environment.DIRECTORY_PICTURES);
        if (dirs != null) {
            picturesDir = dirs[0];
        }

        if (picturesDir != null && !picturesDir.exists()) {
            picturesDir.mkdirs();
        }

        return picturesDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CAMERA == requestCode && RESULT_OK == resultCode) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Add a note");
            final android.widget.EditText editText = new android.widget.EditText(this);
            editText.setSingleLine();
            builder.setView(editText);
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(android.R.string.ok, new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    createPicture(editText.getText().toString(), lastPicture);
                }
            });
            builder.create().show();
        }
    }

    private void createPicture(String title, File content) {
        Picture picture = new Picture(title, content);

        android.content.ContentValues values = new android.content.ContentValues();
        values.put("title", picture.getTitle());
        values.put("content", picture.getContent().getAbsolutePath());

        databaseOpenHelper.getWritableDatabase().insert("picture", null, values);

        refresh();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                }
            }
        }
    }

    private void refresh() {
        pictureAdapter.clear();
        Cursor pictures = databaseOpenHelper.getReadableDatabase().query("picture", null, null, null, null, null, null);
        while (pictures.moveToNext()) {
            String title = pictures.getString(pictures.getColumnIndex("title"));
            String content = pictures.getString(pictures.getColumnIndex("content"));
            Picture picture = new Picture(title, new File(content));
            pictureAdapter.add(picture);
        }
    }
}

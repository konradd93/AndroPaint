package com.konrad.andropaint;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;

public class AndroPaint extends AppCompatActivity {

    private MySurface mySurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_andro_paint);

        mySurface = (MySurface) findViewById(R.id.surface);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //clear button clicked
    public void clear(View view) {
        mySurface.clearCanvas();
    }

    /**
     *
     * @param view
     * Set color, when button clicked
     */
    public void setRed(View view) {
        mySurface.setPaintColor("Red");
    }

    public void setGreen(View view) {
        mySurface.setPaintColor("Green");
    }

    public void setBlue(View view) {
        mySurface.setPaintColor("Blue");
    }

    public void setBlack(View view) {
        mySurface.setPaintColor("Black");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_save_png:
                save();
                break;
            case R.id.action_quit:
                AndroPaint.this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

//create and show save dialog
    private void save() {
        final AlertDialog.Builder editalert = new AlertDialog.Builder(this);
        editalert.setTitle("Zapisz jako png");
        editalert.setMessage("Zapiszę na karcie pamięci w folderze andropaint.\nPodaj nazwę:");
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        input.setLayoutParams(lp);
        editalert.setView(input);
        editalert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String name = input.getText().toString();
                Bitmap bitmap = mySurface.getBitmap();

                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                // create a File object for the parent directory
                File wallpaperDirectory = new File(path+"/andropaint/");
                // have the object build the directory structure, if needed.
                wallpaperDirectory.mkdirs();
                if(name.isEmpty()) name="andropaint";
                File file = new File(wallpaperDirectory, name + ".png");
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                    ostream.close();
                    mySurface.invalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    mySurface.setDrawingCacheEnabled(false);
                }
            }
        });
        editalert.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        editalert.show();
    }
}



package com.feridgoranatanas.projectpinkifinki;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo = (ImageView)findViewById(R.id.imageLogo);
        logo.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.raw.gorun_logo_invis));

        Button allRuns = (Button)findViewById(R.id.buttonAllRuns);
        allRuns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent allRuns = new Intent(MainActivity.this, AllRunsActivity.class);
                startActivity(allRuns);
            }
        });

        Button runStart = (Button)findViewById(R.id.buttonStartRunning);
        runStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent runStart = new Intent(MainActivity.this, RunActivity.class);
                startActivity(runStart);
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

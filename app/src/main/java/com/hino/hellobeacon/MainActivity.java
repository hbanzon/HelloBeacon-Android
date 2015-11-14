package com.hino.hellobeacon;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private Region region;

    // TODO: replace "<major>:<minor>" strings to match your own beacons.
    private static final Map<String, String> BEACON_DATA;
    static {
        Map<String, String> beaconData = new HashMap<>();
        beaconData.put("31194:58554", "Kitchen");
        beaconData.put("63029:44225", "Living Room");
        beaconData.put("52066:51215", "Hino's Desk");
        BEACON_DATA = Collections.unmodifiableMap(beaconData);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconManager = new BeaconManager(this);
        region = new Region(
                "All Beacons", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null
        );

        // setup ranging listener
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    String beaconInfo = getBeaconInfo(nearestBeacon);
                    displayBeaconInfo(beaconInfo);
                    Log.d("HelloBeacon", "Nearest beacon: " + beaconInfo);
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    private String getBeaconInfo(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (BEACON_DATA.containsKey(beaconKey)) {
            return BEACON_DATA.get(beaconKey) + " - Measured Power: " + beacon.getMeasuredPower();
        }
        return "Unresolved Beacon Key [" + beaconKey + "]";
    }

    private void displayBeaconInfo(final String beaconInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView nearestLabelText = (TextView) findViewById(R.id.nearestBeaconText);
                nearestLabelText.setText(beaconInfo);
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

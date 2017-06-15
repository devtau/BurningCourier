package ru.burningcourier.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import ru.burningcourier.R;
import ru.burningcourier.utils.MapHelper;
import ru.burningcourier.utils.MapHelperImpl;
import ru.burningcourier.utils.PermissionHelper;
import ru.burningcourier.utils.PermissionHelperImpl;

public class MapsActivity extends FragmentActivity {

    public static final String DESTINATION_LAT = "DESTINATION_LAT";
    public static final String DESTINATION_LON = "DESTINATION_LON";
    public static final String DESTINATION_TITLE = "DESTINATION_TITLE";
    private MapHelper mapHelper;

    
    public static void startActivity(Context context, double lat, double lon, String destinationTitle) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra(DESTINATION_LAT, lat);
        intent.putExtra(DESTINATION_LON, lon);
        intent.putExtra(DESTINATION_TITLE, destinationTitle);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
    
        double destinationLat = getIntent().getDoubleExtra(DESTINATION_LAT, 0);
        double destinationLon = getIntent().getDoubleExtra(DESTINATION_LON, 0);
        String destinationTitle = getIntent().getStringExtra(DESTINATION_TITLE);
        LatLng destination = new LatLng(destinationLat, destinationLon);
        
        mapHelper = new MapHelperImpl(destination, destinationTitle, savedInstanceState != null, this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapHelper);
        initNavigationButtons();

        PermissionHelper permissionHelper = new PermissionHelperImpl();
        if (!permissionHelper.checkGPSPermission(this)) {
            permissionHelper.requestGPSPermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelperImpl.GPS_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapHelper.handleGeoData();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapHelper.subscribeToLocationChange();
    }

    @Override
    protected void onStop() {
        mapHelper.unsubscribeFromLocationChange();
        super.onStop();
    }

    private void initNavigationButtons() {
        View buttonMyLocation = findViewById(R.id.buttonMyLocation);
        buttonMyLocation.setOnClickListener(v -> mapHelper.goToMyLocation());
        buttonMyLocation.setOnLongClickListener(v -> {
            showHintAboveView(v);
            return false;
        });

        View buttonResetLocation = findViewById(R.id.buttonResetLocation);
        buttonResetLocation.setOnClickListener(v -> mapHelper.goToDestination());
        buttonResetLocation.setOnLongClickListener(v -> {
            showHintAboveView(v);
            return false;
        });
    }

    private void showHintAboveView(View view) {
        String contentDesc = view.getContentDescription().toString();
        if (!TextUtils.isEmpty(contentDesc)) {
            int[] pos = new int[2];
            view.getLocationInWindow(pos);

            Toast toast = Toast.makeText(this, contentDesc, Toast.LENGTH_SHORT);
            int horizontalOffset = (int) getResources().getDimension(R.dimen.horizontal_offset);
            int verticalOffset = (int) getResources().getDimension(R.dimen.vertical_offset);
            toast.setGravity(Gravity.TOP | Gravity.START, pos[0] - horizontalOffset, pos[1] - verticalOffset);
            toast.show();
        }
    }
}

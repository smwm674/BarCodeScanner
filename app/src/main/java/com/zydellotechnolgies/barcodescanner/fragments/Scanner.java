package com.zydellotechnolgies.barcodescanner.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.zxing.Result;
import com.zydellotechnolgies.barcodescanner.MainActivity;
import com.zydellotechnolgies.barcodescanner.R;
import com.zydellotechnolgies.barcodescanner.model.ScanItem;
import com.zydellotechnolgies.barcodescanner.utils.CustomViewFinderView;
import com.zydellotechnolgies.barcodescanner.utils.DatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Scanner#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Scanner extends Fragment implements ZXingScannerView.ResultHandler {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Scanner() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Scanner.
     */
    // TODO: Rename and change types and number of parameters
    public static Scanner newInstance(String param1, String param2) {
        Scanner fragment = new Scanner();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @BindView(R.id.Scanner_view)
    FrameLayout contentFrame;
    @BindView(R.id.flash)
    ImageButton flash;
    @BindView(R.id.switch_camera)
    ImageButton switch_camera;
    @BindView(R.id.gallery)
    ImageButton gallery;
    private ZXingScannerView mScannerView;
    private static boolean flash_state = false;
    private static int camera_state = 0;
    private boolean hasFlash;
    private boolean hasFrontCamera;
    private SharedPreferences sharedpreferences;
    private String MyPREFERENCES = "Settings", SoundPrefrences = "Sound", VibratePrefences = "Vibrate";
    private DatabaseHelper mDatabaseHelper = null;
    private InterstitialAd mInterstitialAd;
    private static final int CAMERA_PERMISSION = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_scanner, container, false);
        ButterKnife.bind(this, root);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.barcode);
        mDatabaseHelper = new DatabaseHelper(getActivity());
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mScannerView = new ZXingScannerView(getContext()) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);

        hasFlash = getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash)
            flash.setVisibility(View.GONE);

        hasFrontCamera = getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        if (!hasFrontCamera)
            switch_camera.setVisibility(View.GONE);

        switch_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwitchCamera(view);
            }
        });
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Flash(view);
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  pickGallery();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
            }
        }


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(Scanner.this);
        mScannerView.setAspectTolerance(0.4f);
        mScannerView.startCamera(camera_state);
        mScannerView.setFlash(flash_state);
        mScannerView.setAutoFocus(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(Scanner.this::handleResult);
            }
        }, 2000);

        try {
            if (sharedpreferences.getBoolean(SoundPrefrences, true)) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);
                r.play();
            }
            if (sharedpreferences.getBoolean(VibratePrefences, true)) {
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  Toast.makeText(getContext(), "Contents = " + rawResult.getText() + ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        /*Calendar cal = Calendar.getInstance();
        cal.setTime(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR,-1);
        Date calendar = cal.getTime();*/
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Date calendar = Calendar.getInstance().getTime();

            SimpleDateFormat time = new SimpleDateFormat("dd/MMM/yyyy");
            SimpleDateFormat date = new SimpleDateFormat("hh:mm a");

            ScanItem item = new ScanItem();
            item.setFavourite(false);
            item.setType(rawResult.getBarcodeFormat().toString());
            item.setScanned_item(rawResult.getText());
            item.setDate(calendar);
            item.setDay(date.format(calendar));
            item.setTime(time.format(calendar));
            if (mDatabaseHelper == null)
                mDatabaseHelper = new DatabaseHelper(getActivity());
            mDatabaseHelper.AddOrUpdateScanItem(item);
            mScannerView.stopCamera();
            PushFragment(new ScanedText().newInstance(item), getString(R.string.scanned_text));
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                // Code to be executed when the interstitial ad is closed.
                Date calendar = Calendar.getInstance().getTime();

                SimpleDateFormat time = new SimpleDateFormat("dd/MMM/yyyy");
                SimpleDateFormat date = new SimpleDateFormat("hh:mm a");

                ScanItem item = new ScanItem();
                item.setFavourite(false);
                item.setType(rawResult.getBarcodeFormat().toString());
                item.setScanned_item(rawResult.getText());
                item.setDate(calendar);
                item.setDay(date.format(calendar));
                item.setTime(time.format(calendar));
                if (mDatabaseHelper == null)
                    mDatabaseHelper = new DatabaseHelper(getActivity());
                mDatabaseHelper.AddOrUpdateScanItem(item);
                mScannerView.stopCamera();
                PushFragment(new ScanedText().newInstance(item), getString(R.string.scanned_text));
            }
        });
    }

    public void Flash(View view) {
        if (hasFlash) {
            if (!flash_state) {
                flash_state = true;
                if (!mScannerView.getFlash())
                    mScannerView.setFlash(true);
                flash.setImageResource(R.drawable.ic_flash_on_black_24dp);
            } else {
                flash_state = false;
                if (mScannerView.getFlash())
                    mScannerView.setFlash(false);
                flash.setImageResource(R.drawable.ic_flash_off);
            }
        } else
            Toast.makeText(getContext(), getString(R.string.flash_support), Toast.LENGTH_SHORT).show();
    }

    public void SwitchCamera(View view) {
        if (camera_state == 0)
            camera_state = 1;
        else
            camera_state = 0;
        mScannerView.stopCamera();
        mScannerView.setResultHandler(Scanner.this);
        mScannerView.setAspectTolerance(0.4f);
        mScannerView.startCamera(camera_state);
        mScannerView.setFlash(flash_state);
        mScannerView.setAutoFocus(true);
    }

    public void PushFragment(Fragment fragment, String string) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment, string);
        ft.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mScannerView.stopCamera();
                mScannerView.startCamera(camera_state);
                mScannerView.setFlash(flash_state);
                mScannerView.setAutoFocus(true);

            } else
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.camera_permission), Toast.LENGTH_LONG).show();
        }
    }

}

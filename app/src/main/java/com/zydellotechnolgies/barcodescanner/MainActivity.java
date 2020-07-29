package com.zydellotechnolgies.barcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zydellotechnolgies.barcodescanner.fragments.GenerateCode;
import com.zydellotechnolgies.barcodescanner.fragments.History;
import com.zydellotechnolgies.barcodescanner.fragments.Scanner;
import com.zydellotechnolgies.barcodescanner.fragments.Settings;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.piruin.quickaction.ActionItem;
import me.piruin.quickaction.QuickAction;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.setting)
    ImageButton setting;
    @BindView(R.id.favourite)
    ImageButton favourite;
    @BindView(R.id.history)
    ImageButton history;
    @BindView(R.id.scanner)
    FloatingActionButton scanner;
    @BindView(R.id.adView)
    AdView adview;
    private AdRequest adRequest;
    private QuickAction quickAction;
    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        actionbar = getSupportActionBar();
        actionbar.setElevation(0);
        getSupportActionBar().setTitle(R.string.barcode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*List<String> testDeviceIds = Arrays.asList("E863B1EA9B0D999A512DE4A3375620BE");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
        */
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);
/*
        adview.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                adRequest = new AdRequest.Builder().build();
                adview.loadAd(adRequest);
                Log.e("onAdFailedToLoad", String.valueOf(errorCode));
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
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
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
*/

        //Config default color
        QuickAction.setDefaultColor(ResourcesCompat.getColor(getResources(), R.color.teal_50, null));
        QuickAction.setDefaultTextColor(Color.BLACK);
        ActionItem bar_code = new ActionItem(1, getString(R.string.barcode_generator), R.drawable.ic_barcode);
        ActionItem qr_code = new ActionItem(2, getString(R.string.qr_generator), R.drawable.ic_qr_code);
        bar_code.setSticky(false);
        qr_code.setSticky(false);
        quickAction = new QuickAction(this, QuickAction.HORIZONTAL);
        quickAction.setColorRes(R.color.teal_50);
        quickAction.setTextColorRes(R.color.teal_500);
        quickAction.setDividerColor(ContextCompat.getColor(this, R.color.teal_500));
        quickAction.addActionItem(bar_code, qr_code);
        quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(ActionItem item) {
                //   if (!item.isSticky()) quickAction.remove(item);
                switch (item.getActionId()) {
                    case 1:
                        PushFragment(new GenerateCode().newInstance(true), getString(R.string.generator_frag));
                        break;
                    case 2:
                        PushFragment(new GenerateCode().newInstance(false), getString(R.string.generator_frag));
                        break;
                }
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PushFragment(new History().newInstance(true), getString(R.string.history_frag));
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PushFragment(new History().newInstance(false), getString(R.string.history_frag));
            }
        });
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().findFragmentByTag(getString(R.string.scanner_main)) == null) {
                    PushFragment(new Scanner(), getString(R.string.scanner_main));
                }
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PushFragment(new Settings(), getString(R.string.setting_frag));
            }
        });

        PushFragment(new Scanner(), getString(R.string.scanner_main));
    }

    public void PushFragment(Fragment fragment, String string) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment, string);
        ft.commit();
    }

    public void ShowPopup(View view) {
        quickAction.show(view);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(getString(R.string.scanned_text)) != null) {
            PushFragment(new Scanner(), getString(R.string.scanner_main));
        } else if (getSupportFragmentManager().findFragmentByTag(getString(R.string.history_detailed_frag)) != null) {
            PushFragment(new History().newInstance(true), getString(R.string.history_frag));
        } else if (getSupportFragmentManager().findFragmentByTag(getString(R.string.favourite_detailed_frag)) != null) {
            PushFragment(new History().newInstance(false), getString(R.string.history_frag));
        } else if (getSupportFragmentManager().findFragmentByTag(getString(R.string.generator_frag_detail_bar)) != null) {
            PushFragment(new GenerateCode().newInstance(true), getString(R.string.generator_frag));
        } else if (getSupportFragmentManager().findFragmentByTag(getString(R.string.generator_frag_detail_qr)) != null) {
            PushFragment(new GenerateCode().newInstance(false), getString(R.string.generator_frag));
        } else if (getSupportFragmentManager().findFragmentByTag(getString(R.string.generated_frag_code_bar)) != null) {
            PushFragment(new GenerateCode().newInstance(true), getString(R.string.generator_frag));
        } else if (getSupportFragmentManager().findFragmentByTag(getString(R.string.generated_frag_code_qr)) != null) {
            PushFragment(new GenerateCode().newInstance(false), getString(R.string.generator_frag));
        } else super.onBackPressed();
    }
}

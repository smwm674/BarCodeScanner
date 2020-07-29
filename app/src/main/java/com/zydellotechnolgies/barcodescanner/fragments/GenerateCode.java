package com.zydellotechnolgies.barcodescanner.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.zydellotechnolgies.barcodescanner.MainActivity;
import com.zydellotechnolgies.barcodescanner.R;
import com.zydellotechnolgies.barcodescanner.model.ScanItem;
import com.zydellotechnolgies.barcodescanner.model.ScanItemCreated;
import com.zydellotechnolgies.barcodescanner.utils.DatabaseHelper;
import com.zydellotechnolgies.barcodescanner.utils.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class GenerateCode extends Fragment {
    private static final String ARG_PARAM1 = "BarCode";

    private boolean isBarCode;
    private String mParam2;

    public GenerateCode() {
        // Required empty public constructor
    }

    public static GenerateCode newInstance(boolean isBarCode) {
        GenerateCode fragment = new GenerateCode();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isBarCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isBarCode = getArguments().getBoolean(ARG_PARAM1);
        }
    }

    @BindView(R.id.email_layout)
    CardView email;
    @BindView(R.id.message_layout)
    CardView message;
    @BindView(R.id.location_layout)
    CardView location;
    @BindView(R.id.event_layout)
    CardView event;
    @BindView(R.id.contact_layout)
    CardView contact;
    @BindView(R.id.telephone_layout)
    CardView telephone;
    @BindView(R.id.text_layout)
    CardView text;
    @BindView(R.id.wifi_layout)
    CardView wifi;
    @BindView(R.id.url_layout)
    CardView url;
    @BindView(R.id.code)
    TextInputEditText product_code;
    @BindView(R.id.barcode_layout)
    RelativeLayout barcode_layout;
    @BindView(R.id.view)
    ImageView view;

    private BitMatrix bitMatrix = null;
    private int size = 660;
    private int size_width = 660;
    private int size_height = 264;
    private Bitmap bitmap = null;
    private String code_type = "Barcode";
    private AwesomeValidation mAwesomeValidation;
    private DatabaseHelper mDatabaseHelper = null;
    private String savePath = Environment.getExternalStorageDirectory().getPath() + "/BarCodeScanner/";

    private InterstitialAd mInterstitialAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_generate_code, container, false);
        ButterKnife.bind(this, root);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        setHasOptionsMenu(true);
        mDatabaseHelper = new DatabaseHelper(getActivity());
        mAwesomeValidation = new AwesomeValidation(BASIC);

        if (isBarCode) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.barcode_generator);
            email.setVisibility(View.GONE);
            message.setVisibility(View.GONE);
            location.setVisibility(View.GONE);
            event.setVisibility(View.GONE);
            telephone.setVisibility(View.GONE);
            contact.setVisibility(View.GONE);
            text.setVisibility(View.GONE);
            url.setVisibility(View.GONE);
            wifi.setVisibility(View.GONE);
            barcode_layout.setVisibility(View.VISIBLE);
            mAwesomeValidation.addValidation(product_code, RegexTemplate.NOT_EMPTY, "Product Code Required");
        } else {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.qr_generator);
            email.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            location.setVisibility(View.VISIBLE);
            event.setVisibility(View.VISIBLE);
            telephone.setVisibility(View.VISIBLE);
            contact.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
            url.setVisibility(View.VISIBLE);
            wifi.setVisibility(View.VISIBLE);
            barcode_layout.setVisibility(View.GONE);

            mInterstitialAd = new InterstitialAd(getContext());
            mInterstitialAd.setAdUnitId(getString(R.string.admob_interstial));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBarCode)
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.email), getString(R.string.barcode_generator)), getString(R.string.generator_frag_detail_bar));
                else {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.email), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
                    }
                }
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.email), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
                    }
                });
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBarCode)
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.message), getString(R.string.barcode_generator)), getString(R.string.generator_frag_detail_bar));
                else
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.message), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBarCode)
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.location), getString(R.string.barcode_generator)), getString(R.string.generator_frag_detail_bar));
                else {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.location), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
                    }
                }
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.location), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
                    }
                });
            }
        });
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBarCode)
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.event), getString(R.string.barcode_generator)), getString(R.string.generator_frag_detail_bar));
                else
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.event), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBarCode)
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.contact), getString(R.string.barcode_generator)), getString(R.string.generator_frag_detail_bar));
                else {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.contact), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
                    }
                }
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.contact), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
                    }
                });
            }
        });
        telephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBarCode)
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.telephone), getString(R.string.barcode_generator)), getString(R.string.generator_frag_detail_bar));
                else
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.telephone), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBarCode)
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.text), getString(R.string.barcode_generator)), getString(R.string.generator_frag_detail_bar));
                else {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.text), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
                    }
                }
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.text), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
                    }
                });
            }
        });
        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBarCode)
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.wifi), getString(R.string.barcode_generator)), getString(R.string.generator_frag_detail_bar));
                else
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.wifi), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
            }
        });
        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBarCode)
                    PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.url), getString(R.string.barcode_generator)), getString(R.string.generator_frag_detail_bar));
                else {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.url), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
                    }
                }
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        PushFragment(new GenerateCodeDetail().newInstance(getString(R.string.url), getString(R.string.qr_generator)), getString(R.string.generator_frag_detail_qr));
                    }
                });
            }
        });

        return root;
    }

    MenuItem share, save, generate_bar_code = null;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.code_generated, menu);
        save = menu.findItem(R.id.save);
        share = menu.findItem(R.id.share);
        generate_bar_code = menu.findItem(R.id.generate_bar);
        if (isBarCode) generate_bar_code.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.generated:
                if (item != null) {
                    if (isBarCode)
                        PushFragment(new CodeGenerated(), getString(R.string.generated_frag_code_bar));
                    else
                        PushFragment(new CodeGenerated(), getString(R.string.generated_frag_code_qr));
                }
                break;
            case R.id.generate_bar:
                utils.hideKeyboard(getActivity());
                if (mAwesomeValidation.validate()) {
                    try {
                        bitmap = CreateImage(product_code.getText().toString(), code_type);
                    } catch (WriterException we) {
                        we.printStackTrace();
                    }
                    if (bitmap != null) {
                        view.setImageBitmap(bitmap);
                    }
                    generate_bar_code.setVisible(false);
                    save.setVisible(true);
                    share.setVisible(true);

                    Date calendar = Calendar.getInstance().getTime();
                    SimpleDateFormat time = new SimpleDateFormat("dd/MMM/yyyy");
                    SimpleDateFormat date = new SimpleDateFormat("hh:mm a");

                    ScanItemCreated ItemCreated = new ScanItemCreated();
                    ItemCreated.setFavourite(false);
                    ItemCreated.setType("BAR_CODE");
                    ItemCreated.setScanned_item(product_code.getText().toString());
                    ItemCreated.setDate(calendar);
                    ItemCreated.setDay(date.format(calendar));
                    ItemCreated.setTime(time.format(calendar));

                    if (mDatabaseHelper == null)
                        mDatabaseHelper = new DatabaseHelper(getActivity());

                    mDatabaseHelper.AddOrUpdateScanItemCreated(ItemCreated);
                }
                break;
            case R.id.share:

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    String bitmapPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, code_type, code_type + "generated from Bar Code Scanner");
                    Uri bitmapUri = Uri.parse(bitmapPath);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                    startActivity(Intent.createChooser(intent, "Share"));
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                }
                break;
            case R.id.save:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        //  String bitmapPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, code_type, code_type + "generated from Bar Code Scanner");
                        File file = new File(savePath, new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(Calendar.getInstance().getTime()).toString() + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                        FileOutputStream fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush(); // Not really required
                        fOut.close();
                        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                        scanGallery(getContext(), savePath);
                        Toast.makeText(getActivity(), "Image Saved", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                    // MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, code_type, code_type + "generated from Bar Code Scanner");

                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }

                break;
        }
        return true;

    }

    public void PushFragment(Fragment fragment, String string) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment, string);
        ft.commit();
    }

    public Bitmap CreateImage(String message, String type) throws WriterException {
        BitMatrix bitMatrix = null;
        // BitMatrix bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);
        switch (type) {
            case "QR Code":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);
                break;
            case "Barcode":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_128, size_width, size_height);
                break;
            case "Data Matrix":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.DATA_MATRIX, size, size);
                break;
            case "PDF 417":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.PDF_417, size_width, size_height);
                break;
            case "Barcode-39":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_39, size_width, size_height);
                break;
            case "Barcode-93":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_93, size_width, size_height);
                break;
            case "AZTEC":
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.AZTEC, size, size);
                break;
            default:
                bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);
                break;
        }
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (bitMatrix.get(j, i)) {
                    pixels[i * width + j] = 0xff000000;
                } else {
                    pixels[i * width + j] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.
                    OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        //  String bitmapPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, code_type, code_type + "generated from Bar Code Scanner");
                        File file = new File(savePath, new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(Calendar.getInstance().getTime()).toString() + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                        FileOutputStream fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush(); // Not really required
                        fOut.close();
                        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                        scanGallery(getContext(), savePath);
                        Toast.makeText(getActivity(), "Image Saved", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.camera_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String bitmapPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, code_type, code_type + "generated from Bar Code Scanner");
                    Uri bitmapUri = Uri.parse(bitmapPath);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                    startActivity(Intent.createChooser(intent, "Share"));
                } else {
                    Toast.makeText(getActivity(), getString(R.string.camera_permission), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}


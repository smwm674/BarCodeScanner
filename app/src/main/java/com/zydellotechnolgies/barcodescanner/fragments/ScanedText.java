package com.zydellotechnolgies.barcodescanner.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zydellotechnolgies.barcodescanner.MainActivity;
import com.zydellotechnolgies.barcodescanner.R;
import com.zydellotechnolgies.barcodescanner.model.ScanItem;
import com.zydellotechnolgies.barcodescanner.utils.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import ezvcard.Ezvcard;
import ezvcard.VCard;

import static android.content.Context.WIFI_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanedText#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanedText extends Fragment {
    private static final String ARG_PARAM1 = "item";

    private ScanItem item = null;

    public ScanedText() {
        // Required empty public constructor
    }

    public static ScanedText newInstance(ScanItem param1) {
        ScanedText fragment = new ScanedText();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (ScanItem) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @BindView(R.id.date)
    TextView DateTime;
    @BindView(R.id.text)
    TextView info;
    @BindView(R.id.label)
    TextView label;
    @BindView(R.id.information)
    TextView information;
    @BindView(R.id.fav)
    ImageView favourite;
    @BindView(R.id.lookup)
    Button lookup;
    private static boolean isFavourite = false;
    private DatabaseHelper mDatabaseHelper = null;

    private static final int PERMISSION_FINE_LOCATION = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_scaned_text, container, false);
        ButterKnife.bind(this, root);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.browser);
        mDatabaseHelper = new DatabaseHelper(getActivity());
        setHasOptionsMenu(true);

        if (item != null) {
            DateTime.setText(item.getDay() + " " + item.getTime());
            SpannableString content = new SpannableString(DateTime.getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            DateTime.setText(item.getDay() + " " + item.getTime());
            info.setText(item.getScanned_item());
            information.setText(item.getScanned_item());
            label.setText("Text Scanned");
            if (item.isFavourite())
                favourite.setImageResource(R.drawable.ic_favourite);
            else favourite.setImageResource(R.drawable.ic_unfavourite);

            if (item.getScanned_item().contains("MATMSG")) //email
            {
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.email);
                info.setText(item.getScanned_item().substring(item.getScanned_item().indexOf("MATMSG:TO:") + 10, item.getScanned_item().indexOf(";SUB:")));
                lookup.setText("Email");
                information.setText(Html.fromHtml("<b>" + "To:" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf("MATMSG:TO:") + 10, item.getScanned_item().indexOf(";SUB:"))
                        + "<b>" + "<br>" + "Subject" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf(";SUB:") + 5, item.getScanned_item().indexOf(";BODY:"))
                        + "<b>" + "<br>" + "Message" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf(";BODY:") + 6, item.getScanned_item().length() - 1)));
            } else if (item.getScanned_item().contains("smsto")) //message
            {
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.message);
                info.setText(item.getScanned_item().substring(item.getScanned_item().indexOf("smsto:") + 6, item.getScanned_item().indexOf(":", 7)));
                lookup.setText("Message");
                information.setText(Html.fromHtml("<b>" + "To" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf("smsto:") + 6, item.getScanned_item().indexOf(":", 7))
                        + "<b>" + "<br>" + "Message" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf(":", 7) + 1, item.getScanned_item().length())));
            } else if (item.getScanned_item().contains("SMSTO")) //message
            {
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.message);
                info.setText(item.getScanned_item().substring(item.getScanned_item().indexOf("SMSTO:") + 6, item.getScanned_item().indexOf(":", 7)));
                lookup.setText("Message");
                information.setText(Html.fromHtml("<b>" + "To" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf("SMSTO:") + 6, item.getScanned_item().indexOf(":", 7))
                        + "<b>" + "<br>" + "Message" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf(":", 7) + 1, item.getScanned_item().length())));
            } else if (item.getScanned_item().contains("geo")) //location
            {
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.location);
                if (item.getScanned_item().contains("?q=")) {
                    info.setText(item.getScanned_item().substring(item.getScanned_item().indexOf("geo:") + 4, item.getScanned_item().indexOf("?q=")));
                    information.setText(Html.fromHtml("<b>" + "Latitude" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf("geo:") + 4, item.getScanned_item().indexOf(","))
                            + "<b>" + "<br>" + "Longitude" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf(",") + 1, item.getScanned_item().indexOf("?q="))
                            + "<b>" + "<br>" + "Query" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf("?q=") + 3, item.getScanned_item().length())));
                } else {
                    info.setText(item.getScanned_item().substring(item.getScanned_item().indexOf("geo:") + 4, item.getScanned_item().length()));
                    information.setText(Html.fromHtml("<b>" + "Latitude" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf("geo:") + 4, item.getScanned_item().indexOf(","))
                            + "<b>" + "<br>" + "Longitude" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf(",") + 1, item.getScanned_item().length())));
                }
                lookup.setText("Open in Map");
            } else if (item.getScanned_item().contains("VEVENT")) //event
            {
                String title = null, begin_time = null, end_time = null;
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.event);
                info.setText(item.getScanned_item().substring(item.getScanned_item().indexOf("SUMMARY:") + 8, item.getScanned_item().indexOf("DTSTART")));
                lookup.setText("Open in Calendar");
                try {
                    title = item.getScanned_item().substring(item.getScanned_item().indexOf("SUMMARY:") + 8, item.getScanned_item().indexOf("DTSTART"));
                } catch (Exception e) {
                    title = "";
                }
                information.setText(Html.fromHtml("<b>" + "Title" + "</b>" + "<br>" + title));
                //   information.setText(item.getScanned_item());
            } else if (item.getScanned_item().contains("MECARD")) //contact
            {
                String name = null, phone = null, email = null, address = null;
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.contact);
                lookup.setText("Add to Contact");
                try {
                    address = item.getScanned_item().substring(item.getScanned_item().indexOf(";ADR:") + 5, item.getScanned_item().length());
                    info.setText(address);
                } catch (Exception e) {
                }
                try {
                    email = item.getScanned_item().substring(item.getScanned_item().indexOf(";EMAIL:") + 7, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("EMAIL:") + 7));
                    info.setText(email);
                } catch (Exception e) {
                }
                try {
                    phone = item.getScanned_item().substring(item.getScanned_item().indexOf(";TEL:") + 5, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("TEL:") + 4));
                    info.setText(phone);
                } catch (Exception e) {
                }
                try {
                    name = item.getScanned_item().substring(item.getScanned_item().indexOf(":N:") + 3, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf(":N:") + 4));
                    info.setText(name);
                } catch (Exception e) {
                }
                information.setText(Html.fromHtml("<b>" + "Fullname" + "</b>" + "<br>" + name +
                        "<b>" + "<br>" + "Phone" + "</b>" + "<br>" + phone +
                        "<b>" + "<br>" + "Email" + "</b>" + "<br>" + email +
                        "<b>" + "<br>" + "Address" + "</b>" + "<br>" + address));
            } else if (item.getScanned_item().contains("BEGIN:VCARD")) //contact
            {
                String name = null, phone = null, email = null, address = null;
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.contact);
                VCard vcard = Ezvcard.parse(item.getScanned_item()).first();
                lookup.setText("Add to Contact");
                try {
                    address = vcard.getAddresses().get(0).getStreetAddress().toString() + vcard.getAddresses().get(0).getCountry().toString();
                    info.setText(address);
                } catch (Exception e) {
                    address = "";
                }
                try {
                    email = vcard.getEmails().get(0).getValue().toString();
                    info.setText(email);
                } catch (Exception e) {
                    email = "";
                }
                try {
                    phone = vcard.getTelephoneNumbers().get(0).getText().toString();
                    info.setText(phone);
                } catch (Exception e) {
                    phone = "";
                }
                try {
                    name = vcard.getFormattedName().getValue().toString();
                    info.setText(name);
                    if (name == null || name.equals("") || name.equals(" ") || name.isEmpty()) {
                        if (item.getScanned_item().contains("\nN:") || item.getScanned_item().contains("\rN:")) {
                            if (item.getScanned_item().contains("VERSION:")) {
                                info.setText(item.getScanned_item().substring(item.getScanned_item().indexOf("N:", item.getScanned_item().indexOf("VERSION:") + 8) + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("N:") + 2)));
                                name = info.getText().toString();
                            } else {
                                info.setText(item.getScanned_item().substring(item.getScanned_item().indexOf("N:") + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("N:") + 2)));
                                name = info.getText().toString();
                            }
                        }
                    }

                } catch (Exception e) {
                    name = "";
                    try {
                        if (name == null || name.equals("") || name.equals(" ") || name.isEmpty()) {
                            if (item.getScanned_item().contains("\nN:") || item.getScanned_item().contains("\rN:")) {
                                if (item.getScanned_item().contains("VERSION:")) {
                                    info.setText(item.getScanned_item().substring(item.getScanned_item().indexOf("N:", item.getScanned_item().indexOf("VERSION:") + 8) + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("N:") + 2)))
                                    ;
                                    name = info.getText().toString();
                                } else {
                                    info.setText(item.getScanned_item().substring(item.getScanned_item().indexOf("N:") + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("N:") + 2)));
                                    name = info.getText().toString();
                                }
                            }
                        }
                    } catch (Exception ee) {
                        name = "";
                    }
                }
                information.setText(Html.fromHtml("<b>" + "Fullname" + "</b>" + "<br>" + name +
                        "<b>" + "<br>" + "Phone" + "</b>" + "<br>" + phone +
                        "<b>" + "<br>" + "Email" + "</b>" + "<br>" + email +
                        "<b>" + "<br>" + "Address" + "</b>" + "<br>" + address));
            } else if (item.getScanned_item().contains("tel")) //telephone
            {
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.telephone);
                info.setText(item.getScanned_item().substring(item.getScanned_item().indexOf("tel:") + 4, item.getScanned_item().length()));
                lookup.setText("Make a Call");
                information.setText(Html.fromHtml("<b>" + "Telephone" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf("tel:") + 4, item.getScanned_item().length())));
            } else if (item.getScanned_item().contains("WIFI:"))//wifi
            {
                info.setText(item.getScanned_item().substring(item.getScanned_item().indexOf("S:") + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("S:") + 3)));
                lookup.setText("WIFI");
                information.setText(Html.fromHtml("<b>" + "SSID/Network Name" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf("S:") + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("S:") + 3))
                        + "<b>" + "<br>" + "Type" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf("T:") + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("T:") + 3))
                        + "<b>" + "<br>" + "Password" + "</b>" + "<br>" + item.getScanned_item().substring(item.getScanned_item().indexOf("P:") + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("P:") + 3))));

            } else if (item.getScanned_item().contains("https://")) //url
            {
                info.setText(item.getScanned_item());
                lookup.setText("Open in browser");
                information.setText(item.getScanned_item());
            } else // text
            {
                info.setText(item.getScanned_item());
                information.setText(item.getScanned_item());

            }

        }

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFavourite) {
                    isFavourite = true;
                    item.setFavourite(isFavourite);
                    favourite.setImageResource(R.drawable.ic_favourite);
                    if (mDatabaseHelper == null)
                        mDatabaseHelper = new DatabaseHelper(getActivity());
                    mDatabaseHelper.AddOrUpdateScanItem(item);
                } else {
                    isFavourite = false;
                    item.setFavourite(isFavourite);
                    favourite.setImageResource(R.drawable.ic_unfavourite);
                    if (mDatabaseHelper == null)
                        mDatabaseHelper = new DatabaseHelper(getActivity());
                    mDatabaseHelper.AddOrUpdateScanItem(item);
                }
            }
        });

        lookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (item.getScanned_item().contains("MATMSG")) //email
                {
                    Intent email = new Intent(android.content.Intent.ACTION_SEND);
                    email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    email.setType("vnd.android.cursor.item/email");
                    email.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{item.getScanned_item().substring(item.getScanned_item().indexOf("MATMSG:TO:") + 10, item.getScanned_item().indexOf(";SUB:"))});
                    email.putExtra(Intent.EXTRA_SUBJECT, item.getScanned_item().substring(item.getScanned_item().indexOf(";SUB:") + 5, item.getScanned_item().indexOf(";BODY:")));
                    email.putExtra(Intent.EXTRA_TEXT, item.getScanned_item().substring(item.getScanned_item().indexOf(";BODY:") + 6, item.getScanned_item().length() - 1));
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));

                } else if (item.getScanned_item().contains("smsto")) //message
                {
                    sendSMSMessage(item.getScanned_item().substring(item.getScanned_item().indexOf("smsto:") + 6, item.getScanned_item().indexOf(":", 7)), item.getScanned_item().substring(item.getScanned_item().indexOf(":", 7) + 1, item.getScanned_item().length()));
                } else if (item.getScanned_item().contains("SMSTO")) //message
                {
                    sendSMSMessage(item.getScanned_item().substring(item.getScanned_item().indexOf("SMSTO:") + 6, item.getScanned_item().indexOf(":", 7)), item.getScanned_item().substring(item.getScanned_item().indexOf(":", 7) + 1, item.getScanned_item().length()));
                } else if (item.getScanned_item().contains("geo")) //location
                {
                    Uri gmmIntentUri = Uri.parse(item.getScanned_item());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                } else if (item.getScanned_item().contains("VEVENT")) //event
                {
                    try {
                        Calendar cal = Calendar.getInstance();
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra("title", item.getScanned_item().substring(item.getScanned_item().indexOf("SUMMARY:") + 8, item.getScanned_item().indexOf("DTSTART")));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (item.getScanned_item().contains("MECARD")) //contact
                {

                    Intent my = new Intent(Intent.ACTION_INSERT);
                    my.setType(ContactsContract.Contacts.CONTENT_TYPE);
                    try {
                        my.putExtra(ContactsContract.Intents.Insert.NAME, item.getScanned_item().substring(item.getScanned_item().indexOf(":N:") + 3, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf(":N:") + 4)));
                    } catch (Exception e) {
                    }
                    try {
                        my.putExtra(ContactsContract.Intents.Insert.PHONE, item.getScanned_item().substring(item.getScanned_item().indexOf(";TEL:") + 5, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("TEL:") + 4)));
                    } catch (Exception e) {
                    }
                    try {
                        my.putExtra(ContactsContract.Intents.Insert.EMAIL, item.getScanned_item().substring(item.getScanned_item().indexOf(";EMAIL:") + 7, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("EMAIL:") + 7)));
                    } catch (Exception e) {
                    }
                    try {
                        my.putExtra(ContactsContract.Intents.Insert.POSTAL, item.getScanned_item().substring(item.getScanned_item().indexOf(";ADR:") + 5, item.getScanned_item().length()));
                    } catch (Exception e) {
                    }
                    startActivity(my);
                } else if (item.getScanned_item().contains("BEGIN:VCARD")) //contact
                {
                    VCard vcard = Ezvcard.parse(item.getScanned_item()).first();
                    Intent my = new Intent(Intent.ACTION_INSERT);
                    my.setType(ContactsContract.Contacts.CONTENT_TYPE);
                    try {
                        my.putExtra(ContactsContract.Intents.Insert.NAME, vcard.getFormattedName().getValue().toString());
                    } catch (Exception e) {
                    }
                    try {
                        my.putExtra(ContactsContract.Intents.Insert.PHONE, vcard.getTelephoneNumbers().get(0).getText().toString());
                    } catch (Exception e) {
                    }
                    try {
                        my.putExtra(ContactsContract.Intents.Insert.EMAIL, vcard.getEmails().get(0).getValue().toString());
                    } catch (Exception e) {
                    }
                    try {
                        my.putExtra(ContactsContract.Intents.Insert.POSTAL, vcard.getAddresses().get(0).getStreetAddress().toString() + vcard.getAddresses().get(0).getCountry().toString());
                    } catch (Exception e) {
                    }
                    startActivity(my);
                } else if (item.getScanned_item().contains("tel")) //telephone
                {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(item.getScanned_item()));
                    getContext().startActivity(intent);
                } else if (item.getScanned_item().contains("WIFI:"))//wifi
                {
                    try {
                        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        if (wifiManager.isWifiEnabled()) {
                            Connect_WIFI(wifiManager);
                        } else {
                            wifiManager.setWifiEnabled(true);
                            Connect_WIFI(wifiManager);
                        }
                    } catch (Exception e) {
                        startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                    }
                } else if (item.getScanned_item().contains("https://")) //url
                {
                    Uri uri = Uri.parse("http://www.google.com/#q=" + item.getScanned_item());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else // text
                {
                    Uri uri = Uri.parse("http://www.google.com/#q=" + item.getScanned_item());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        return root;
    }

    protected void sendSMSMessage(String phoneNo, String message) {
        Uri uri = Uri.parse("smsto:" + phoneNo);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", message);
        startActivity(intent);
        /*SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        Toast.makeText(getActivity().getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (wifiManager.isWifiEnabled()) {
                        Connect_WIFI(wifiManager);
                    } else {
                        wifiManager.setWifiEnabled(true);
                        Connect_WIFI(wifiManager);
                    }
                } catch (Exception e) {
                    startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                }
            } else
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.camera_permission), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.scanned_text, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                if (item != null)
                    share();
                break;
        }
        return true;

    }

    public void share() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity()).setType("text/plain")
                .setText(information.getText().toString()).getIntent();
        startActivity(Intent.createChooser(shareIntent, "Share Text"));
    }

    void Connect_WIFI(WifiManager wifiManager) {
        String networkSSID = item.getScanned_item().substring(item.getScanned_item().indexOf("S:") + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("S:") + 3));
        String networkPass = item.getScanned_item().substring(item.getScanned_item().indexOf("P:") + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("P:") + 3));

      /*  WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);
          wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);

        if(!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);

        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();*/

      /*WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        if (!item.getScanned_item().substring(item.getScanned_item().indexOf("T:") + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("T:") + 3)).contains("WPA") && item.getScanned_item().substring(item.getScanned_item().indexOf("T:") + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("T:") + 3)).contains("WEP")) {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (item.getScanned_item().substring(item.getScanned_item().indexOf("T:") + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("T:") + 3)).contains("WEP")) {
            conf.wepKeys[0] = "\"" + networkPass + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if (item.getScanned_item().substring(item.getScanned_item().indexOf("T:") + 2, item.getScanned_item().indexOf(";", item.getScanned_item().indexOf("T:") + 3)).contains("WPA")) {
            conf.preSharedKey = "\"" + networkPass + "\"";
        }

        wifiManager.addNetwork(conf);

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Connecting...", true);
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ;
                    {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
                    }
                }

                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                for (WifiConfiguration i : list) {
                    if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, true);
                        wifiManager.reconnect();
                        // startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                        break;
                    }
                }
                dialog.dismiss();
            }
        }, 3000); // 3000 milliseconds delay
*/


        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Connecting...", true);
        dialog.show();

       /* Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
            }
        }, 3000);*/
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (item.getScanned_item().substring(item.getScanned_item().indexOf("T:") + 2, item.getScanned_item().indexOf(";P:") - 3).contains("WPA")) {
                        Log.i("Found WPA", item.getScanned_item().substring(item.getScanned_item().indexOf("T:") + 2, item.getScanned_item().indexOf(";P:") - 3));
                        ConnectToNetworkWPA(networkSSID, networkPass);
                    } else if (item.getScanned_item().substring(item.getScanned_item().indexOf("T:") + 2, item.getScanned_item().indexOf(";P:") - 3).contains("WEP")) {
                        Log.i("Found WPE", item.getScanned_item().substring(item.getScanned_item().indexOf("T:") + 2, item.getScanned_item().indexOf(";P:") - 3));
                        ConnectToNetworkWEP(networkSSID, networkPass);
                    }
                    dialog.dismiss();
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
                    builder.setSsid(networkSSID);
                    builder.setWpa2Passphrase(networkPass).build();

                    WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();
                    NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();
                    networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
                    // networkRequestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED);
                    //networkRequestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED);
                    networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
                    NetworkRequest networkRequest = networkRequestBuilder.build();

                    ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (cm != null) {
                        cm.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onAvailable(@NonNull Network network) {
                                super.onAvailable(network);
                                cm.bindProcessToNetwork(network);
                            }
                        });
                    }
                    dialog.dismiss();
                }
            }).start();
        }
    }

    public boolean ConnectToNetworkWEP(String networkSSID, String password) {
        try {
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain SSID in quotes
            conf.wepKeys[0] = "\"" + password + "\""; //Try it with quotes first

            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.OPEN);
            conf.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.SHARED);


            WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int networkId = wifiManager.addNetwork(conf);

            if (networkId == -1) {
                //Try it again with no quotes in case of hex password
                conf.wepKeys[0] = password;
                networkId = wifiManager.addNetwork(conf);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
                }
            }
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    break;
                }
            }

            //WiFi Connection success, return true
            return true;
        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    public boolean ConnectToNetworkWPA(String networkSSID, String password) {
        try {
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain SSID in quotes

            conf.preSharedKey = "\"" + password + "\"";

            conf.status = WifiConfiguration.Status.ENABLED;
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            Log.d("connecting", conf.SSID + " " + conf.preSharedKey);

            WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.addNetwork(conf);

            Log.d("after connecting", conf.SSID + " " + conf.preSharedKey);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
                }
            }
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    Log.d("re connecting", i.SSID + " " + conf.preSharedKey);
                    break;
                }
            }


            //WiFi Connection success, return true
            return true;
        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

}

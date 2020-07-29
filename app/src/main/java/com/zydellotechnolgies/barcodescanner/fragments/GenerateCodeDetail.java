package com.zydellotechnolgies.barcodescanner.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
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
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.bottlerocketstudios.barcode.generation.ui.BarcodeView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.zydellotechnolgies.barcodescanner.MainActivity;
import com.zydellotechnolgies.barcodescanner.R;
import com.zydellotechnolgies.barcodescanner.model.ScanItemCreated;
import com.zydellotechnolgies.barcodescanner.utils.DatabaseHelper;
import com.zydellotechnolgies.barcodescanner.utils.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.WINDOW_SERVICE;
import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class GenerateCodeDetail extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String ARG_PARAM1 = "Type";
    private static final String ARG_PARAM2 = "Code_Type";

    private String type, code_type;

    public GenerateCodeDetail() {
        // Required empty public constructor
    }

    public static GenerateCodeDetail newInstance(String type, String code_type) {
        GenerateCodeDetail fragment = new GenerateCodeDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, type);
        args.putString(ARG_PARAM2, code_type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_PARAM1);
            code_type = getArguments().getString(ARG_PARAM2);
        }
    }

    @BindView(R.id.email_layout)
    RelativeLayout email;
    @BindView(R.id.message_layout)
    RelativeLayout message;
    @BindView(R.id.location_layout)
    RelativeLayout location;
    @BindView(R.id.event_layout)
    RelativeLayout event;
    @BindView(R.id.contact_layout)
    RelativeLayout contact;
    @BindView(R.id.telephone_layout)
    RelativeLayout telephone;
    @BindView(R.id.text_layout)
    RelativeLayout text;
    @BindView(R.id.wifi_layout)
    RelativeLayout wifi;
    @BindView(R.id.url_layout)
    RelativeLayout url;
    @BindView(R.id.generated_layout)
    RelativeLayout generated_layout;

    @BindView(R.id.email_to)
    TextInputEditText email_to;
    @BindView(R.id.email_subject)
    TextInputEditText email_subject;
    @BindView(R.id.email_message)
    TextInputEditText email_message;
    @BindView(R.id.message_to)
    TextInputEditText message_to;
    @BindView(R.id.message_message)
    TextInputEditText message_message;
    @BindView(R.id.message_add)
    ImageButton message_add;
    @BindView(R.id.location_latitude)
    TextInputEditText location_latitude;
    @BindView(R.id.location_longitude)
    TextInputEditText location_longitude;
    @BindView(R.id.event_title)
    TextInputEditText event_title;
    @BindView(R.id.event_location)
    TextInputEditText event_location;
    @BindView(R.id.event_description)
    TextInputEditText event_description;
    @BindView(R.id.event_begin_time)
    TextInputEditText event_begin_time;
    @BindView(R.id.event_end_time)
    TextInputEditText event_end_time;
    @BindView(R.id.contact_full_name)
    TextInputEditText contact_full_name;
    @BindView(R.id.contact_address)
    TextInputEditText contact_address;
    @BindView(R.id.contact_phone)
    TextInputEditText contact_phone;
    @BindView(R.id.contact_email)
    TextInputEditText contact_email;
    @BindView(R.id.wifi_ssid)
    TextInputEditText wifi_ssid;
    @BindView(R.id.wifi_password)
    TextInputEditText wifi_password;
    @BindView(R.id.contact_import)
    Button contact_import;
    @BindView(R.id.telephone_number)
    TextInputEditText telephone_number;
    @BindView(R.id.telephone_import)
    Button telephone_import;
    @BindView(R.id.text_text)
    TextInputEditText text_text;
    @BindView(R.id.url_http)
    TextInputEditText url_http;
    @BindView(R.id.view)
    ImageView view;
    @BindView(R.id.label)
    TextView displayed_label;
    @BindView(R.id.inserted_text)
    TextView displayed_text;
    @BindView(R.id.wifi_type)
    Spinner wifi_type;

    private String savePath = Environment.getExternalStorageDirectory().getPath() + "/BarCodeScanner/";
    private AwesomeValidation mAwesomeValidation;
    private Bitmap bitmap = null;
    private QRGEncoder qrgEncoder;
    private int smallerDimension;
    private DatabaseHelper mDatabaseHelper = null;
    private final int REQUEST_CODE_MESSAGE = 01, REQUEST_CODE_CONTACT = 02, REQUEST_CODE_TELEPHONE = 03;
    String event_end = null, event_begin = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_generate_code_detail, container, false);
        ButterKnife.bind(this, root);
        setHasOptionsMenu(true);
        mAwesomeValidation = new AwesomeValidation(BASIC);
        mDatabaseHelper = new DatabaseHelper(getActivity());

        WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        if (code_type.equals(getString(R.string.barcode_generator))) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.create_barcode);
        } else {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.create_qr);
        }
        if (type.equals(getString(R.string.email))) {
            email.setVisibility(View.VISIBLE);
            mAwesomeValidation.addValidation(email_to, android.util.Patterns.EMAIL_ADDRESS, "Invalid Email Format");
            mAwesomeValidation.addValidation(email_to, RegexTemplate.NOT_EMPTY, "Email Required");
            mAwesomeValidation.addValidation(email_message, RegexTemplate.NOT_EMPTY, "Message Required");
        } else if (type.equals(getString(R.string.message))) {
            message.setVisibility(View.VISIBLE);
            mAwesomeValidation.addValidation(message_to, RegexTemplate.NOT_EMPTY, "Number Required");
            mAwesomeValidation.addValidation(message_message, RegexTemplate.NOT_EMPTY, "Message Required");
        } else if (type.equals(getString(R.string.location))) {
            location.setVisibility(View.VISIBLE);
            mAwesomeValidation.addValidation(location_latitude, RegexTemplate.NOT_EMPTY, "Latitude Required");
            mAwesomeValidation.addValidation(location_longitude, RegexTemplate.NOT_EMPTY, "Longitude Required");
        } else if (type.equals(getString(R.string.event))) {
            event.setVisibility(View.VISIBLE);
            mAwesomeValidation.addValidation(event_title, RegexTemplate.NOT_EMPTY, "Title Required");
            mAwesomeValidation.addValidation(event_begin_time, RegexTemplate.NOT_EMPTY, "Begin Time Required");
        } else if (type.equals(getString(R.string.contact))) {
            contact.setVisibility(View.VISIBLE);
            mAwesomeValidation.addValidation(contact_full_name, RegexTemplate.NOT_EMPTY, "Name Required");
            mAwesomeValidation.addValidation(contact_phone, RegexTemplate.NOT_EMPTY, "Phone Number Required");
            mAwesomeValidation.addValidation(contact_address, RegexTemplate.NOT_EMPTY, "Adresse Required");
            mAwesomeValidation.addValidation(contact_email, android.util.Patterns.EMAIL_ADDRESS, "Invalid Email Format");
            mAwesomeValidation.addValidation(contact_email, RegexTemplate.NOT_EMPTY, "Email Required");
        } else if (type.equals(getString(R.string.telephone))) {
            telephone.setVisibility(View.VISIBLE);
            mAwesomeValidation.addValidation(telephone_number, RegexTemplate.NOT_EMPTY, "Phone Number Required");
        } else if (type.equals(getString(R.string.text))) {
            text.setVisibility(View.VISIBLE);
            mAwesomeValidation.addValidation(text_text, RegexTemplate.NOT_EMPTY, "Text Required");
        } else if (type.equals(getString(R.string.wifi))) {
            wifi.setVisibility(View.VISIBLE);
            mAwesomeValidation.addValidation(wifi_ssid, RegexTemplate.NOT_EMPTY, "Network Name Required");
            mAwesomeValidation.addValidation(wifi_password, RegexTemplate.NOT_EMPTY, "Wifi Password Required");
        } else if (type.equals(getString(R.string.url))) {
            url.setVisibility(View.VISIBLE);
            mAwesomeValidation.addValidation(url_http, RegexTemplate.NOT_EMPTY, "URL Required");
        }

        message_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_MESSAGE);
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 3);
                }

            }
        });
        telephone_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_TELEPHONE);
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 4);
                }
            }
        });
        contact_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_CONTACT);
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 5);
                }
            }
        });

        List<String> categories = new ArrayList<String>();
        categories.add("WPA/WPA2");
        categories.add("WEP");
        categories.add("None");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wifi_type.setAdapter(dataAdapter);

        SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                getString(R.string.begin_time),
                "OK",
                "Cancel"
        );

        dateTimeDialogFragment.startAtCalendarView();
        //    dateTimeDialogFragment.set24HoursMode(true);
        dateTimeDialogFragment.setDefaultDateTime(new GregorianCalendar().getTime());

        try {
            dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("dd MMMM", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            e.printStackTrace();
        }

        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
                event_begin_time.setText(format.format(date));
                event_begin = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
            }

            @Override
            public void onNegativeButtonClick(Date date) {
            }
        });

        // Initialize
        SwitchDateTimeDialogFragment end_time_fragment = SwitchDateTimeDialogFragment.newInstance(
                getString(R.string.end_time),
                "OK",
                "Cancel"
        );

        end_time_fragment.startAtCalendarView();
        // end_time_fragment.set24HoursMode(true);
        end_time_fragment.setDefaultDateTime(new GregorianCalendar().getTime());
        try {
            end_time_fragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("dd MMMM", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            e.printStackTrace();
        }
        end_time_fragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
                event_end_time.setText(format.format(date));
                event_end = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
            }

            @Override
            public void onNegativeButtonClick(Date date) {
            }
        });

        event_begin_time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    dateTimeDialogFragment.show(getFragmentManager(), "begin_dialog_time");
            }
        });
        event_end_time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    end_time_fragment.show(getFragmentManager(), "end_dialog_time");
            }
        });
        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (REQUEST_CODE_MESSAGE):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String num = "";
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                message_to.setText(num);
                            }
                        }
                    }
                    break;
                }
            case (REQUEST_CODE_TELEPHONE):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String num = "";
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                telephone_number.setText(num);
                            }
                        }
                    }
                    break;
                }
            case (REQUEST_CODE_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String num = "";
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                contact_phone.setText(num);
                            }
                        }
                        contact_full_name.setText(name);
                       /* String email = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        contact_email.setText(email);*/
                    }
                    break;
                }
        }
    }

    MenuItem share, save, generate;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.generte_code_detial, menu);
        save = menu.findItem(R.id.save);
        share = menu.findItem(R.id.share);
        generate = menu.findItem(R.id.generate);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.generate:
                if (item != null) {
                    utils.hideKeyboard(getActivity());
                    if (mAwesomeValidation.validate()) {
                        String encoded_text = null;
                        if (email.getVisibility() == View.VISIBLE) {
                            encoded_text = "MATMSG:TO:" + email_to.getText().toString() + ";SUB:" + email_subject.getText().toString() + ";BODY:" + email_message.getText().toString();
                            qrgEncoder = new QRGEncoder(
                                    encoded_text, null, QRGContents.Type.EMAIL,
                                    smallerDimension);
                            email.setVisibility(View.GONE);
                            generated_layout.setVisibility(View.VISIBLE);
                            displayed_label.setText(getString(R.string.email));
                            displayed_text.setText(email_to.getText().toString());
                        } else if (message.getVisibility() == View.VISIBLE) {
                            encoded_text = "smsto:" + message_to.getText().toString() + ":" + message_message.getText().toString();
                            qrgEncoder = new QRGEncoder(
                                    encoded_text, null,
                                    QRGContents.Type.SMS,
                                    smallerDimension);
                            message.setVisibility(View.GONE);
                            generated_layout.setVisibility(View.VISIBLE);
                            displayed_label.setText(getString(R.string.message));
                            displayed_text.setText(message_message.getText().toString());
                        } else if (location.getVisibility() == View.VISIBLE) {
                            encoded_text = "geo:" + location_latitude.getText().toString() + "," + location_longitude.getText().toString();
                            qrgEncoder = new QRGEncoder(
                                    encoded_text, null,
                                    QRGContents.Type.TEXT,
                                    smallerDimension);
                            location.setVisibility(View.GONE);
                            generated_layout.setVisibility(View.VISIBLE);
                            displayed_label.setText(getString(R.string.location));
                            displayed_text.setText(location_latitude.getText().toString() + "," + location_longitude.getText().toString());
                        } else if (event.getVisibility() == View.VISIBLE) {
                            encoded_text = "BEGIN:VEVENT" + "\nSUMMARY:" + event_title.getText().toString() + "\nDTSTART:" + event_begin + "\nDTEND:" + event_end + "\nLOCATION:" + event_location.getText().toString() + "\nDESCRIPTION:" + event_description.getText().toString() + "\nEND:VEVENT";
                            qrgEncoder = new QRGEncoder(
                                    encoded_text, null,
                                    QRGContents.Type.TEXT,
                                    smallerDimension);
                            event.setVisibility(View.GONE);
                            generated_layout.setVisibility(View.VISIBLE);
                            displayed_label.setText(getString(R.string.event));
                            displayed_text.setText(event_title.getText().toString());
                        } else if (contact.getVisibility() == View.VISIBLE) {
                            encoded_text = "MECARD:N:" + contact_full_name.getText().toString() + ";TEL:" + contact_phone.getText().toString() + ";EMAIL:" + contact_email.getText().toString() + ";ADR:" + contact_address.getText().toString();
                            qrgEncoder = new QRGEncoder(
                                    encoded_text, null,
                                    QRGContents.Type.TEXT,
                                    smallerDimension);
                            contact.setVisibility(View.GONE);
                            generated_layout.setVisibility(View.VISIBLE);
                            displayed_label.setText(getString(R.string.contact));
                            displayed_text.setText(contact_full_name.getText().toString());
                        } else if (telephone.getVisibility() == View.VISIBLE) {
                            encoded_text = "tel:" + telephone_number.getText().toString();
                            qrgEncoder = new QRGEncoder(
                                    encoded_text, null,
                                    QRGContents.Type.PHONE,
                                    smallerDimension);
                            telephone.setVisibility(View.GONE);
                            generated_layout.setVisibility(View.VISIBLE);
                            displayed_label.setText(getString(R.string.telephone));
                            displayed_text.setText(telephone_number.getText().toString());
                        } else if (text.getVisibility() == View.VISIBLE) {
                            encoded_text = text_text.getText().toString() + "";
                            qrgEncoder = new QRGEncoder(
                                    encoded_text, null,
                                    QRGContents.Type.TEXT,
                                    smallerDimension);
                            text.setVisibility(View.GONE);
                            generated_layout.setVisibility(View.VISIBLE);
                            displayed_label.setText(getString(R.string.text));
                            displayed_text.setText(text_text.getText().toString());
                        } else if (wifi.getVisibility() == View.VISIBLE) {
                            encoded_text = "WIFI:S:" + wifi_ssid.getText().toString() + ";T:" + wifi_type.getSelectedItem().toString() + ";P:" + wifi_password.getText().toString() + ";";
                            qrgEncoder = new QRGEncoder(
                                    encoded_text + "", null,
                                    QRGContents.Type.TEXT,
                                    smallerDimension);
                            wifi.setVisibility(View.GONE);
                            generated_layout.setVisibility(View.VISIBLE);
                            displayed_label.setText(getString(R.string.wifi));
                            displayed_text.setText(wifi_ssid.getText().toString());
                        } else if (url.getVisibility() == View.VISIBLE) {
                            if (url_http.getText().toString().contains("https://"))
                                encoded_text = url_http.getText().toString() + "";
                            else encoded_text = "https://" + url_http.getText().toString() + "";
                            qrgEncoder = new QRGEncoder(
                                    encoded_text, null,
                                    QRGContents.Type.TEXT,
                                    smallerDimension);
                            url.setVisibility(View.GONE);
                            generated_layout.setVisibility(View.VISIBLE);
                            displayed_label.setText(getString(R.string.url));
                            displayed_text.setText(url_http.getText().toString());
                        }

                        Date calendar = Calendar.getInstance().getTime();
                        SimpleDateFormat time = new SimpleDateFormat("dd/MMM/yyyy");
                        SimpleDateFormat date = new SimpleDateFormat("hh:mm a");

                        ScanItemCreated ItemCreated = new ScanItemCreated();
                        ItemCreated.setFavourite(false);
                        ItemCreated.setType("QR_CODE");
                        ItemCreated.setScanned_item(encoded_text);
                        ItemCreated.setDate(calendar);
                        ItemCreated.setDay(date.format(calendar));
                        ItemCreated.setTime(time.format(calendar));


                        if (mDatabaseHelper == null)
                            mDatabaseHelper = new DatabaseHelper(getActivity());

                        mDatabaseHelper.AddOrUpdateScanItemCreated(ItemCreated);

                        qrgEncoder.setColorBlack(Color.BLACK);
                        qrgEncoder.setColorWhite(Color.WHITE);
                        bitmap = qrgEncoder.getBitmap();
                        view.setImageBitmap(bitmap);
                        save.setVisible(true);
                        share.setVisible(true);
                        generate.setVisible(false);
                    }
                }
                break;
            case R.id.save:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        boolean save = new QRGSaver().save(savePath, new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(Calendar.getInstance().getTime()), bitmap, QRGContents.ImageType.IMAGE_JPEG);
                        String result = save ? "Image Saved" : "Image Not Saved";
                        scanGallery(getContext(), savePath);
                        Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, code_type, code_type + "generated from Bar Code Scanner");

                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
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
        }
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean save = new QRGSaver().save(savePath, new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(Calendar.getInstance().getTime()), bitmap, QRGContents.ImageType.IMAGE_JPEG);
                    String result = save ? "Image Saved" : "Image Not Saved";
                    scanGallery(getContext(), savePath);
                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.camera_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String bitmapPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Saved_BarCodeScanner", null);
                    Uri bitmapUri = Uri.parse(bitmapPath);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                    startActivity(Intent.createChooser(intent, "Share"));
                } else {
                    Toast.makeText(getActivity(), getString(R.string.camera_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intentt = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intentt, REQUEST_CODE_MESSAGE);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.camera_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intenttt = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intenttt, REQUEST_CODE_TELEPHONE);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.camera_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            case 5:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intentttt = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intentttt, REQUEST_CODE_CONTACT);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.camera_permission), Toast.LENGTH_SHORT).show();
                }
                break;
        }
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

    public void PushFragment(Fragment fragment, String string) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment, string);
        ft.commit();
    }

}

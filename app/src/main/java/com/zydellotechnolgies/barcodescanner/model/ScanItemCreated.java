package com.zydellotechnolgies.barcodescanner.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

import static com.zydellotechnolgies.barcodescanner.utils.TabelDetails.TABLE_SCAN_ITEMS;
import static com.zydellotechnolgies.barcodescanner.utils.TabelDetails.TABLE_SCAN_ITEMS_CREATED;
import static com.zydellotechnolgies.barcodescanner.utils.TabelDetails.TABLE_SCAN_ITEMS_DATE;
import static com.zydellotechnolgies.barcodescanner.utils.TabelDetails.TABLE_SCAN_ITEMS_DAY;
import static com.zydellotechnolgies.barcodescanner.utils.TabelDetails.TABLE_SCAN_ITEMS_FAVOURITE;
import static com.zydellotechnolgies.barcodescanner.utils.TabelDetails.TABLE_SCAN_ITEMS_ID;
import static com.zydellotechnolgies.barcodescanner.utils.TabelDetails.TABLE_SCAN_ITEMS_SCANNED_TEXT;
import static com.zydellotechnolgies.barcodescanner.utils.TabelDetails.TABLE_SCAN_ITEMS_TIME;
import static com.zydellotechnolgies.barcodescanner.utils.TabelDetails.TABLE_SCAN_ITEMS_TYPE;

@DatabaseTable(tableName = TABLE_SCAN_ITEMS_CREATED)
public class ScanItemCreated implements Serializable {
    @DatabaseField(generatedId = true, columnName = TABLE_SCAN_ITEMS_ID, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField(columnName = TABLE_SCAN_ITEMS_DAY)
    private String day;
    @DatabaseField(columnName = TABLE_SCAN_ITEMS_TIME)
    private String time;
    @DatabaseField(columnName = TABLE_SCAN_ITEMS_SCANNED_TEXT)
    private String scanned_item;
    @DatabaseField(columnName = TABLE_SCAN_ITEMS_TYPE)
    private String type;
    @DatabaseField(columnName = TABLE_SCAN_ITEMS_DATE)
    private Date date;
    @DatabaseField(columnName = TABLE_SCAN_ITEMS_FAVOURITE)
    boolean favourite;
    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getId() {
        return id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getScanned_item() {
        return scanned_item;
    }

    public void setScanned_item(String scanned_item) {
        this.scanned_item = scanned_item;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}

package com.zydellotechnolgies.barcodescanner.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zydellotechnolgies.barcodescanner.model.ScanItem;
import com.zydellotechnolgies.barcodescanner.model.ScanItemCreated;

import java.sql.SQLException;
import java.util.ArrayList;

import static com.zydellotechnolgies.barcodescanner.utils.TabelDetails.TABLE_SCAN_ITEMS_FAVOURITE;
import static com.zydellotechnolgies.barcodescanner.utils.TabelDetails.TABLE_SCAN_ITEMS_ID;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "BarCodeScanner_ZydelloTechnologies.db";
    private static final int DATABASE_VERSION = 2;

    private Dao<ScanItem, String> ScanItemDao = null;
    private Dao<ScanItemCreated, String> ScanItemCreatedDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, ScanItem.class);
            TableUtils.createTable(connectionSource, ScanItemCreated.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, ScanItem.class, true);
            TableUtils.dropTable(connectionSource, ScanItemCreated.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    //Scan Item
    public Dao<ScanItem, String> getScanItemsDao() throws SQLException {
        if (ScanItemDao == null) {
            ScanItemDao = getDao(ScanItem.class);
        }
        return ScanItemDao;
    }

    public void AddOrUpdateScanItem(ScanItem item) {
        try {
            getScanItemsDao().createOrUpdate(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ScanItem getScanItemId(int id) {
        try {
            QueryBuilder<ScanItem, String> query = getScanItemsDao().queryBuilder();

            return query.where().eq(TABLE_SCAN_ITEMS_ID, id).queryForFirst();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<ScanItem> getScanItems() {
        try {

            return (ArrayList<ScanItem>) getScanItemsDao().queryBuilder().query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<ScanItem> getFavouriteScanItems(boolean fav) {
        try {
            return (ArrayList<ScanItem>) getScanItemsDao().queryBuilder().where().eq(TABLE_SCAN_ITEMS_FAVOURITE, fav).query();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void delete_list(ArrayList<ScanItem> List) {
        try {
            if (List.size() > 0)
                for (int i = 0; i < List.size(); i++) {
                    getScanItemsDao().delete(List.get(i));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Scan Item Created
    public Dao<ScanItemCreated, String> getScanItemsCreatedDao() throws SQLException {
        if (ScanItemCreatedDao == null) {
            ScanItemCreatedDao = getDao(ScanItemCreated.class);
        }
        return ScanItemCreatedDao;
    }

    public void AddOrUpdateScanItemCreated(ScanItemCreated item) {
        try {
            getScanItemsCreatedDao().createOrUpdate(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ScanItemCreated getScanItemCreatedId(int id) {
        try {
            QueryBuilder<ScanItemCreated, String> query = getScanItemsCreatedDao().queryBuilder();

            return query.where().eq(TABLE_SCAN_ITEMS_ID, id).queryForFirst();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<ScanItemCreated> getScanItemsCreated() {
        try {

            return (ArrayList<ScanItemCreated>) getScanItemsCreatedDao().queryBuilder().query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public ArrayList<ScanItemCreated> getFavouriteScanItemsCreated(boolean fav) {
        try {
            return (ArrayList<ScanItemCreated>) getScanItemsCreatedDao().queryBuilder().where().eq(TABLE_SCAN_ITEMS_FAVOURITE, fav).query();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void delete() {
        try {
            if (getScanItems().size() > 0)
                getScanItemsDao().deleteBuilder().delete();
            if (getScanItemsCreated().size() > 0)
                getScanItemsCreatedDao().deleteBuilder().delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        super.close();
        ScanItemDao = null;
        ScanItemCreatedDao = null;
    }

}

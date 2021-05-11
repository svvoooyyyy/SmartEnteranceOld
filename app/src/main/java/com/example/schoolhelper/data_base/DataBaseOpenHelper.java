package com.example.schoolhelper.data_base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataBaseOpenHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 2;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public DataBaseOpenHelper(Context context) {
        super(context, Contract.DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, 1);// единица только для проверок какие обновления выполнять
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db, oldVersion, newVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {//то приложение либо новое, либо удаляются данные
            //чистка бд
            db.execSQL("PRAGMA foreign_keys = OFF;");
            db.execSQL("DROP TABLE IF EXISTS " + Contract.TableAuthorizedLearners.NAME_TABLE_LEARNERS + ";");
            db.execSQL("DROP TABLE IF EXISTS " + Contract.TableEnteredLearners.NAME_TABLE_LEARNERS + ";");
            db.execSQL("PRAGMA foreign_keys = ON;");

            //ученики
            String sql = "CREATE TABLE " + Contract.TableAuthorizedLearners.NAME_TABLE_LEARNERS + "( " + Contract.TableAuthorizedLearners.KEY_STUDENT_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Contract.TableAuthorizedLearners.COLUMN_MOSKVENOK_ID + " INTEGER NOT NULL, " +
                    Contract.TableAuthorizedLearners.COLUMN_NAME + " VARCHAR, " +
                    Contract.TableAuthorizedLearners.COLUMN_FORM + " VARCHAR);";
            db.execSQL(sql);
//            {// ---- вставляем одну запись настроек ----
//                ContentValues values = new ContentValues();
//                values.put(SchoolContract.TableSettingsData.COLUMN_PROFILE_NAME, "simpleName");
//                values.put(SchoolContract.TableSettingsData.COLUMN_INTERFACE_SIZE, 1);
//                db.insert(SchoolContract.TableSettingsData.NAME_TABLE_SETTINGS, null, values);
//            }
        }

        if (oldVersion < 2) {

            // создание таблицы вошедших учеников
            String sql = "CREATE TABLE " + Contract.TableEnteredLearners.NAME_TABLE_LEARNERS + "( " +
                    Contract.TableEnteredLearners.KEY_STUDENT_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Contract.TableEnteredLearners.KEY_AUTHORIZED_PROFILE_ID + " INTEGER, " +
                    Contract.TableEnteredLearners.COLUMN_ENTERED_MOSKVENOK_ID + " INTEGER, " +
                    Contract.TableEnteredLearners.COLUMN_ENTER_DATE + " DATE, " +
                    Contract.TableEnteredLearners.COLUMN_ENTER_TIME + " VARCHAR, " +
                    "FOREIGN KEY(" + Contract.TableEnteredLearners.KEY_AUTHORIZED_PROFILE_ID + ") REFERENCES " +
                    Contract.TableAuthorizedLearners.NAME_TABLE_LEARNERS + " (" + Contract.TableAuthorizedLearners.KEY_STUDENT_PROFILE_ID + ") ON DELETE CASCADE);";
            db.execSQL(sql);
        }
    }


    // Authorized ---------------------------------------

    public Cursor getAuthorizedStudents() {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.query(Contract.TableAuthorizedLearners.NAME_TABLE_LEARNERS, null, null,
                null, null, null, null);
    }

    public Cursor getAuthorizedStudentById(long id) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.query(Contract.TableAuthorizedLearners.NAME_TABLE_LEARNERS, null,
                Contract.TableAuthorizedLearners.KEY_STUDENT_PROFILE_ID + " = " + id,
                null, null, null, null);
    }

    public Cursor getAuthorizedStudentByMoskvenokId(long moskvenokId) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.query(Contract.TableAuthorizedLearners.NAME_TABLE_LEARNERS, null,
                Contract.TableAuthorizedLearners.COLUMN_MOSKVENOK_ID + " = " + moskvenokId,
                null, null, null, null);
    }

    public long createAuthorizedStudent(long moskvenokId, String name, String form) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.TableAuthorizedLearners.COLUMN_MOSKVENOK_ID, moskvenokId);
        values.put(Contract.TableAuthorizedLearners.COLUMN_NAME, name);
        values.put(Contract.TableAuthorizedLearners.COLUMN_FORM, form);
        return db.insert(Contract.TableAuthorizedLearners.NAME_TABLE_LEARNERS, null, values);
    }

    public void clearAuthorizedStudentsList() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys = OFF;");
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TableAuthorizedLearners.NAME_TABLE_LEARNERS + ";");
        String sql = "CREATE TABLE " + Contract.TableAuthorizedLearners.NAME_TABLE_LEARNERS + "( " + Contract.TableAuthorizedLearners.KEY_STUDENT_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.TableAuthorizedLearners.COLUMN_MOSKVENOK_ID + " INTEGER NOT NULL, " +
                Contract.TableAuthorizedLearners.COLUMN_NAME + " VARCHAR, " +
                Contract.TableAuthorizedLearners.COLUMN_FORM + " VARCHAR);";
        db.execSQL(sql);
        db.execSQL("PRAGMA foreign_keys = ON;");

        clearEnteredStudentsList();
    }


    // Entered ---------------------------------------

    public long createEnteredStudent(long autorizedId, long enteredMoskvenokId, Date date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (autorizedId != -1) {
            values.put(Contract.TableEnteredLearners.KEY_AUTHORIZED_PROFILE_ID, autorizedId);
        }
        values.put(Contract.TableEnteredLearners.COLUMN_ENTERED_MOSKVENOK_ID, enteredMoskvenokId);
        values.put(Contract.TableEnteredLearners.COLUMN_ENTER_DATE, dateFormat.format(date));
        values.put(Contract.TableEnteredLearners.COLUMN_ENTER_TIME, timeFormat.format(date));
        return db.insert(Contract.TableEnteredLearners.NAME_TABLE_LEARNERS, null, values);
    }

    public Cursor getEnteredStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " +
                Contract.TableEnteredLearners.NAME_TABLE_LEARNERS +
                " ORDER BY " +
                Contract.TableEnteredLearners.COLUMN_ENTER_DATE + " ASC, " +
                Contract.TableEnteredLearners.COLUMN_ENTER_TIME + " ASC;", null);
    }

    public long deleteEnteredStudentByRecordId(long recordId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Contract.TableEnteredLearners.NAME_TABLE_LEARNERS,
                Contract.TableEnteredLearners.KEY_STUDENT_PROFILE_ID + " = " + recordId,
                null
        );
    }

    public void clearEnteredStudentsList() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys = OFF;");
        db.execSQL("DROP TABLE IF EXISTS " + Contract.TableEnteredLearners.NAME_TABLE_LEARNERS + ";");
        // создание таблицы вошедших учеников
        String sql = "CREATE TABLE " + Contract.TableEnteredLearners.NAME_TABLE_LEARNERS + "( " +
                Contract.TableEnteredLearners.KEY_STUDENT_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.TableEnteredLearners.KEY_AUTHORIZED_PROFILE_ID + " INTEGER, " +
                Contract.TableEnteredLearners.COLUMN_ENTERED_MOSKVENOK_ID + " INTEGER, " +
                Contract.TableEnteredLearners.COLUMN_ENTER_DATE + " DATE, " +
                Contract.TableEnteredLearners.COLUMN_ENTER_TIME + " VARCHAR, " +
                "FOREIGN KEY(" + Contract.TableEnteredLearners.KEY_AUTHORIZED_PROFILE_ID + ") REFERENCES " +
                Contract.TableAuthorizedLearners.NAME_TABLE_LEARNERS + " (" + Contract.TableAuthorizedLearners.KEY_STUDENT_PROFILE_ID + ") ON DELETE CASCADE);";
        db.execSQL(sql);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }
}
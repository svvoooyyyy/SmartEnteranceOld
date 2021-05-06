package com.example.schoolhelper.data_base;

import android.provider.BaseColumns;

public class Contract {

    public static final String DB_NAME = "school";
    public static final String DECODING_TIMES_STRING = "yyyy-MM-dd HH:mm:ss";
    public static final class TableLearners{
        public static final String NAME_TABLE_LEARNERS = "learnersData";
        public static final String KEY_SETTINGS_PROFILE_ID = BaseColumns._ID;
        public static final String COLUMN_MOSKVENOK_ID = "moskvenokId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_FORM = "form";

    }

}

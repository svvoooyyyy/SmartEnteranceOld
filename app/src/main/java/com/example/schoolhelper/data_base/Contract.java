package com.example.schoolhelper.data_base;

import android.provider.BaseColumns;

public class Contract {

    public static final String DB_NAME = "school";
    public static final String DECODING_TIMES_STRING = "yyyy-MM-dd HH:mm:ss";
    public static final class TableAuthorizedLearners {
        public static final String NAME_TABLE_LEARNERS = "learnersData";
        public static final String KEY_STUDENT_PROFILE_ID = BaseColumns._ID;
        public static final String COLUMN_MOSKVENOK_ID = "moskvenokId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_FORM = "form";
    }

    public static final class TableEnteredLearners {
        public static final String NAME_TABLE_LEARNERS = "enteredLearners";
        public static final String KEY_STUDENT_PROFILE_ID = BaseColumns._ID;
        public static final String KEY_AUTHORIZED_PROFILE_ID = "studentProfile";
        public static final String COLUMN_ENTERED_MOSKVENOK_ID = "enteredMoskvenokId";
        public static final String COLUMN_ENTER_DATE = "enterDate";
        public static final String COLUMN_ENTER_TIME = "enterTime";
    }

}


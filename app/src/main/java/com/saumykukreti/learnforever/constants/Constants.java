package com.saumykukreti.learnforever.constants;

import java.util.ArrayList;

/**
 * Created by saumy on 12/16/2017.
 */

public class Constants {
    public static final String PAUSE_FOR_TWO_HUNDERED_MILISECONDS = "&%&22&&&&&&&&&&&";
    public static final String PAUSE_FOR_THREE_HUNDERED_MILISECONDS = "&%&33&&&&&&&&&&&";
    public static final String PAUSE_FOR_FOUR_HUNDERED_MILISECONDS = "&%&44&&&&&&&&&&&";
    public static final String PAUSE_FOR_FIVE_HUNDERED_MILISECONDS = "&%&55&&&&&&&&&&&";
    public static final String PAUSE_FOR_SIX_HUNDERED_MILISECONDS = "&%&66&&&&&&&&&&&";

    public static final String LEARN_FOREVER_PREFERENCE = "lf_preference";

    public static final String LEARN_FOREVER_PREFERENCE_SYNC_PENDING_NOTE_IDS = "lf_preference_list_of_note_ids_pending_sync";
    public static final String LEARN_FOREVER_PREFERENCE_SYNC_PENDING_NOTE_IDS_TO_DELETE = "lf_preference_delete_list_of_note_ids_pending_sync";
    public static final String LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST = "lf_preference_saved_notes_list";
    public static final String LEARN_FOREVER_PREFERENCE_IS_ALARM_SET = "lf_preference_is_alarm_set";
    public static final String LEARN_FOREVER_PREFERENCE_IS_SPEECH_ON = "lf_preference_is_speech_on";
    public static final String LEARN_FOREVER_PREFERENCE_SIGN_IN_METHOD = "lf_preference_sign_in_method";
    public static final String LEARN_FOREVER_PREFERENCE_USER_ID = "lf_preference_user_id";

    //Preferences
    public static final String LEARN_FOREVER_PREFERENCE_TITLE_SETTINGS = "lf_preference_title";
    public static final String LEARN_FOREVER_PREFERENCE_CIS_SETTINGS = "lf_preference_cis";
    public static final String LEARN_FOREVER_PREFERENCE_CATEGORY_SETTINGS = "lf_preference_category";

    //Sign in methods
    public static final int SIGN_IN_METHOD_GOOGLE_SIGN_IN = 1010;
    public static final int SIGN_IN_METHOD_FIREBASE_SIGN_IN = 1011;

    public static final int[] DAY_INTERVAL_ONE = {1,3,7,14,29, 59, 89, 129,219, 309, 399};
    public static final int NOTIFICATION_ALARM_REQUEST_CODE = 1000;
    public static final int NOTIFICATION_REQUEST_CODE = 1001;
}

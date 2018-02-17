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
    public static final String LEARN_FOREVER_PREFERENCE_SPEECH_RATE = "lf_preference_speech_rate";
    public static final String LEARN_FOREVER_PREFERENCE_SIGN_IN_METHOD = "lf_preference_sign_in_method";
    public static final String LEARN_FOREVER_PREFERENCE_USER_ID = "lf_preference_user_id";
    public static final String LEARN_FOREVER_PREFERENCE_CURRENT_INTERVAL = "lf_preference_current_interval";
    public static final String LEARN_FOREVER_PREFERENCE_CUSTOM_INTERVAL = "lf_preference_custom_interval";
    public static final String LEARN_FOREVER_PREFERENCE_LAYOUT_PREFERENCE = "lf_preference_layout_style";
    public static final String LEARN_FOREVER_PREFERENCE_SPEECH_IN_BACKGROUND_PREFERENCE = "lf_preference_speech_in_background";
    public static final String LEARN_FOREVER_PREFERENCE_DEFAULT_FILTER = "lf_preference_default_filter";

    //Preferences
    public static final String LEARN_FOREVER_PREFERENCE_TITLE_SETTINGS = "lf_preference_title";
    public static final String LEARN_FOREVER_PREFERENCE_CIS_SETTINGS = "lf_preference_cis";
    public static final String LEARN_FOREVER_PREFERENCE_CATEGORY_SETTINGS = "lf_preference_category";

    public static final String LEARN_FOREVER_PREFERENCE_FILTER_SETTING_ALPHABETICALLY = "lf_preference_alphabetically";
    public static final String LEARN_FOREVER_PREFERENCE_FILTER_SETTING_OLD_FIRST = "lf_preference_old_first";
    public static final String LEARN_FOREVER_PREFERENCE_FILTER_SETTING_NEW_FIRST = "lf_preference_new_first";

    //Notification time
    public static final String LEARN_FOREVER_PREFERENCE_NOTIFICATION_HOUR = "lf_preference_hour";
    public static final String LEARN_FOREVER_PREFERENCE_NOTIFICATION_MINUTE = "lf_preference_minute";

    //Revise date
    public static final String LEARN_FOREVER_PREFERENCE_LAST_REVISE_DATE = "lf_preference_last_revise_date";

    //Sign in methods
    public static final int SIGN_IN_METHOD_GOOGLE_SIGN_IN = 1010;
    public static final int SIGN_IN_METHOD_FIREBASE_SIGN_IN = 1011;

    public static final int[] DAY_INTERVAL_ONE = {1,3,7,14,29, 59, 89, 129,219, 309, 399};
    public static final int[] DAY_INTERVAL_TWO = {1,2,3,5,7,11,15,23,31,47,63,95,127,191,255,383};
    public static final int[] DAY_INTERVAL_THREE = {1,2,4,7,11,16,22,29,37,46,56,67,79,92,106,121};


    public static final int NOTIFICATION_ALARM_REQUEST_CODE = 1000;
    public static final int NOTIFICATION_REQUEST_CODE = 1001;
}

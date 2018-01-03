package com.saumykukreti.learnforever.constants;

import java.util.ArrayList;

/**
 * Created by saumy on 12/16/2017.
 */

public class Constants {
    public static final String PAUSE_LINE_SEPARATOR = "&&&&&&&&&&&&&";

    public static final String LEARN_FOREVER_PREFERENCE = "lf_preference";

    public static final String LEARN_FOREVER_PREFERENCE_SYNC_PENDING_NOTE_IDS = "lf_preference_list_of_note_ids_pending_sync";
    public static final String LEARN_FOREVER_PREFERENCE_SYNC_PENDING_NOTE_IDS_TO_DELETE = "lf_preference_delete_list_of_note_ids_pending_sync";
    public static final String LEARN_FOREVER_PREFERENCE_SAVED_NOTES_LIST = "lf_preference_saved_notes_list";
    public static final String LEARN_FOREVER_PREFERENCE_IS_ALARM_SET = "lf_preference_is_alarm_set";
    public static final String LEARN_FOREVER_PREFERENCE_IS_SPEECH_ON = "lf_preference_is_speech_on";

    public static final int[] DAY_INTERVAL_ONE = {1,3,7,14,29, 59, 89, 129,219, 309, 399};
    public static final int NOTIFICATION_ALARM_REQUEST_CODE = 1000;
    public static final int NOTIFICATION_REQUEST_CODE = 1001;
}

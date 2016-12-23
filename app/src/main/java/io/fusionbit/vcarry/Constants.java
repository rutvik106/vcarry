package io.fusionbit.vcarry;

import java.util.HashMap;

/**
 * Created by rutvik on 11/16/2016 at 2:38 PM.
 */

public class Constants
{

    public static final String API_BASE_URL = "http://tapandtype.com/vcarry/webservice/"; //"http://192.168.0.104/vcarry/webservice/"; //"http://tapandtype.com/vcarry/webservice/";

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final int NOTIFICATION_ID = 246;

    public static final int CHANGE_LANGUAGE = 123;

    public static final String TRANSPORT_REQUEST_RESPONSE = "TRANSPORT_REQUEST_RESPONSE";

    public static final String T_RESPONSE = "T_RESPONSE";

    public static final String ACCEPT = "ACCEPT";

    public static final String REJECT = "REJECT";

    public static final String TRANSPORT_REQUEST_ID = "TRANSPORT_REQUEST_ID";

    public static final String WAS_LANGUAGE_CHANGED = "WAS_LANGUAGE_CHANGED";

    public static final String INTENT_EXTRA_TRIP_ID = "TRIP_ID";

    public static final String INTENT_EXTRA_TIME = "INTENT_EXTRA_TIME";
    public static final String INTENT_EXTRA_FROM = "INTENT_EXTRA_FROM";
    public static final String INTENT_EXTRA_TO = "INTENT_EXTRA_TO";

    public static final String TRIP_STATUS_NEW = "1";
    public static final String TRIP_STATUS_DRIVER_ALLOCATED = "2";
    public static final String TRIP_STATUS_LOADING = "3";
    public static final String TRIP_STATUS_TRIP_STARTED = "4";
    public static final String TRIP_STATUS_UNLOADING = "5";
    public static final String TRIP_STATUS_FINISHED = "6";
    public static final String TRIP_STATUS_CANCELLED = "7";

    public static final String IS_DRIVER_ON_TRIP = "IS_DRIVER_ON_TRIP";

    public static final String CURRENT_TRIP_ID = "CURRENT_TRIP_ID";

    public static final String SERVICE_RESULT_RECEIVER = "SERVICE_RESULT_RECEIVER";

    public static final int ON_TRIP_STOPPED = 3333;

    public static class FirebaseNames
    {
        public static final String NODE_ACCEPTED = "accepted";
        public static final String KEY_CONFIRM = "confirm";
    }

    public static final String IS_BILL_PENDING = "IS_BILL_PENDING";

    public static HashMap<String, Object> getFirebaseRemoteValuesMap()
    {

        return new HashMap<String, Object>()
        {{

            put("urgent_notice_english", "");
            put("urgent_notice_gujarati", "");
            put("show_urgent_notice", false);
            put("force_update", false);
            put("version_code", 1);
            put("update_message_english", "");
            put("update_message_gujarati", "");
            put("urgent_notice_title_english", "");
            put("urgent_notice_title_gujarati", "");

        }};

    }

    static class Broadcast
    {

        public static final String UPCOMING_TRIP_NOTIFICATION =
                App.APP_TAG + Broadcast.class.getSimpleName() + "UPCOMING_TRIP_NOTIFICATION";

    }

}

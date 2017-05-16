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

    public static final String REQUEST_ID = "REQUEST_ID";

    public static final String ACCEPT = "ACCEPT";

    public static final String REJECT = "REJECT";

    public static final String TRANSPORT_REQUEST_ID = "TRANSPORT_REQUEST_ID";

    public static final String WAS_LANGUAGE_CHANGED = "WAS_LANGUAGE_CHANGED";
    public static final String WAS_REALM_DATABASE_CLEARED = "WAS_REALM_DATABASE_CLEARED";

    public static final String INTENT_EXTRA_TRIP_ID = "TRIP_ID";

    public static final String INTENT_EXTRA_TIME = "INTENT_EXTRA_TIME";
    public static final String INTENT_EXTRA_FROM = "INTENT_EXTRA_FROM";
    public static final String INTENT_EXTRA_TO = "INTENT_EXTRA_TO";

    public static final String TRIP_STATUS_PENDING = "0";
    public static final String TRIP_STATUS_NEW = "1";
    public static final String TRIP_STATUS_DRIVER_ALLOCATED = "2";
    public static final String TRIP_STATUS_LOADING = "3";
    public static final String TRIP_STATUS_TRIP_STARTED = "4";
    public static final String TRIP_STATUS_UNLOADING = "5";
    public static final String TRIP_STATUS_FINISHED = "6";
    public static final String TRIP_STATUS_CANCELLED_BY_DRIVER = "7";
    public static final String TRIP_STATUS_CANCELLED_BY_CUSTOMER = "8";
    public static final String TRIP_STATUS_CANCELLED_BY_VCARRY = "9";

    public static final String IS_DRIVER_ON_TRIP = "IS_DRIVER_ON_TRIP";

    public static final String CURRENT_TRIP_ID = "CURRENT_TRIP_ID";

    public static final String SERVICE_RESULT_RECEIVER = "SERVICE_RESULT_RECEIVER";

    public static final int ON_TRIP_STOPPED = 3333;

    public static final int ON_TRIP_CANCELED = 4444;

    public static final String REALM_DATABASE_NAME = "vcarryrealmdb";

    public static final String NEW_TRIP_REQUEST = "NEW_TRIP_REQUEST";

    public static final String ACCOUNT_TRIP_TYPE = "ACCOUNT_TRIP_TYPE";

    public static final String PARCELABLE_TRIP_LIST = "PARCELABLE_TRIP_LIST";

    public static final String FCM_DRIVER_INSTANCE_ID = "a0f7161f2d14ba177ed72b00017d1ddd";
    public static final String IS_BILL_PENDING = "IS_BILL_PENDING";
    public static final String DRIVER_ID = "DRIVER_ID";
    public static final String INTENT_EXTRA_TRIP_NUMBER = "INTENT_EXTRA_TRIP_NUMBER";

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

    public static class FirebaseNames
    {
        public static final String NODE_ACCEPTED = "accepted";
        public static final String KEY_CONFIRM = "confirm";
    }

    static class Broadcast
    {

        public static final String UPCOMING_TRIP_NOTIFICATION =
                App.APP_TAG + Broadcast.class.getSimpleName() + "UPCOMING_TRIP_NOTIFICATION";

    }

    public static final class NotificationType
    {
        public static final String SIMPLE = "simple";
        public static final String GET_DRIVER_LOCATION = "get_driver_location";
        public static final String DRIVER_CURRENT_LOCATION = "driver_current_location";
        public static final String DRIVER_ALLOCATED = "driver_allocated";
        public static final String DRIVER_UNALLOCATED = "driver_unallocated";
        public static final String SEND_LOCATION_SERVER = "send_location_server";
    }

    public static class PrimaryKey
    {
        public static final int FOR_TRIP_SUMMARY = 1;
        public static final int FOR_ACCOUNT_SUMMARY = 11;
    }

    public static class AccountTripType
    {
        public static final String TODAY = "TODAY";
        public static final String THIS_MONTH = "THIS_MONTH";
        public static final String TOTAL = "TOTAL";
    }

}

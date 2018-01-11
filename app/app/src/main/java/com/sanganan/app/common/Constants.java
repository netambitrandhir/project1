package com.sanganan.app.common;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by root on 1/9/16.
 */

public class Constants {

    public static String AWSLINK = "https://s3.amazonaws.com/pyntandroid/profilepic/";
    public static final String COGNITO_POOL_ID = "us-east-1:5fa31bf6-62e5-4a56-91d7-f6032bf2d9ca";
    public static final String BUCKET_NAME = "pyntandroid";
    public static final String BUCKET_NAME_GALLERY = "/gallery";
    public static String AWSBUSINESSLINK = "https://s3.amazonaws.com/pyntandroid/";
    public static String preference = "SHARED_PREF";
    public static String linkOne = "";

    //user specific data
    public static String isLoggedIn = "isLoggedIn";
    public static String filterToCategoryById = "";
    public static String filterToCategoryNotification = "";
    public static String id = "userid";
    public static String Phone = "Phone";
    public static String email = "email";
    public static String Password = "Password";
    public static String FirstName = "firstName";
    public static String MiddleName = "MiddleName";
    public static String LastName = "LastName";
    public static String ProfilePic = "ProfilePic";
    public static String Gender = "Gender";
    public static String Occupation = "Occupation";
    public static String AddedOn = "AddedOn";
    public static String isActive = "isActive";
    public static String approvalStatus = "ApprovalStatus";


   // final public static String BaseUrl = "http://52.91.49.255/nukkad_dev_api/nukadmanager.php/nukad/";//development
    final public static String BaseUrl = "http://52.91.49.255/Nukkad/nukadmanager.php/nukad/";//production
    //final public static String MongoBaseUrl = "http://52.91.49.255/phpmongodb_dev_api/";//development mongo
    final public static String MongoBaseUrl = "http://52.91.49.255/phpmongodb_prod_api/";//production mongo

    final public static String HELPER_LINK = BaseUrl + "showfavhelper";
    final public static String NEARBY_LINK = BaseUrl + "showfavshop";
    public static String userRwa = "userRwaId";
    public static String userRwaName = "userRwaName";
    public static String ResidentRWAID = "ResidentRWAID";
    public static String flatId = "flatId";
    public static String isPhonePublic = "isPhonePublic";
    public static String flatNumber = "flatNumber";
    public static String aboutMe = "aboutMe";
    public static String isCommonAreaComplain = "1";
    public static String statusComplain = "0";

    public static JSONObject responseForward;
    public static String refreshCount = "refresh";
    public static String fromWhere = "Blank";
    public static String VehicleList = "list_of_vehicle";
    public static String pollLastSeenTime = "pollLastSeenTime";
    public static String classifiedLastSeenTime = "classifiedLastSeenTime";
    public static String complaintLastSeenTime = "complaintLastSeenTime";
    public static String notificationLastSeenTime = "notificationLastSeenTime";
    public static String galleryLastSeenTime = "galleryLastSeenTime";
    public static String calloutLastSeenTime = "calloutLastSeenTime";
    public static String neighborsLastSeenTime = "neighborsLastSeenTime";
    public static String helperLastSeenTime = "helperLastSeenTime";
    public static String pollCount = "pollCount";
    public static String classifiedCount = "classifiedCount";
    public static String complaintCount = "complaintCount";
    public static String notificationCount = "notificationCount";
    public static String galleryCount = "galleryCount";
    public static String calloutCount = "calloutCount";
    public static String neighborsCount = "neighborsCount";
    public static String helperCount = "helperCount";
    public static String SocietyPrivateinfo = "SocietyPrivateinfo";
    public static String SocietyPublicInfo = "SocietyPublicInfo";
    public static String SocietyBanner = "SocietyBanner";
    public static String SocietyPendingInfo = "SocietyPendingInfo";
    public static String body = "idBody";
    public static String LONGITUDE = "longitude";
    public static String LATITUDE = "latitude";

    public static HashSet<String> rolesGivenToUser = new HashSet<>();

    public static String imageUrlStartingTag = "https://s3-ap-southeast-1.amazonaws.com/pyntandroid/";

    public static boolean isAnyHelperEditedorAdded = false;
    public static boolean isAnyCallOutCreatedOrRemoved = false;
    public static boolean isAnyPollCreatedOrRemoved = false;
    public static boolean isAnyClassifiedCreatedOrRemoved = false;
    public static boolean isAnyPicAddedOrRemoved = false;
    public static boolean isComplainStatusChangedOrEdited = false;
    public static boolean isAnyNewPostAdded = false;
    public static boolean isAnyNewPostAddedFroMPostAdd = false;
    public static int positionListEdited = 0;


    //Codes used while activity result and response handling
    public static final int RESULTCODE_DONE = 78780;
    public static final int RESULTCODE_DELETE = 78781;

    public static final int SOCKET_TIMEOUT_MS = 250000;

    public static boolean isCommentAdded = false;
}

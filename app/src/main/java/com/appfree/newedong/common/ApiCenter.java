package com.appfree.newedong.common;

public class ApiCenter {

    private static final String URL_edong = "http://www.abcdongvn.com";
    private static final String PREFIX_edong = "/servlet/app/NewAppAc?function=";
    private static final String REQUEST_SMS_BACKEND_edong = "NewappOp";
    public static final String LOGIN_WITH_OTP_edong = "NewLgin";
    public static final String GET_INFO_edong = "NewAppH";
    public static final String GET_BANK_edong = "NewBankRz";
    public static final String GET_VERIFY_ID_edong = "NewYhRz";
    public static final String GET_ALL_CONTACT_edong = "NewTonXlAdd";
    public static final String GET_TWO_CONTACT_edong = "NewLianxi";
    public static final String GET_LOAN_edong = "NewTjJKXx";
    public static final String GET_UPLOAD_VIDEO_edong = "NewAddVideo";
    public static final String GET_UPLOAD_REPAY_BILL_edong = "NewHkQdHk";
    public static final String GET_UPLOAD_LOCATION_edong = "NewDwipYh";
    public static final String GET_REPAY_INFO_edong = "NewHkBankXx";
    public static final String GET_FB_NAME_edong = "NewFacebook";
    public static final String GET_ORDER_ID_edong = "SaveOrderId";
    public static final String URL_FUNPAY_edong = "https://payment.funmobi.asia/fun/payment/offlinePay/fun/payment/offlinePay";


    private static ApiCenter INSTANCE = null;

    // other instance variables can be here

    public static ApiCenter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApiCenter();
        }
        return(INSTANCE);
    }
    public ApiCenter() {

    }


    public String apiRequestSmsFromBackEnd(){
        return URL_edong + PREFIX_edong + REQUEST_SMS_BACKEND_edong;
    }

    public String apiLoginOtpBackEnd(){
        return URL_edong + PREFIX_edong + LOGIN_WITH_OTP_edong;
    }

    public String apiLogin(){

        return URL_edong + PREFIX_edong + LOGIN_WITH_OTP_edong;
    }

    public String apiGetInfo(){
        return URL_edong + PREFIX_edong + GET_INFO_edong;
    }

    public String apiGetVerifyBank(){
        return URL_edong + PREFIX_edong + GET_BANK_edong;
    }
    public String apiGetVerifyId(){
        return URL_edong + PREFIX_edong + GET_VERIFY_ID_edong;
    }
    public String apiGetAllContact(){
        return URL_edong + PREFIX_edong + GET_ALL_CONTACT_edong;
    }

    public String apiGetTwoContact(){
        return URL_edong + PREFIX_edong + GET_TWO_CONTACT_edong;
    }

    public String apiGetLoan(){
        return URL_edong + PREFIX_edong + GET_LOAN_edong;
    }
    public String apiGetUploadVideo(){
        return URL_edong + PREFIX_edong + GET_UPLOAD_VIDEO_edong;
    }

    public String apiGetUploadRepayBill(){
        return URL_edong + PREFIX_edong + GET_UPLOAD_REPAY_BILL_edong;
    }
    public String apiGetUploadLocation(){
        return URL_edong + PREFIX_edong + GET_UPLOAD_LOCATION_edong;
    }
    public String apiGetRepayInfo(){
        return URL_edong + PREFIX_edong + GET_REPAY_INFO_edong;
    }
    public String apiGetFbAccount(){
        return URL_edong + PREFIX_edong + GET_FB_NAME_edong;
    }

    public static final String apiGetOrderId(){
        return URL_edong + PREFIX_edong + GET_ORDER_ID_edong;
    }

    public static final String apiFunPay(){
        return URL_FUNPAY_edong;
    }


}

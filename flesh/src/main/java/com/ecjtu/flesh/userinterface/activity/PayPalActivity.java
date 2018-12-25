package com.ecjtu.flesh.userinterface.activity;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by xiang on 2018/3/9.
 */

public class PayPalActivity extends AppCompatActivity {
//    private static final String TAG = "paymentExample";
//    /**
//     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
//     * <p>
//     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
//     * from https://developer.paypal.com
//     * <p>
//     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
//     * without communicating to PayPal's servers.
//     */
//    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
//
//    // note that these credentials will differ between live & sandbox environments.
//    private static final String CONFIG_CLIENT_ID = "AS2hguXeqedhAMS9_WQqQezlz_VjhenTS1g7roUA6govlxZ0BghxeApZmQt3OyJSZeqgLDNv7mKUq5TD";
//
//    private static final int REQUEST_CODE_PAYMENT = 1;
//    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
//    private static final int REQUEST_CODE_PROFILE_SHARING = 3;
//    //    http://13.125.219.143:8080/flesh/api/isVip?userId=%s&paymentId=%s
//    private static final String VIP_SERVER_URL = Constants.SERVER_URL + "/api/verifyVip?deviceId=%s&paymentJson=%s";
//
//    private static PayPalConfiguration config = new PayPalConfiguration()
//            .environment(CONFIG_ENVIRONMENT)
//            .clientId(CONFIG_CLIENT_ID)
//            // The following are only used in PayPalFuturePaymentActivity.
//            .merchantName("Example Merchant")
//            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
//            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        TranslucentUtil.INSTANCE.translucentWindow(this);
//        Intent intent = new Intent(this, PayPalService.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        startService(intent);
//
//        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
//        Intent activityIntent = new Intent(PayPalActivity.this, PaymentActivity.class);
//        // send the same configuration for restart resiliency
//        activityIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        activityIntent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
//        startActivityForResult(activityIntent, REQUEST_CODE_PAYMENT);
//    }
//
//
//    private PayPalPayment getThingToBuy(String paymentIntent) {
////        return new PayPalPayment(new BigDecimal("0.01"), "USD", "sample item",
////                paymentIntent);
//
//        return new PayPalPayment(new BigDecimal("5"), "USD", "Vip充值",
//                paymentIntent);
//    }
//
//    public void onProfileSharingPressed(View pressed) {
//        Intent intent = new Intent(PayPalActivity.this, PayPalProfileSharingActivity.class);
//
//        // send the same configuration for restart resiliency
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//
//        intent.putExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES, getOauthScopes());
//
//        startActivityForResult(intent, REQUEST_CODE_PROFILE_SHARING);
//    }
//
//    private PayPalOAuthScopes getOauthScopes() {
//        /* create the set of required scopes
//         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
//         * attributes you select for this app in the PayPal developer portal and the scopes required here.
//         */
//        Set<String> scopes = new HashSet<String>(
//                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL, PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS));
//        return new PayPalOAuthScopes(scopes);
//    }
//
//    protected void displayResultText(String result) {
//        Toast.makeText(
//                getApplicationContext(),
//                result, Toast.LENGTH_LONG)
//                .show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_PAYMENT) {
//            if (resultCode == Activity.RESULT_OK) {
//                PaymentConfirmation confirm =
//                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//                if (confirm != null) {
//                    try {
//                        Log.i(TAG, confirm.toJSONObject().toString(4));
//                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
//                        /**
//                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
//                         * or consent completion.
//                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
//                         * for more details.
//                         *
//                         * For sample mobile backend interactions, see
//                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
//                         */
//                        displayResultText("PaymentConfirmation info received from PayPal");
//                        JSONObject jRoot = confirm.toJSONObject();
//                        JSONObject payment = confirm.getPayment().toJSONObject();
//                        JSONObject proofPayment = confirm.getProofOfPayment().toJSONObject();
//                        JSONObject jsonArray = new JSONObject();
//                        jsonArray.put("confirm", jRoot);
//                        jsonArray.put("payment", payment);
//                        jsonArray.put("proofPayment", proofPayment);
//                        saveVipInfo(confirm.getProofOfPayment().getPaymentId());
//                        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("paymentId", confirm.getProofOfPayment().getPaymentId())
//                                .apply();
//                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//                        if (telephonyManager != null) {
//                            String deviceId = telephonyManager.getDeviceId();
//                            if (TextUtils.isEmpty(deviceId)) {
//                                deviceId = confirm.getProofOfPayment().getPaymentId();
//                                deviceId = MD5Utils.INSTANCE.MD5(deviceId);
//                            }
//                            PreferenceManager.getDefaultSharedPreferences(this).edit().
//                                    putString("deviceId", deviceId).apply();
//                            AsyncNetwork request = new AsyncNetwork();
//                            request.setTimeOut(20 * 1000);
//                            String encodeStr = "";
//                            try {
//                                encodeStr = URLEncoder.encode(jsonArray.toString(), "utf-8");
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                                encodeStr = jsonArray.toString();
//                            }
//                            request.request(String.format(VIP_SERVER_URL, deviceId, encodeStr));
//                            request.setRequestCallback(new IRequestCallbackV2() {
//                                @Override
//                                public void onError(@Nullable HttpURLConnection httpURLConnection, @NotNull Exception exception) {
//                                    setResult(Activity.RESULT_CANCELED);
//                                    finish();
//                                }
//
//                                @Override
//                                public void onSuccess(@Nullable HttpURLConnection httpURLConnection, @NotNull String response) {
//                                    try {
//                                        String code = new JSONObject(response).optString("code");
//                                        if (code.equals("0")) {
//                                            setResult(Activity.RESULT_OK);
//                                        } else {
//                                            setResult(Activity.RESULT_CANCELED);
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                        setResult(Activity.RESULT_CANCELED);
//                                    }
//                                    finish();
//                                }
//                            });
//                        }
//                    } catch (JSONException e) {
//                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
//                    }
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Log.i(TAG, "The user canceled.");
//                Toast.makeText(this, "失败了", Toast.LENGTH_SHORT).show();
//                setResult(Activity.RESULT_CANCELED);
//                finish();
//            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
//                Log.i(TAG, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
//                Toast.makeText(this, "失败了", Toast.LENGTH_SHORT).show();
//                setResult(Activity.RESULT_CANCELED);
//                finish();
//            }
//        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
//            if (resultCode == Activity.RESULT_OK) {
//                PayPalAuthorization auth =
//                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
//                if (auth != null) {
//                    try {
//                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));
//
//                        String authorization_code = auth.getAuthorizationCode();
//                        Log.i("FuturePaymentExample", authorization_code);
//
//                        sendAuthorizationToServer(auth);
//                        displayResultText("Future Payment code received from PayPal");
//
//                    } catch (JSONException e) {
//                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
//                    }
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Log.i("FuturePaymentExample", "The user canceled.");
//            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
//                Log.i(
//                        "FuturePaymentExample",
//                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
//            }
//        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
//            if (resultCode == Activity.RESULT_OK) {
//                PayPalAuthorization auth =
//                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
//                if (auth != null) {
//                    try {
//                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));
//
//                        String authorization_code = auth.getAuthorizationCode();
//                        Log.i("ProfileSharingExample", authorization_code);
//
//                        sendAuthorizationToServer(auth);
//                        displayResultText("Profile Sharing code received from PayPal");
//
//                    } catch (JSONException e) {
//                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
//                    }
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Log.i("ProfileSharingExample", "The user canceled.");
//            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
//                Log.i(
//                        "ProfileSharingExample",
//                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
//            }
//        } else {
//            setResult(Activity.RESULT_CANCELED);
//            finish();
//        }
//    }
//
//    private void sendAuthorizationToServer(PayPalAuthorization authorization) {
//
//        /**
//         * TODO: Send the authorization response to your server, where it can
//         * exchange the authorization code for OAuth access and refresh tokens.
//         *
//         * Your server must then store these tokens, so that your server code
//         * can execute payments for this user in the future.
//         *
//         * A more complete example that includes the required app-server to
//         * PayPal-server integration is available from
//         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
//         */
//
//    }
//
//    public void saveVipInfo(String content) {
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            File file = new File(Environment.getExternalStorageDirectory(), Constants.LOCAL_VIP_PATH);
//            file.mkdirs();
//            BufferedWriter is = null;
//            try {
//                if (!file.exists()) {
//                    file.createNewFile();
//                }
//                is = new BufferedWriter(new FileWriter(file));
//                is.write(content);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (is != null) {
//                    CloseableUtil.INSTANCE.closeQuitely(is);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        // Stop service when done
//        stopService(new Intent(this, PayPalService.class));
//        super.onDestroy();
//    }

}

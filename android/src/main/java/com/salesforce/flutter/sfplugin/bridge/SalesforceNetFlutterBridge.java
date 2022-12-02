/*
 Copyright (c) 2018-present, salesforce.com, inc. All rights reserved.

 Redistribution and use of this software in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 conditions and the following disclaimer in the documentation and/or other materials provided
 with the distribution.
 * Neither the name of salesforce.com, inc. nor the names of its contributors may be used to
 endorse or promote products derived from this software without specific prior written
 permission of salesforce.com, inc.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.flutter.sfplugin.bridge;

import androidx.annotation.NonNull;
import android.util.Base64;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.util.SalesforceSDKLogger;
import com.salesforce.flutter.sfplugin.ui.SalesforceFlutterActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;

import io.flutter.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Flutter bridge for network operations
 */
public class SalesforceNetFlutterBridge extends SalesforceFlutterBridge {

    private static final String PREFIX = "network";

    enum Method {
        sendRequest,
        getClientInfo
    }

    private static final String METHOD_KEY = "method";
    private static final String END_POINT_KEY = "endPoint";
    private static final String PATH_KEY = "path";
    private static final String QUERY_PARAMS_KEY = "queryParams";
    private static final String HEADER_PARAMS_KEY = "headerParams";
    private static final String FILE_PARAMS_KEY = "fileParams";
    private static final String FILE_MIME_TYPE_KEY = "fileMimeType";
    private static final String FILE_URL_KEY = "fileUrl";
    private static final String FILE_NAME_KEY = "fileName";
    private static final String RETURN_BINARY = "returnBinary";
    private static final String ENCODED_BODY = "encodedBody";
    private static final String CONTENT_TYPE = "contentType";
    private static final String TAG = "SalesforceNetFlutterBridge";
    private static final String CLIENT_INSTANCE_URL = "instanceUrl";
    private static final String CLIENT_LOGIN_URL = "loginUrl";
    private static final String CLIENT_ACCOUNT_NAME = "accountName";
    private static final String CLIENT_USERNAME = "username";
    private static final String CLIENT_USER_ID = "userId";
    private static final String CLIENT_ORG_ID = "orgId";
    private static final String CLIENT_COMMUNITY_ID = "communityId";
    private static final String CLIENT_COMMUNITY_URL = "communityUrl";
    private static final String CLIENT_FIRST_NAME = "firstName";
    private static final String CLIENT_LAST_NAME = "lastName";
    private static final String CLIENT_DISPLAY_NAME = "displayName";
    private static final String CLIENT_EMAIL = "email";
    private static final String CLIENT_PHOTO_URL = "photoUrl";
    private static final String CLIENT_THUMBNAIL_URL = "thumbnailUrl";
    private static final String CLIENT_LIGHTNING_DOMAIN = "lightningDomain";
    private static final String CLIENT_LIGHTNING_ID = "lightningSid";
    private static final String CLIENT_VF_DOMAIN = "vfDomain";
    private static final String CLIENT_VF_SID = "vfSid";
    private static final String CLIENT_CONTENT_DOMAIN = "contentDomain";
    private static final String CLIENT_CONTENT_SID = "contentSid";
    private static final String CLIENT_IDENTITY_URL = "identityUrl";
    private static final String CONTENT_CSRF_TOKEN = "csrfToken";
    private static final String CONTENT_ADDITIONAL_OAUTH_VALUES = "additionalOauthValues";

    public SalesforceNetFlutterBridge(SalesforceFlutterActivity currentActivity) {
        super(currentActivity);
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        Method method = Method.valueOf(call.method.substring(PREFIX.length() + 1));
        switch(method) {
            case sendRequest:
                sendRequest((Map<String, Object>) call.arguments, result); break;
            case getClientInfo:
                getClientInfo((Map<String, Object>) call.arguments, result); break;
            default:
                result.notImplemented();
        }
    }

    protected void getClientInfo(Map<String, Object> args,
                                 final MethodChannel.Result callback) {
        try {
            // Getting restClient
            RestClient restClient = getRestClient();

            if (restClient == null) {
                callback.error("No restClient", null, null);
                return;
            }
            final RestClient.ClientInfo clientInfo = restClient.getClientInfo();
            final JSONObject result = new JSONObject();
            result.put(CLIENT_INSTANCE_URL, clientInfo.instanceUrl);
            result.put(CLIENT_LOGIN_URL, clientInfo.loginUrl);
            result.put(CLIENT_ACCOUNT_NAME, clientInfo.accountName);
            result.put(CLIENT_USERNAME, clientInfo.username);
            result.put(CLIENT_USER_ID, clientInfo.userId);
            result.put(CLIENT_ORG_ID, clientInfo.orgId);
            result.put(CLIENT_COMMUNITY_ID, clientInfo.communityId);
            result.put(CLIENT_COMMUNITY_URL, clientInfo.communityUrl);
            result.put(CLIENT_FIRST_NAME, clientInfo.firstName);
            result.put(CLIENT_LAST_NAME, clientInfo.lastName);
            result.put(CLIENT_DISPLAY_NAME, clientInfo.displayName);
            result.put(CLIENT_EMAIL, clientInfo.email);
            result.put(CLIENT_PHOTO_URL, clientInfo.photoUrl);
            result.put(CLIENT_THUMBNAIL_URL, clientInfo.thumbnailUrl);
            result.put(CLIENT_LIGHTNING_DOMAIN, clientInfo.lightningDomain);
            result.put(CLIENT_LIGHTNING_ID, clientInfo.lightningSid);
            result.put(CLIENT_VF_DOMAIN, clientInfo.vfDomain);
            result.put(CLIENT_VF_SID, clientInfo.vfSid);
            result.put(CLIENT_CONTENT_DOMAIN, clientInfo.contentDomain);
            result.put(CLIENT_CONTENT_SID, clientInfo.contentSid);
            result.put(CLIENT_IDENTITY_URL, clientInfo.identityUrl);
            result.put(CONTENT_CSRF_TOKEN, clientInfo.csrfToken);

            result.put(CONTENT_ADDITIONAL_OAUTH_VALUES, clientInfo.additionalOauthValues);

            callback.success(result.toString());
        } catch (Exception exception) {
            returnError("getClientInfo failed: ", exception, callback);
        }
    }

    protected void sendRequest(Map<String, Object> args,
                               final MethodChannel.Result callback) {

        try {
            // Getting restClient
            RestClient restClient = getRestClient();

            if (restClient == null) {
                callback.error("No restClient", null, null);
                return;
            }

            // Prepare request
            RestRequest request = prepareRestRequest(args);
            final boolean returnBinary = args.containsKey(RETURN_BINARY) && ((Boolean) args.get(RETURN_BINARY));

            restClient.sendAsync(request, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse response) {
                    try {
                        SalesforceSDKLogger.d(TAG, "response: " + response.asString());

                        // Sending a string over and letting javascript do a JSON.decode(result)

                        // Not a 2xx status
                        if (!response.isSuccess()) {
                            callback.error("Got http response " + response.getStatusCode(), response.asString(), null);
                        }
                        // Binary response
                        else if (returnBinary) {
                            JSONObject result = new JSONObject();
                            result.put(CONTENT_TYPE, response.getContentType());
                            result.put(ENCODED_BODY, Base64.encodeToString(response.asBytes(), Base64.DEFAULT));
                            callback.success(result.toString());
                        }
                        // Other cases
                        else {
                            callback.success(response.asString());
                        }
                    } catch (Exception e) {
                        if (e instanceof RestClient.RefreshTokenRevokedException) {
                            currentActivity.logout();
                        }
                        returnError("sendRequest failed", e, callback);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    if (exception instanceof RestClient.RefreshTokenRevokedException) {
                        currentActivity.logout();
                    }
                    returnError("sendRequest failed", exception, callback);
                }
            });

        } catch (Exception exception) {
            returnError("sendRequest failed", exception, callback);
        }
    }

    private void returnError(String message, Exception exception, MethodChannel.Result callback) {
        SalesforceSDKLogger.e(TAG, message, exception);
        callback.error(exception.getClass().getName(), exception.getMessage(), exception);
    }

    @NonNull
    private RestRequest prepareRestRequest(Map<String, Object> args) throws UnsupportedEncodingException, URISyntaxException {
        // Parse args
        RestRequest.RestMethod method = RestRequest.RestMethod.valueOf((String) args.get(METHOD_KEY));
        String endPoint = (String) args.get(END_POINT_KEY);
        String path = (String) args.get(PATH_KEY);
        Map<String, Object> queryParams = (Map<String, Object>) args.get(QUERY_PARAMS_KEY);
        Map<String, String> additionalHeaders = (Map<String, String>) args.get(HEADER_PARAMS_KEY);
        Map<String, Map<String, String>> fileParams = (Map<String, Map<String, String>>) args.get(FILE_PARAMS_KEY);

        String urlParams = "";
        RequestBody requestBody = null;
        if (method == RestRequest.RestMethod.DELETE || method == RestRequest.RestMethod.GET || method == RestRequest.RestMethod.HEAD) {
            urlParams = buildQueryString(queryParams);
        } else {
            requestBody = buildRequestBody(queryParams, fileParams);
        }

        String separator = urlParams.isEmpty()
                ? ""
                : path.contains("?")
                ? (path.endsWith("&") ? "" : "&")
                : "?";

        return new RestRequest(method, endPoint + path + separator + urlParams, requestBody, additionalHeaders);
    }

    /**
     * Returns the RestClient instance being used by this bridge.
     *
     * @return RestClient instance.
     */
    protected RestClient getRestClient() {
        return currentActivity != null ? currentActivity.getRestClient() : null;
    }

    private static String buildQueryString(Map<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(), RestRequest.UTF_8)).append("&");
        }
        return sb.toString();
    }

    private static RequestBody buildRequestBody(Map<String, Object> params, Map<String, Map<String, String>> fileParams) throws URISyntaxException {
        if (fileParams.isEmpty()) {
            return RequestBody.create(RestRequest.MEDIA_TYPE_JSON, new JSONObject(params).toString());
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue().toString());
            }

            // File params expected to be of the form:
            // {<fileParamNameInPost>: {fileMimeType:<someMimeType>, fileUrl:<fileUrl>, fileName:<fileNameForPost>}}
            for (Map.Entry<String, Map<String, String>> fileParamEntry : fileParams.entrySet()) {
                Map<String, String> fileParam = fileParamEntry.getValue();
                String fileParamName = fileParamEntry.getKey();
                String mimeType = fileParam.get(FILE_MIME_TYPE_KEY);
                String name = fileParam.get(FILE_NAME_KEY);
                URI url = new URI(fileParam.get(FILE_URL_KEY));
                File file = new File(url);
                MediaType mediaType = MediaType.parse(mimeType);
                builder.addFormDataPart(fileParamName, name, RequestBody.create(mediaType, file));
            }
            return builder.build();
        }
    }
}
package com.rocketchat.core;

public class TestMessages {
    public static final String CONNECT_REQUEST = "{\"msg\":\"connect\",\"version\":\"1\",\"support\":[\"1\",\"pre2\",\"pre1\"]}";
    public static final String CONNECT_RESPONSE_OK = "{\"msg\":\"connected\",\"session\":\"tWGmekeKP2FcBAevf\"}";

    public static final String LOGIN_REQUEST = "{\"msg\":\"method\",\"method\":\"login\",\"id\":\"1\",\"params\":[{\"password\":{\"digest\":\"d8586252e7825f8e7c5c4f51906acaa68278899aba3be7078fa0c76bdfc72bab\",\"algorithm\":\"sha-256\"},\"user\":{\"username\":\"testuserrocks\"}}]}";
    public static final String LOGIN_RESPONSE_OK = "{\"msg\":\"result\",\"id\":\"1\",\"result\":{\"id\":\"yG6FQYRsuTWRK8KP6\",\"token\":\"Yk_MNMp7K6A8J_3ytsC3rxwIZe9PZ4pfkPe-6G7JPYg\",\"tokenExpires\":{\"$date\":1511909570220}}}";
    public static final String LOGIN_REQUEST_FAIL = "{\"msg\":\"method\",\"method\":\"login\",\"id\":\"1\",\"params\":[{\"password\":{\"digest\":\"8ecd67dceb90a898b1f94bddf570ce1c23629cd328140d81f1e02e43d42eb44e\",\"algorithm\":\"sha-256\"},\"user\":{\"username\":\"testuserrocks\"}}]}";
    public static final String LOGIN_RESPONSE_FAIL = "{\"msg\":\"result\",\"id\":\"1\",\"error\":{\"isClientSafe\":true,\"error\":403,\"reason\":\"User not found\",\"message\":\"User not found [403]\",\"errorType\":\"Meteor.Error\"}}";

    public static final String LOGIN_RESUME_REQUEST = "{\"msg\":\"method\",\"method\":\"login\",\"id\":\"1\",\"params\":[{\"resume\":\"tHKn4H62mdBi_gh5hjjqmu-x4zdZRAYiiluqpdRzQKD\"}]}";
    public static final String LOGIN_RESUME_REQUEST_FAIL = "{\"msg\":\"method\",\"method\":\"login\",\"id\":\"1\",\"params\":[{\"resume\":\"tHKn4H62mdBi_gh5hjjqmu-x4zdZRAYiiluqpdR\"}]}";
    public static final String LOGIN_RESUME_RESPONSE_OK = "{\"msg\":\"result\",\"id\":\"1\",\"result\":{\"id\":\"yG6FQYRsuTWRK8KP6\",\"token\":\"tHKn4H62mdBi_gh5hjjqmu-x4zdZRAYiiluqpdRzQKD\",\"tokenExpires\":{\"$date\":null}}}";
    public static final String LOGIN_RESUME_RESPONSE_FAIL = "{\"msg\":\"result\",\"id\":\"1\",\"error\":{\"isClientSafe\":true,\"error\":403,\"reason\":\"You've been logged out by the server. Please log in again.\",\"message\":\"You've been logged out by the server. Please log in again. [403]\",\"errorType\":\"Meteor.Error\"}}";
}

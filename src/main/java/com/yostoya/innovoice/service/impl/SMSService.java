package com.yostoya.innovoice.service.impl;

import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;

import static com.twilio.Twilio.init;
import static com.twilio.rest.api.v2010.account.Message.creator;

public class SMSService {

    @Value("${twilio.from}")
    private static String from;

    @Value("${twilio.sid}")
    private static String sid;

    @Value("${twilio.token}")
    private static String token;

    public static void sendSMS(String to, String sms) {
        init(sid, token);
        var message = creator(new PhoneNumber("+" + to), new PhoneNumber(from), sms).create();
        System.out.println(message);
    }
}

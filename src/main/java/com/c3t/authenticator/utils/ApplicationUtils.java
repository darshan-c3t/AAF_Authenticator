package com.c3t.authenticator.utils;

import com.c3t.authenticator.entity.ClientUsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationUtils {

    public static Map<String, ClientUsers> tokenRecords = new HashMap<>();
    public static List<String> blackListToken = new ArrayList<>();
}

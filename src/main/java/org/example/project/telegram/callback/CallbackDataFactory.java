package org.example.project.telegram.callback;

import org.example.project.telegram.enums.CallbackAction;

import java.util.HashMap;
import java.util.Map;

public final class CallbackDataFactory {

    private static final String SEP = ":";

    private CallbackDataFactory() {
    }

    public static String build(CallbackAction action, String... params) {
        StringBuilder sb = new StringBuilder(action.name());
        for (String p : params) {
            sb.append(SEP).append(p == null ? "" : p);
        }
        String data = sb.toString();
        if (data.length() > 64) {
            throw new IllegalArgumentException("Callback data exceeds 64 bytes: " + data.length());
        }
        return data;
    }

    public static ParsedCallback parse(String data) {
        if (data == null || data.isBlank()) {
            return new ParsedCallback(CallbackAction.NOOP, new String[0]);
        }
        String[] parts = data.split(SEP);
        CallbackAction action;
        try {
            action = CallbackAction.valueOf(parts[0]);
        } catch (IllegalArgumentException e) {
            action = CallbackAction.NOOP;
        }
        String[] params = new String[parts.length - 1];
        if (parts.length > 1) {
            System.arraycopy(parts, 1, params, 0, params.length);
        }
        return new ParsedCallback(action, params);
    }

    public record ParsedCallback(CallbackAction action, String[] params) {

        public String param(int index) {
            return params.length > index ? params[index] : null;
        }

        public int paramAsInt(int index, int defaultValue) {
            try {
                return Integer.parseInt(param(index));
            } catch (Exception e) {
                return defaultValue;
            }
        }

        public Map<String, String> asMap() {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < params.length; i++) {
                map.put("p" + i, params[i]);
            }
            return map;
        }
    }
}

package com.steamgamessearcherbackend.utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class YouDaoTranslator {
    private static final String APP_KEY = "2bb8b08fb3927cad";     // 您的应用ID
    private static final String APP_SECRET = "N2bObBCiEKpakwaM4v6qlrF8oLsr6aoU";  // 您的应用密钥

    public static String translate(String input){
        Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[]{input});
        params.put("from", new String[]{"auto"});
        params.put("to", new String[]{"en"});
        try {
            AuthV3Util.addAuthParams(APP_KEY, APP_SECRET, params);
            byte[] result = HttpUtil.doPost("https://openapi.youdao.com/api", null, params, "application/json");
            // 打印返回结果
            String translationResult = new String(result, StandardCharsets.UTF_8);
//            if (result != null) {
//                System.out.println(translationResult);
//            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(translationResult);
            JsonNode translationNode = rootNode.path("translation");
            if (translationNode.isArray() && !translationNode.isEmpty()) {
                String translation = translationNode.get(0).asText();
                // System.out.println("Translation: " + translation);
                return translation;
            } else {
                System.out.println("Translation not found or is empty.");
                return null;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

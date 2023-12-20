package com.netty.customer.utils;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.Resource;
import okhttp3.*;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 功能描述：ChatGPT工具类
 * 作者：唐泽齐
 */
@Component
public class ChatGPTUtils {

    private static Instrumentation instrumentation;
    static volatile Cache<String, ConcurrentLinkedDeque> gpt;

    @Resource
    public void makeCahe(CacheManager cacheManager) {
        gpt = cacheManager.getCache("ChatGPT", String.class, ConcurrentLinkedDeque.class);
    }

    final static private String[] keys = {
            "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"
            , "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"
            , "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"
            , "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"
            , "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"
            , "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"
            , "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"
            , "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"
            , "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"
            , "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"
            , "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"
            , "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"
            , "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"
            , "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"
            , "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"
    };
    final static private String[] tokens = {
            "vickye89@hotmail.com---LeZ25X5dwL"
            , "eugeniev552@hotmail.com---4w95MnvIvc"
            , "ayakonewuyo@hotmail.com---q8UCd2lST4"
            , "ludmillasteb9@hotmail.com---7sb54Oii8I"
            , "chr89kuchto@hotmail.com---Cea7IQj5ud"
            , "emil03mk@hotmail.com---ENC82hip2A"
            , "lydiavn9ktutoky@hotmail.com---6351VsZz25"
            , "tulagj3@hotmail.com---Wx99eCqer7"
            , "prue56zjehle@hotmail.com---8398k84X85"
            , "vernitagq2@hotmail.com---geFfr4H0x9"
            , "kirstiealtqw@hotmail.com---1FBbV8OJg9"
            , "ralphnamer32d@hotmail.com---F85T86YtbL"
            , "celinabullievmu@hotmail.com---OqTz0lj525"
            , "chanafdidelaet@hotmail.com---6x3CM4pPYY"
            , "alita07pbogdon@hotmail.com---Umppl1ylc0"
            , "averilltxgt@hotmail.com---94cymN2p42"
            , "kaeleeviarla@hotmail.com---GUR00fTOS5"
            , "johniehsro@hotmail.com---Env5d4Cd1T"
            , "shizuebrf0c@hotmail.com---7f2b55JRGQ"
            , "gloryx5fo@hotmail.com---qYSZn2J2eH"
            , "jackalynashxa@hotmail.com---g40KT0c2aA"
            , "tisaphkgwes@hotmail.com---94c9G550lm"
            , "brm3hort@hotmail.com---7wRRzS3PjU"
            , "emiliel0al@hotmail.com---pXGh4yLZz5"
            , "nwsmayton@hotmail.com---5DPuVWcw4e"
            , "faithtyrub@hotmail.com---VzRJlm1nS8"
            , "lashonda01kou@hotmail.com---01XKYgn2o7"
            , "ossielukena0x@hotmail.com---i5aW60i51G"
            , "clitus8haxe@hotmail.com---11n370pKrx"
            , "tiav2zk4@hotmail.com---b2PsID8X8Q"
            , "rokorvinlgy0@hotmail.com---olOR1G3k6W"
            , "mariesowardu1@hotmail.com---jMh68mz2A9"
            , "irvinglgd@hotmail.com---VnqYMuc2Jc"
            , "delorawwuhusch@hotmail.com---GrGhPP830l"
            , "beckisatowpkm@hotmail.com---7Rsc5K8lz6"
            , "samanthamilc3@hotmail.com---jZ71fWSMW9"
            , "asavagells@hotmail.com---xfxbFQFfX8"
            , "danaeju2kaut@hotmail.com---yQlw96OaeK"
            , "tanalb4br@hotmail.com---d7d7GiWxsg"
            , "arlinda8r0g@hotmail.com---WLk1COUGSt"
            , "katelynsz6y@hotmail.com---99vYa3hien"
            , "aileenyditiw@hotmail.com---O8ySz2TT79"
            , "kittyapoch5q@hotmail.com---1kb8MbO5m9"
            , "andyvenz1d@hotmail.com---oA99Pqq9I3"
            , "lakiamcmnwuh@hotmail.com---QPytS87q0i"
            , "mandaekins3wd@hotmail.com---Hd89v6nU05"
            , "arianne5t40kup@hotmail.com---ehMtR379iw"
            , "maryannasgu@hotmail.com---3uyZmI118X"
            , "kathec873@hotmail.com---2yQ9qy7n19"
            , "inesllva@hotmail.com---3bkkLA05d6"
            , "geritc9jz@hotmail.com----I1wfh8rS61"
            , "jerrybergock4i@hotmail.com----BJ5KJQ0DlK"
            , "camillavvlgri@hotmail.com---3AKOLwANcD"
            , "lucreciat2est@hotmail.com---yBJ60bP54S"
            , "charissauptrdu@hotmail.com---p2YF90AH9F"
            , "clfultz37vi@hotmail.com---CC26qH3V0U"
            , "jasmyny8so@hotmail.com---YPP188M965"
            , "lasfacioqbj@hotmail.com---F0z8cd090S"
            , "arnoldhwx9s@hotmail.com---13GcrRSLRy"
            , "percivalwilsm@hotmail.com----RT25f10uKv"
            , "nievesfavors9cb@hotmail.com----3mVRN6YmgN"
            , "eugeniev552@hotmail.com----4w95MnvIvc"
            , "ayakonewuyo@hotmail.com----q8UCd2lST4"
            , "ludmillasteb9@hotmail.com----7sb54Oii8I"
            , "chr89kuchto@hotmail.com----Cea7IQj5ud"
            , "emil03mk@hotmail.com----ENC82hip2A"
            , "lydiavn9ktutoky@hotmail.com-----6351VsZz25"
            , "tulagj3@hotmail.com----Wx99eCqer7"
            , "prue56zjehle@hotmail.com----8398k84X85"
            , "volodya.suslov.2024@mail.ru----N01LRY2u"
            , "maks.koshkin.2024@mail.ru----z5P2A9QEYX"
            , "vityusha.popov.2024@mail.ru----l1kcBRj9P4"
            , "aslanov----2024@mail.ru"
            , "mitya.suvorov.1994@mail.ru----eDhyUmEcY3"
            , "maksim.naumov.2024@mail.ru----FnD8LI6Oy"
            , "oleg_baranov_2024@mail.ru----307ih4Rnp"
            , "feliks.kalashnikov.89@mail.ru----rfTY6DI9FZ"
            , "vladik.golofast@mail.ru----7w0tOWRjTY"
            , "tima.nikiforov.2024@mail.ru----TJILFvyd"
            , "dima.safronov.2024@mail.ru----pHX27cN0"
            , "strakhov.2024@mail.ru----BWYw3fvOGp"
            , "petr.titov.2003@mail.ru----unxkfIbBRL"
            , "tosha.kornev.2024@mail.ru----hzY9qQle"
            , "vladimir.goncharov.2024@mail.ru----ZdW14pDJ"
            , "azamat.stepanov.06@mail.ru----vyQK6LjW"
            , "vakhrushev_2024@mail.ru----Fs0P87e6x"
            , "sannikov_2024@mail.ru----Ml2ITAdJ"
            , "vanyusha.ushakov.2005@mail.ru----pFfLHt6sIz"
            , "ilyukha.fedorov.1995@mail.ru----lMS6bNBfp"
            , "misha.kulikov.2024@mail.ru----w0n1ciFH"
            , "sasha.anokhin.2024@mail.ru----pKzjMcUYT"
            , "baturin.2024@mail.ru----lc3XgDFsA6"
            , "khaziyev2024@mail.ru----8EhRtZUnPe"
            , "pavel.denisov.2024@mail.ru----npVIdoMyN"
            , "vovochka.shklyayev@mail.ru----YMdjyP2gw"
            , "sashuta.kulikov.2024@mail.ru----d9mNk3o2R"
            , "shestopalov.2024@mail.ru----QC0TyBFY"
            , "eduard.vinokurov.2024@mail.ru----RKmY6dkWFe"
            , "olezhka.litvinov.2024@mail.ru----a72QBPKhF"
            , "kotov_aleksandr_1980_15_6@mail.ru----g5GWDcmk"
    };

    private final static OkHttpClient client = new OkHttpClient.Builder().readTimeout(Duration.ofDays(1)).connectTimeout(Duration.ofDays(1)).build();
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/";

    public static String chat(String user, String message) throws Throwable {
        return chat(user, message, keys[14]);
    }

    public static String chat(String user, String message, String key) throws Throwable {
        String completions = "";
        try {
            ConcurrentLinkedDeque<Map> content = gpt.get(user);
            if (ObjectUtils.isEmpty(content)) content = new ConcurrentLinkedDeque<Map>();
            while (content.size() > 1000) {
                content.pollFirst();
            }
            content.addLast(MapUtil.builder().put("role", "user").put("content", message).build());
            while (instrumentation.getObjectSize(content) > 4097) {
                content.pollFirst();
            }
            //请求参数
            Map map = new HashMap<>();
            map.put("model", "gpt-3.5-turbo-0301");
            map.put("messages", content.toArray());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(map));
            Request request = new Request.Builder()
                    .url(OPENAI_API_URL + "completions")
                    .header("Authorization", "Bearer " + key)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .post(requestBody) //post请求
                    .build();
            completions = client.newCall(request).execute().body().string();
            if (ObjectUtils.isEmpty(JSON.parseObject(completions).getJSONArray("choices"))) {
                content.removeLast();
                completions = JSON.parseObject(completions).getJSONObject("error").getString("message");
            } else {
                for (Object o : JSON.parseObject(completions).getJSONArray("choices")) {
                    content.addLast(JSON.parseObject(JSON.toJSONString(o)).getJSONObject("message").toJavaObject(Map.class));
                }
                completions = content.peekLast().get("content").toString();
                gpt.put(user, content);
            }
        } catch (IOException e) {
            e.printStackTrace();
            completions = "请稍后重试";
        }
        return completions;
    }

    private static void chattest() throws Throwable {
        System.out.println("输入聊天信息：");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(chat("15", scanner.nextLine()));
        }
    }

    public static String key(String username, String password) throws Throwable {

        OkHttpClient client = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        String credential = Credentials.basic(username, password);
                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .build();
                    }
                })
                .build();

        // Prepare JSON data for request body
        JsonObject jsonBody = new JsonObject();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody.toString());

        // Set up HTTP request
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/auth/create")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .build();

        // Send the request to server and get response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Extract OpenAI access token from the response
            JsonObject jsonResponse = JsonParser.parseString(response.body().string()).getAsJsonObject();
            String openaiToken = jsonResponse.get("access_token").getAsString();
            System.out.println("OpenAI access token: " + openaiToken);
            return openaiToken;
        }
    }

    private static void testKey() throws Throwable {
        List<String> ables = new ArrayList<>();
        for (String token : tokens) {
            String[] split = token.split("----");
            if (split.length < 2) split = token.split("---");
            String key = key(split[0], split[1]);
            try {
                String chat = chat("15", "1", key);
                ables.add(key);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        System.out.println(ables);
    }

    public static void main(String[] args) throws Throwable {
        testKey();
    }


}

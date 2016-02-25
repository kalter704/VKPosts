package com.example.vasiliy.vkposts;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.util.VKUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String accessToken;
    String message;

    String returnCaptchaKey;
    boolean isGetCaptchaKey;

    int countPosts;

    boolean isGoNext;

    TextView tv;

    private String[] scope = new String[]{
            VKScope.WALL
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        //System.out.println(Arrays.asList(fingerprints));

        countPosts = 0;
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText("Опубликованно постов = " + String.valueOf(countPosts));

        VKSdk.login(this, scope);

        accessToken = VKAccessToken.currentToken().accessToken;

        isGoNext = true;

        //message = "Добавь меня!!!";

        message = "\uD83D\uDCB0Хочешь по 50-100 заявок в день❓\uD83D\uDCB0 \n \uD83D\uDCB0Тогда тебе к нам! Добавь всех из списка \n ○〰 ○〰 ○〰 ○〰 \n \uD83D\uDCB2АДМИНЫ\uD83D\uDCB2 \n \uD83C\uDF1A @id163385541 (\uD83C\uDF3AАдминка\uD83C\uDF3A) \n \uD83C\uDF1D@id295591956 (\uD83C\uDF37Зам.Админа\uD83C\uDF37) \n \uD83D\uDC51.::VIP::.\uD83D\uDC51 \n \uD83C\uDFAF \uD83C\uDF1F@id336021593 (Ольга Ларина)\uD83C\uDF1F \n \uD83C\uDFAF \uD83C\uDF1F@id322157495 (Лиза Драганова)\uD83C\uDF1F \n \uD83C\uDFAF \uD83C\uDF1F@id350952179 (Иван Кобра)\uD83C\uDF1F \n \uD83C\uDFAF\uD83C\uDF1F@id262819189 (Дмитрий Некрасов)\uD83C\uDF1F \n \uD83C\uDF89 УЧАСТНИКИ \uD83C\uDF89\n ✨@id350485363 (Артур Великий)✨\n ✨@id340983842 (Алина Волкова)✨\n ✨@id221687262 (Лиза Каргина)✨\n ✨@id245084399 (Анастасия Царёва)✨ \n ✨@liza_kiseleva_09742 (Елизавета Киселёва)✨\n ✨@id191473719 (Анастасия Зимогляд)✨ \n ✨@id345261648 (Александр Сахаров)✨ \n ✨@id351201918 (Иисус Христос)✨ \n ✨@id256931905 (Катя Яроцинская)✨ \n ✨@id6254046 (Анна Шварц)✨ \n ✨@id336427015 (Дмитрий Романов) ✨ \n ✨@id282509700 (Карина Юнусова) ✨ \n ✨@id285908462 (Alina Muzychenko)✨ \n ✨@id152100209 (Артём Московский) ✨ \n ✨@id315759394 (Иван Шнайдер) ✨ \n ●▬▬▬▬▬▬▬▬ஜ ۩۞۩ ஜ▬▬▬▬▬▬▬▬● \n Хочешь в список? Пиши им с пометкой \"Хочу в список\"⬇ \n @id163385541(\uD83C\uDF3AАдминке \uD83C\uDF3A) или \n @id295591956 (\uD83C\uDF37Лиле\uD83C\uDF37) \n ●▬▬▬▬▬▬▬▬ஜ ۩۞۩ ஜ▬▬▬▬▬▬▬▬●";

        startWork();

    }

    protected void startWork() {
        int cc = 0;
        while(cc < 5) {
            if(isGoNext) {
                isGoNext = false;
                String id = "-" + "114571061";
                new BackgroundSendPosts().execute(id);
            }
            ++cc;
        }
    }

    class BackgroundSendPosts extends AsyncTask<String, Map, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String captchaId = "-1";
            String captchaKey = "-1";
            boolean isExit;
            do {
                String result = doPost(params[0], captchaId, captchaKey);
                if (isBadResponse(result)) {
                    isExit = false;
                    Map captcha = getCaptch(result);

                    publishProgress(captcha);

                    isGetCaptchaKey = false;
                    while (!isGetCaptchaKey) {
                    }

                    captchaId = (String) captcha.get("captcha_sid");
                    captchaKey = returnCaptchaKey;
                } else {
                    isExit = true;
                    countPosts++;
                    captchaId = "-1";
                    captchaKey = "-1";
                }
            } while (isExit);
            return true;
        }

        @Override
        protected void onProgressUpdate(Map... values) {
            super.onProgressUpdate(values);

            Intent intent = new Intent(MainActivity.this, InputCaptchActivity.class);
            intent.putExtra("captch_url", (String) values[0].get("captcha_img"));
            startActivity(intent);
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

        }
    }

    protected Map getCaptch(String result) {
        Map capt = new HashMap<String, String>();
        JSONObject dataJsonObject = null;
        try {
            dataJsonObject = new JSONObject(result);
            JSONObject response = dataJsonObject.getJSONObject("error");
            JSONObject captchaSid = response.getJSONObject("captcha_sid");
            JSONObject captchaImg = response.getJSONObject("captcha_img");
            capt.put("captcha_sid", captchaSid.getString("captcha_sid"));
            capt.put("captcha_img", captchaImg.getString("captcha_img"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("qwerty", (String) capt.get("captcha_img"));

        return capt;
    }

    protected boolean isBadResponse(String result) {
        JSONObject dataJsonObject = null;
        /*
        try {
            dataJsonObject = new JSONObject(result);
            JSONObject response = dataJsonObject.getJSONObject("error");
        } catch (JSONException e) {
            return true;
        }
        */
        return false;
    }

    protected String doPost(String id, String captchaId, String captchaKey) {
        String requestString = null;
        if (captchaId.equals("-1") && captchaKey.equals("-1")) {
            try {
                requestString = "https://api.vk.com/method/wall.post?owner_id=" +
                        id +
                        "&friends_only=0&from_group=0&message=" +
                        URLEncoder.encode(message, "UTF-8") +
                        "&v=5.45&access_token=" +
                        accessToken;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            try {
                requestString = "https://api.vk.com/method/wall.post?owner_id=" +
                        id +
                        "&friends_only=0&from_group=0&message=" +
                        URLEncoder.encode(message, "UTF-8") +
                        "&captcha_sid=" +
                        captchaId +
                        "&captcha_key=" +
                        captchaKey +
                        "&v=5.45&access_token=" +
                        accessToken;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        BufferedReader reader = null;
        try {
            URL url = new URL(requestString);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoInput(true);
            c.setReadTimeout(10000);
            c.connect();
            reader = new BufferedReader(new InputStreamReader(c.getInputStream()));

            StringBuilder buf = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }
            c.disconnect();
            return (buf.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Toast.makeText(getApplicationContext(), "Good", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}

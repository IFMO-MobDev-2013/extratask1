package ru.marsermd.flikrVigintus.utility.http.translate.yandex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.marsermd.flikrVigintus.utility.http.translate.TranslateResult;
import ru.marsermd.flikrVigintus.utility.http.translate.TranslateTask;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 29.09.13
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class YandexTranslateTask extends TranslateTask {
    protected String apiKey;

    public YandexTranslateTask(String apiKey) {
        this(apiKey, null, null);
    }

    public YandexTranslateTask(String apiKey, String srcText, String toLanguage) {
        this(apiKey, srcText, toLanguage, null);
    }

    public YandexTranslateTask(String apiKey, String srcText, String toLanguage, String fromLanguage) {
        this.apiKey = apiKey;
        this.srcText = srcText;
        this.toLanguage = toLanguage;
        this.fromLanguage = fromLanguage;
    }


    @Override
    protected TranslateResult getResultByJson(JSONObject jsonObject) {
        TranslateResult translateResult = new TranslateResult();
        try {
            translateResult.setLangDetected(jsonObject.getJSONObject("detected").getString("lang"));
            JSONArray translationsJSONArray = jsonObject.getJSONArray("text");
            String[] translations = new String[translationsJSONArray.length()];
            for (int i = 0; i < translations.length; ++i) translations[i] = translationsJSONArray.getString(i);
            translateResult.setResults(translations);
        } catch (JSONException e) {
            handleJSONException(e, jsonObject.toString());
            return null;
        }

        return translateResult;
    }

    @Override
    protected String getUrl() {
        String url = null;
        Map<String, String> getParams = new Hashtable<String, String>();
        getParams.put("key", apiKey);
        getParams.put("text", srcText);
        getParams.put("lang", fromLanguage == null ? toLanguage : fromLanguage + "-" + toLanguage);
        getParams.put("options", "1");
        try {
            url = composeUrl("https://translate.yandex.net/api/v1.5/tr.json/translate", getParams);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

}

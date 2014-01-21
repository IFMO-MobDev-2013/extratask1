package ru.marsermd.flikrVigintus.utility.http.translate;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 29.09.13
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class TranslateResult {
    protected String[] results = null;
    protected String langDetected = null;

    public String[] getResults() {
        return results;
    }

    public String getLangDetected() {
        return langDetected;
    }

    public String getResult() {
        if (results != null && results.length > 0) {
            return results[0];
        }
        return null;
    }

    public void setResults(String[] results) {
        this.results = results;
    }

    public void setLangDetected(String langDetected) {
        this.langDetected = langDetected;
    }

}

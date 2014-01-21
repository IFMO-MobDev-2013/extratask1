package ru.marsermd.flikrVigintus.utility.http.translate;

import ru.marsermd.flikrVigintus.utility.http.HttpUtility;
import ru.marsermd.flikrVigintus.utility.http.JsonResponseHttpTask;

import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 29.09.13
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public abstract class TranslateTask extends JsonResponseHttpTask<TranslateResult> {
    protected String srcText;
    protected String fromLanguage;
    protected String toLanguage;

    public void setSrcText(String srcText) {
        this.srcText = srcText;
    }

    public void setFromLanguage(String fromLanguage) {
        this.fromLanguage = fromLanguage;
    }

    public void setToLanguage(String toLanguage) {
        this.toLanguage = toLanguage;
    }

    public String getSrcText() {
        return srcText;
    }

    public String getFromLanguage() {
        return fromLanguage;
    }

    public String getToLanguage() {
        return toLanguage;
    }

    @Override
    protected Executor getExecutor() {
        return HttpUtility.getInstance().getTranslateApiExecutor();
    }
}

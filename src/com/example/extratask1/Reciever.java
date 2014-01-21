package com.example.extratask1;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created with IntelliJ IDEA.
 * User: PWR
 * Date: 19.01.14
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */
public class Reciever extends ResultReceiver
{

    public interface inReciever
    {
        public void onReceiveResult(int result, Bundle data);
    }

    private inReciever reciever;

    public Reciever(Handler handler)
    {
        super(handler);
    }

    public void setReciever(inReciever inreciever)
    {
        reciever = inreciever;
    }

    @Override
    protected void onReceiveResult(int result, Bundle data)
    {
        if(reciever != null)
        {
            reciever.onReceiveResult(result, data);
        }
    }
}

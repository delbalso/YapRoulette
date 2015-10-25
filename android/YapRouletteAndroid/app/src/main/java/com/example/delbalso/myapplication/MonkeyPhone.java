/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package com.example.delbalso.myapplication;
/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

        import android.content.Context;
        import android.os.AsyncTask;
        import android.util.Log;

        import com.twilio.client.Connection;
        import com.twilio.client.ConnectionListener;
        import com.twilio.client.Device;
        import com.twilio.client.Twilio;

        import java.util.HashMap;
        import java.util.Map;

public class MonkeyPhone implements Twilio.InitListener
{
    private static final String TAG = "MonkeyPhone";

    private Device device;
    private Connection connection;

    public MonkeyPhone(Context context)
    {
        Twilio.initialize(context, this /* Twilio.InitListener */);
    }

    /* Twilio.InitListener method */
    @Override
    public void onInitialized()
    {
        Log.d(TAG, "Twilio SDK is ready");

        new RetrieveCapabilityToken().execute("http://rocky-badlands-8451.herokuapp.com/token");
    }

    private class RetrieveCapabilityToken extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            try{
                String capabilityToken = HttpHelper.httpGet(params[0]);
                Log.d(TAG, "Capability Token: " + capabilityToken);
                return capabilityToken;
            } catch( Exception e ){
                Log.e(TAG, "Failed to obtain capability token: " + e.getLocalizedMessage());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String capabilityToken ){
            Log.w(TAG, "Setting Capability token");
            MonkeyPhone.this.setCapabilityToken(capabilityToken);
        }
    }

    protected void setCapabilityToken(String capabilityToken){
        device = Twilio.createDevice(capabilityToken, null /* DeviceListener */);
    }

    /* Twilio.InitListener method */
    @Override
    public void onError(Exception e)
    {
        Log.e(TAG, "Twilio SDK couldn't start: " + e.getLocalizedMessage());
    }

    @Override
    protected void finalize()
    {
        if (device != null)
            device.release();
    }

    public void connect(String phoneNumber)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("To", phoneNumber);
        ConnectionListener cl = new ConnectionListener() {
            @Override
            public void onConnecting(Connection connection) {
                Log.d(TAG, "Connecting...");

            }

            @Override
            public void onConnected(Connection connection) {
                Log.d(TAG, "Connected");
            }

            @Override
            public void onDisconnected(Connection connection) {

            }

            @Override
            public void onDisconnected(Connection connection, int i, String s) {

            }
        };
        connection = device.connect(parameters /* parameters */, null /* ConnectionListener */);
        if (connection == null)
            Log.w(TAG, "Failed to create new connection");
    }

    public void disconnect()
    {
        Log.d(TAG, "Disconnecting");
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }
}
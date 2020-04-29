package lasdot.com.ui.home;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FetchNewsJSON extends AsyncTask<String, Void, String> {
    private Context context;
    private Handler handler;
    private String type;

    public FetchNewsJSON(Context context, Handler handler, String type){
        this.context = context;
        this.handler = handler;
        this.type = type;
    }

    @Override
    protected String doInBackground(String... urls) {
        String url = urls[0];
        HttpsURLConnection con = null ;
        InputStream is = null;

        try {
            con = (HttpsURLConnection) ( new URL(url)).openConnection();
            con.setRequestMethod("GET");
            con.connect();

            StringBuffer buffer = null;
            try {
                buffer = new StringBuffer();
                is = con.getInputStream();
                BufferedReader br = new BufferedReader(new
                        InputStreamReader(is));
                String line = null;
                while ( (line = br.readLine()) != null )
                    buffer.append(line);

            } catch (IOException e) {
                e.printStackTrace();
            }
            is.close();
            con.disconnect();

            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Message message = this.handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("Type", this.type);
        bundle.putString("result", s);
        message.setData(bundle);
        this.handler.sendMessage(message);
    }
}

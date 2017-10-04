package comdeamond18.vk.timeline;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class getTimeline
{
    public String getHtml(String request)
    {
        try {
            URL url = new URL("http://www.omgtu.ru/students/temp/ajax.php?action=get_schedule");
            URLConnection con = url.openConnection();
            if (con instanceof HttpURLConnection) {
                HttpURLConnection httpConnect = (HttpURLConnection) con;
                httpConnect.setRequestMethod("POST");
                httpConnect.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
                httpConnect.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
                httpConnect.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
                httpConnect.setRequestProperty("Accept-Encoding", "gzip, deflate");
                httpConnect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                httpConnect.connect();
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(request);
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                httpConnect.disconnect();

                JSONParser parser = new JSONParser();
                Object object;
                try {
                    object = parser.parse(response.toString());
                    JSONObject jsonobj = (JSONObject) object;
                    String html = jsonobj.get("html").toString();
                    return html;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


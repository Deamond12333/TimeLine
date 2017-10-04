package comdeamond18.vk.timeline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static android.R.layout.simple_spinner_item;
import static java.net.URLEncoder.encode;

public class Teachers extends Activity
{
    String urlParameters;
    Spinner abcSpinner;
    Spinner teachersSpinner;
    ArrayList<Group> teachers;
    String[] abcHex;
    SharedPreferences save;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Resources resources = getResources();
        String[] abc = resources.getStringArray(R.array.abc);
        abcHex = resources.getStringArray(R.array.abchex);

        abcSpinner = (Spinner) findViewById(R.id.abc);
        ArrayAdapter<String> abcAdapter = new ArrayAdapter<>(getBaseContext(), simple_spinner_item, abc);
        abcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        abcSpinner.setAdapter(abcAdapter);
        abcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position != 0)
                {
                    urlParameters = "action=get_lecturers&letter=%" + abcHex[position];
                    getTeachers teachers = new getTeachers();
                    teachers.execute(urlParameters);
                } else return;
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private class getTeachers extends AsyncTask<String, Void, ArrayList<Group>>
    {
        public ProgressDialog progress;
        Context ctx = Teachers.this;

        protected void onPreExecute()
        {
            super.onPreExecute();

            progress = new ProgressDialog(ctx);
            progress.setMessage("Загрузка...");
            progress.setIndeterminate(true);
            progress.setCancelable(true);
            progress.show();
        }

        protected ArrayList<Group> doInBackground(String... params)
        {
            if (isOnline())
            {
                try
                {
                    URL url = new URL("http://www.omgtu.ru/students/temp/ajax.php?"+params[0]);
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
                        wr.writeBytes(params[0]);
                        wr.flush();
                        wr.close();

                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null)
                        {
                            response.append(inputLine);
                        }
                        in.close();
                        httpConnect.disconnect();

                        JSONParser parser = new JSONParser();
                        Object object;
                        try
                        {
                            object = parser.parse(response.toString());
                            JSONObject jsonobj = (JSONObject) object;
                            JSONArray list = (JSONArray) jsonobj.get("list");
                            teachers = new ArrayList<>();
                            for (int i = 0; i < list.size(); ++i)
                            {
                                JSONObject jsonGroup = (JSONObject) list.get(i);
                                Group teacher = new Group();
                                teacher.number = jsonGroup.get("fio").toString();
                                teacher.groupOid = Integer.parseInt(jsonGroup.get("lecturerOid").toString());
                                teachers.add(teacher);
                            }
                            return teachers;
                        }
                        catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(final ArrayList<Group> groups)
        {
            super.onPostExecute(groups);
            progress.dismiss();
            if (groups == null) Toast.makeText(getBaseContext(), "Проверьте подключение к интернету...", Toast.LENGTH_LONG).show();
            else
            {
                String[] adapt = new String[groups.size()+1];
                adapt[0] = "...";
                for (int i = 1; i < groups.size()+1; ++i)
                    adapt[i] = groups.get(i-1).number;
                teachersSpinner = (Spinner) findViewById(R.id.teachers);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Teachers.this, simple_spinner_item, adapt);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                teachersSpinner.setAdapter(adapter);

                teachersSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
                {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        if (position == 0) return;
                        urlParameters = new String();
                        try
                        {
                            urlParameters = encode("filter[type]", "UTF-8") + "=l&" + encode("filter[course]", "UTF-8") + "=&" + encode("filter[lecturerOid]", "UTF-8") + "=" + teachers.get(teachersSpinner.getSelectedItemPosition() - 1).groupOid + "&" + encode("filter[auditoriumOid]", "UTF-8") + "=&" + encode("filter[fromDate]", "UTF-8") + "=&" + encode("filter[toDate]", "UTF-8") + "=";
                        }
                        catch (UnsupportedEncodingException e)
                        {
                            e.printStackTrace();
                        }

                        getSchedule groups = new getSchedule();
                        groups.execute(urlParameters);
                    }

                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });
            }
        }
    }

    private class getSchedule extends AsyncTask<String, Void, String>
    {
        public ProgressDialog progress;
        Context ctx = Teachers.this;

        protected void onPreExecute()
        {
            super.onPreExecute();

            progress = new ProgressDialog(ctx);
            progress.setMessage("Загрузка...");
            progress.setIndeterminate(true);
            progress.setCancelable(true);
            progress.show();
        }

        protected String doInBackground(String... params)
        {
            if (isOnline()) return new getTimeline().getHtml(params[0]);
            else return null;
        }

        protected void onPostExecute(String html)
        {
            super.onPostExecute(html);
            progress.dismiss();
            if (html == null) Toast.makeText(getBaseContext(), "Проверьте подключение к интернету...", Toast.LENGTH_LONG).show();
            else
            {
                save = getSharedPreferences("Schedule", MODE_PRIVATE);
                SharedPreferences.Editor edit = save.edit();
                ArrayList<AcademDay> schedule = (new Parser()).parseHTML(html);
                edit.putBoolean("isFirstStart", false);
                edit.putString("request", urlParameters);
                edit.putString("group", teachersSpinner.getSelectedItem().toString());
                edit.putInt("dayInWeek", schedule.size());
                for (int i = 1; i <= schedule.size(); ++i)
                {
                    AcademDay today = schedule.get(i - 1);

                    edit.putString("date" + i, today.date);
                    edit.putString("dayOfWeek" + i, today.dayOfWeek);
                    ArrayList<Subject> subjects = schedule.get(i - 1).subjects;
                    for (int j = 1; j <= subjects.size(); ++j)
                    {
                        Subject subject = subjects.get(j - 1);
                        edit.putString("time" + i + " " + j, subject.time);
                        edit.putString("subject" + i + " " + j, subject.subject);
                    }
                    edit.putInt("SubjectCount" + i, subjects.size());
                }
                edit.commit();

                if (save.getBoolean("isFirstStart", false)) startActivity(new Intent(Teachers.this, Schedule.class));
                finish();
            }
        }
    }

    private boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) return false;
        else return true;
    }
}

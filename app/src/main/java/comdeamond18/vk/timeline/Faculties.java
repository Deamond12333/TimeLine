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
import android.widget.RadioGroup;
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

public class Faculties extends Activity
{
    Spinner selectGroup;
    int course = 0;
    ArrayList<Group> groups;
    String urlParameters;
    Spinner selectFaculty;
    SharedPreferences save;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculties);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Resources resources = getResources();
        String[] faculties = resources.getStringArray(R.array.faculties);

        selectFaculty = (Spinner) findViewById(R.id.faculties);
        final RadioGroup selectCourse = (RadioGroup) findViewById(R.id.courses);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, simple_spinner_item, faculties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectFaculty.setAdapter(adapter);
        selectFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (course != 0)
                {
                    urlParameters = new String();
                    try
                    {
                        urlParameters = encode("filter[type]", "UTF-8") + "=g&" + encode("filter[faculty]", "UTF-8") + "=" + encode(selectFaculty.getSelectedItem().toString(), "UTF-8") + "&" + encode("filter[course]", "UTF-8") + "=&" + encode("filter[course]", "UTF-8") + "=" + course + "&" + encode("filter[lecturerOid]", "UTF-8") + "=&" + encode("filter[auditoriumOid]", "UTF-8") + "=&" + encode("filter[fromDate]", "UTF-8") + "=&" + encode("filter[toDate]", "UTF-8") + "=";
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }

                    getGroups groups = new getGroups();
                    groups.execute(urlParameters);
                } else return;
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        selectCourse.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.course1:
                        course = 1;
                        break;
                    case R.id.course2:
                        course = 2;
                        break;
                    case R.id.course3:
                        course = 3;
                        break;
                    case R.id.course4:
                        course = 4;
                        break;
                    case R.id.course5:
                        course = 5;
                        break;
                    case R.id.course6:
                        course = 6;
                        break;
                    default:
                        break;
                }

                if (selectFaculty.getSelectedItemPosition() == 0) {
                    Toast.makeText(getBaseContext(), "Выберите факультет!", Toast.LENGTH_LONG).show();
                    return;
                }
                urlParameters = new String();
                try {
                    urlParameters = encode("filter[type]", "UTF-8") + "=g&" + encode("filter[faculty]", "UTF-8") + "=" + encode(selectFaculty.getSelectedItem().toString(), "UTF-8") + "&" + encode("filter[course]", "UTF-8") + "=&" + encode("filter[course]", "UTF-8") + "=" + course + "&" + encode("filter[lecturerOid]", "UTF-8") + "=&" + encode("filter[auditoriumOid]", "UTF-8") + "=&" + encode("filter[fromDate]", "UTF-8") + "=&" + encode("filter[toDate]", "UTF-8") + "=";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                getGroups groups = new getGroups();
                groups.execute(urlParameters);
            }
        });

    }



    public class getGroups extends AsyncTask<String, Void, ArrayList<Group>>
    {
        public ProgressDialog progress;
        Context ctx = Faculties.this;

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
                    URL url = new URL("http://www.omgtu.ru/students/temp/ajax.php?action=get_groups");
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
                            groups = new ArrayList<>();
                            for (int i = 0; i < list.size(); ++i)
                            {
                                JSONObject jsonGroup = (JSONObject) list.get(i);
                                Group group = new Group();
                                group.number = jsonGroup.get("number").toString();
                                group.groupOid = Integer.parseInt(jsonGroup.get("groupOid").toString());
                                groups.add(group);
                            }
                            return groups;
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
                selectGroup = (Spinner) findViewById(R.id.groups);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Faculties.this, simple_spinner_item, adapt);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectGroup.setAdapter(adapter);

                selectGroup.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
                {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        if (position == 0) return;
                        urlParameters = new String();
                        try
                        {
                            urlParameters = encode("filter[type]", "UTF-8") + "=g&" + encode("filter[faculty]", "UTF-8") + "=" + encode(selectFaculty.getSelectedItem().toString(), "UTF-8") + "&" + encode("filter[course]", "UTF-8") + "=&" + encode("filter[course]", "UTF-8") + "=" + course + "&" + encode("filter[groupOid]", "UTF-8") + "=" + groups.get(position-1).groupOid + "&" + encode("filter[lecturerOid]", "UTF-8") + "=&" + encode("filter[auditoriumOid]", "UTF-8") + "=&" + encode("filter[fromDate]", "UTF-8") + "=&" + encode("filter[toDate]", "UTF-8") + "=";
                        }
                        catch (UnsupportedEncodingException e)
                        {
                            e.printStackTrace();
                        }
                        getSchedule schedule = new getSchedule();
                        schedule.execute(urlParameters);
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
        private ProgressDialog progress;
        Context ctx = Faculties.this;

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
                edit.putString("group", selectGroup.getSelectedItem().toString());
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
                        edit.putString("subject" + i + " " + j, " "+subject.subject);
                    }
                    edit.putInt("SubjectCount" + i, subjects.size());
                }
                edit.commit();

                if (save.getBoolean("isFirstStart", false)) startActivity(new Intent(Faculties.this, Schedule.class));
                finish();
            }
        }
    }

    private boolean isOnline()
    {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) return false;
        else return true;
    }
}

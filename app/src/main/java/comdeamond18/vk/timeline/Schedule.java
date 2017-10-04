package comdeamond18.vk.timeline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Schedule extends Activity
{
    SharedPreferences save;
    ArrayList<AcademDay> schedule;
    ImageButton update, about, calendar;
    Button menu;
    ListView scheduleList;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        save = getSharedPreferences("Schedule", MODE_PRIVATE);

        menu = (Button) findViewById(R.id.menu);
        update = (ImageButton) findViewById(R.id.update);
        about = (ImageButton) findViewById(R.id.about);
        calendar = (ImageButton) findViewById(R.id.calendar);
        scheduleList = (ListView) findViewById(R.id.scheduleList);

        refresh();

        menu.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(getBaseContext(), HomeTabs.class);
                startActivityForResult(intent, 1);
            }
        });


        calendar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CalendarActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        update.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                getSchedule schedule = new getSchedule();
                schedule.execute(save.getString("request", null));
                refresh();
            }
        });

        about.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getBaseContext(), About.class));
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        refresh();
    }

    public class SubjectAdapter extends ArrayAdapter<Subject>
    {
        private final Context context;
        private final ArrayList<Subject> values;

        public SubjectAdapter(Context context, ArrayList<Subject> values)
        {
            super(context, R.layout.subject_layout, values);
            this.context = context;
            this.values = values;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.subject_layout, parent, false);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            time.setText(values.get(position).time);
            TextView subject = (TextView) convertView.findViewById(R.id.subject);
            subject.setText(values.get(position).subject);
            return convertView;
        }
    }

    public class DayAdapter extends ArrayAdapter<AcademDay>
    {
        private final Context context;
        private final ArrayList<AcademDay> values;

        public DayAdapter(Context context, ArrayList<AcademDay> values)
        {
            super(context, R.layout.day_layout, values);
            this.context = context;
            this.values = values;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.day_layout, parent, false);
            TextView daydate = (TextView) convertView.findViewById(R.id.daydate);
            daydate.setText(values.get(position).date);
            TextView dayofweek = (TextView) convertView.findViewById(R.id.dayofweek);
            dayofweek.setText(values.get(position).dayOfWeek);

            LinearLayout subjects = (LinearLayout) convertView.findViewById(R.id.subjects);

            SubjectAdapter subjectAdapter = new SubjectAdapter(Schedule.this, values.get(position).subjects);
            for (int i = 0; i < subjectAdapter.getCount(); ++i)
            {
                subjects.addView(subjectAdapter.getView(i, new LinearLayout(getBaseContext()), subjects));
            }
            return convertView;
        }
    }

    private class getSchedule extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progress;
        Context ctx = Schedule.this;

        protected void onPreExecute()
        {
            super.onPreExecute();

            progress = new ProgressDialog(ctx);
            progress.setMessage("Обновление...");
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
                refresh();
            }
        }
    }

    private void refresh()
    {
        schedule = new ArrayList<>();

        for (int i = 1; i <= save.getInt("dayInWeek", 0); ++i)
        {
            AcademDay today = new AcademDay();
            today.date = save.getString("date" + i, null);
            today.dayOfWeek = save.getString("dayOfWeek" + i, null);
            ArrayList<Subject> subjects = new ArrayList<>();
            for (int j = 1; j <= save.getInt("SubjectCount" + i, 2); ++j)
            {
                Subject subject = new Subject();
                subject.time = save.getString("time" + i + " " + j, null);
                subject.subject = save.getString("subject" + i + " " + j, null);
                subjects.add(subject);
            }
            today.subjects = subjects;
            schedule.add(today);
        }

        menu.setText(save.getString("group", null));
        DayAdapter adapter = new DayAdapter(this, schedule);
        scheduleList.setAdapter(adapter);
    }

    private boolean isOnline()
    {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) return false;
        else return true;
    }
}

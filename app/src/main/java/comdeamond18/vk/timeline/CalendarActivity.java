package comdeamond18.vk.timeline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class CalendarActivity extends Activity
{
    String todayOfWeek, todayMonth;
    int today;
    int todayYear;
    ArrayList<ArrayList<AcademDate>> month;
    AcademDate date;
    Month qurMonth;

    ArrayList<AcademDate> weekDays;

    String[] week, year, yearRus;
    int[] monthDayCount;


    TextView monthText;
    ListView calendar;
    Button monthNext, monthLast;
    WeekAdapter adapter;

    SharedPreferences save;

    int todayBuf, indexOfMonth, yearBuf, indexOfWeekDay;
    boolean isThisMonth;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Date cal = java.util.Calendar.getInstance().getTime();
        String dt = cal.toString();

        todayOfWeek = dt.substring(0, 3);
        todayMonth = dt.substring(4, 7);
        today = Integer.parseInt(dt.substring(8, 10));
        todayYear = Integer.parseInt(dt.substring(dt.length() - 4, dt.length()));

        Resources r = getResources();

        week = r.getStringArray(R.array.week);
        year = r.getStringArray(R.array.year);
        yearRus = r.getStringArray(R.array.yearRus);

        monthDayCount = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (todayYear % 4 == 0) monthDayCount[1] = 29;

        month = new ArrayList<>();
        weekDays = new ArrayList<>();
        qurMonth = new Month();

        date = new AcademDate();
        date.date = today;
        date.month = indexOfMonth(todayMonth);
        date.year = todayYear;
        date.isThisMonth = true;
        date.isToday = true;

        weekDays.add(0, date);

        todayBuf = today;//текущее число
        indexOfMonth = indexOfMonth(todayMonth);//текущий месяц
        yearBuf = todayYear;//текущийй год
        indexOfWeekDay = indexOfWeekDay(todayOfWeek);//день текущей недели

        if (indexOfWeekDay == 1)
        {
            month.add(0, weekDays);
            weekDays = new ArrayList<>();
            indexOfWeekDay = 8;
        }

        isThisMonth = true;

        while (true)
        {
            date = new AcademDate();
            todayBuf -= 1;

            if (todayBuf == 0)//если месяц закончился
            {
                isThisMonth = false;

                if (indexOfWeekDay == 1)//и неделя тоже закончилась
                {
                    month.add(0, weekDays);
                    break;
                }
                else //если неделя еще не закончилась
                {
                    indexOfMonth -= 1;
                    if (indexOfMonth == 0)//если год закончился
                    {
                        indexOfMonth = 12;
                        yearBuf -= 1;
                    }
                    todayBuf = monthDayCount[indexOfMonth - 1];//вычисляем число дней в следующем месяце
                }
            }

            date.date = todayBuf;
            date.month = indexOfMonth;
            date.year = yearBuf;
            date.isThisMonth = isThisMonth;
            date.isToday = false;
            weekDays.add(0, date);

            indexOfWeekDay -= 1;
            if (indexOfWeekDay == 1)
            {
                month.add(0, weekDays);
                weekDays = new ArrayList<>();
                if (isThisMonth == false) break;
                else indexOfWeekDay = 8;
            }
        }

        todayBuf = today;//текущее число
        indexOfMonth = indexOfMonth(todayMonth);//текущий месяц
        yearBuf = todayYear;//текущиий год
        indexOfWeekDay = indexOfWeekDay(todayOfWeek);//день текущей недели
        isThisMonth = true;

        if (indexOfWeekDay == 7)
        {
            weekDays = new ArrayList<>();
            indexOfWeekDay = 0;
        }
        else
        {
            weekDays = month.get(month.size()-1);
            month.remove(month.size() - 1);
        }

        while (true)
        {
            date = new AcademDate();
            todayBuf += 1;

            if (todayBuf == monthDayCount[indexOfMonth - 1]+1)//если месяц закончился
            {
                isThisMonth = false;

                if (indexOfWeekDay == 7)//и неделя тоже закончилась
                {
                    month.add(weekDays);
                    break;
                }
                else //если неделя еще не закончилась
                {
                    indexOfMonth += 1;
                    if (indexOfMonth == 12)//если год закончился
                    {
                        indexOfMonth = 1;
                        yearBuf += 1;
                    }
                    todayBuf = 1;//вычисляем число дней в следующем месяце
                }
            }

            date.date = todayBuf;
            date.month = indexOfMonth;
            date.year = yearBuf;
            date.isThisMonth = isThisMonth;
            date.isToday = false;
            weekDays.add(date);


            indexOfWeekDay += 1;
            if (indexOfWeekDay == 7)
            {
                month.add(weekDays);
                weekDays = new ArrayList<>();
                if (isThisMonth == false) break;
                else indexOfWeekDay = 0;
            }
        }

        qurMonth.days = month;
        qurMonth.monthName = yearRus[indexOfMonth(todayMonth) - 1];

        calendar = (ListView) findViewById(R.id.calendar);
        monthText = (TextView) findViewById(R.id.monthText);
        adapter = new WeekAdapter(CalendarActivity.this, month);
        monthText.setText(qurMonth.monthName + ", " + month.get(2).get(3).year);
        calendar.setAdapter(adapter);
        calendar.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                save = getSharedPreferences("Schedule", MODE_PRIVATE);
                String request = save.getString("request", null);
                int l = save.getString("request", null).length();
                request = request.substring(0, l-20)+qurMonth.days.get(position).get(0).date+"."+qurMonth.days.get(position).get(0).month+"."+qurMonth.days.get(position).get(0).year+"&"+request.substring(l - 19, l)+qurMonth.days.get(position).get(6).date+"."+qurMonth.days.get(position).get(6).month+"."+qurMonth.days.get(position).get(6).year;
                getSchedule schedule = new getSchedule();
                schedule.execute(request);
            }
        });

        monthNext = (Button) findViewById(R.id.buttonNext);
        monthNext.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                weekDays = month.get(month.size() - 1);
                month = new ArrayList<>();

                for (int i = 0; i < weekDays.size(); ++i)
                {
                    weekDays.get(i).isThisMonth = !weekDays.get(i).isThisMonth;
                }
                month.add(weekDays);

                todayBuf = weekDays.get(weekDays.size() - 1).date;//текущее число
                indexOfMonth = weekDays.get(weekDays.size() - 1).month;//текущий месяц
                qurMonth.monthName = yearRus[indexOfMonth - 1];
                yearBuf = weekDays.get(weekDays.size() - 1).year;//текущиий год
                indexOfWeekDay = 0;//день текущей недели
                isThisMonth = true;
                weekDays = new ArrayList<>();

                while (true)
                {
                    date = new AcademDate();
                    todayBuf += 1;

                    if (todayBuf == monthDayCount[indexOfMonth - 1]+1)//если месяц закончился
                    {
                        isThisMonth = false;

                        if (indexOfWeekDay == 7)//и неделя тоже закончилась
                        {
                            month.add(weekDays);
                            break;
                        }
                        else //если неделя еще не закончилась
                        {
                            indexOfMonth += 1;
                            if (indexOfMonth == 13)//если год закончился
                            {
                                indexOfMonth = 1;
                                yearBuf += 1;
                            }
                            todayBuf = 1;//вычисляем число дней в следующем месяце
                        }
                    }

                    date.date = todayBuf;
                    date.month = indexOfMonth;
                    date.year = yearBuf;
                    if (todayBuf == today && year[indexOfMonth - 1].equals(todayMonth) && yearBuf == todayYear) date.isToday = true;
                    else date.isToday = false;
                    date.isThisMonth = isThisMonth;
                    weekDays.add(date);


                    indexOfWeekDay += 1;
                    if (indexOfWeekDay == 7)
                    {
                        month.add(weekDays);
                        weekDays = new ArrayList<>();
                        if (isThisMonth == false) break;
                        else indexOfWeekDay = 0;
                    }
                }
                qurMonth.days = month;
                adapter = new WeekAdapter(CalendarActivity.this, qurMonth.days);
                calendar.setAdapter(adapter);
                monthText.setText(qurMonth.monthName+", "+month.get(2).get(3).year);
            }
        });

        monthLast = (Button) findViewById(R.id.buttonLast);
        monthLast.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                weekDays = month.get(0);
                month = new ArrayList<>();

                for (int i = 0; i < weekDays.size(); ++i)
                {
                    weekDays.get(i).isThisMonth = !weekDays.get(i).isThisMonth;
                }
                month.add(0, weekDays);

                todayBuf = weekDays.get(0).date;//текущее число
                indexOfMonth = weekDays.get(0).month;//текущий месяц
                qurMonth.monthName = yearRus[indexOfMonth - 1];
                yearBuf = weekDays.get(0).year;//текущиий год
                indexOfWeekDay = 8;//день текущей недели
                isThisMonth = true;
                weekDays = new ArrayList<>();

                while (true)
                {
                    date = new AcademDate();
                    todayBuf -= 1;

                    if (todayBuf == 0)//если месяц закончился
                    {
                        isThisMonth = false;

                        if (indexOfWeekDay == 1)//и неделя тоже закончилась
                        {
                            month.add(0, weekDays);
                            break;
                        }
                        else //если неделя еще не закончилась
                        {
                            indexOfMonth -= 1;
                            if (indexOfMonth == 0)//если год закончился
                            {
                                indexOfMonth = 12;
                                yearBuf -= 1;
                            }
                            todayBuf = monthDayCount[indexOfMonth - 1];//вычисляем число дней в следующем месяце
                        }
                    }

                    date.date = todayBuf;
                    date.month = indexOfMonth;
                    date.year = yearBuf;
                    if (todayBuf == today && year[indexOfMonth - 1].equals(todayMonth) && yearBuf == todayYear) date.isToday = true;
                    else date.isToday = false;
                    date.isThisMonth = isThisMonth;
                    weekDays.add(0, date);

                    indexOfWeekDay -= 1;
                    if (indexOfWeekDay == 1)
                    {
                        month.add(0, weekDays);
                        weekDays = new ArrayList<>();
                        if (isThisMonth == false) break;
                        else indexOfWeekDay = 8;
                    }
                }

                qurMonth.days = month;
                adapter = new WeekAdapter(CalendarActivity.this, qurMonth.days);
                calendar.setAdapter(adapter);
                monthText.setText(qurMonth.monthName+", "+month.get(2).get(3).year);
            }
        });
    }

    private int indexOfWeekDay(String dayOfWeek)
    {
        for (int i = 0; i < week.length; ++i)
        {
            if (week[i].equals(dayOfWeek)) return i+1;
        }
        return -1;
    }

    private int indexOfMonth(String month)
    {
        for (int i = 0; i < year.length; ++i)
        {
            if (year[i].equals(month)) return i+1;
        }
        return -1;
    }

    public class WeekAdapter extends ArrayAdapter<ArrayList<AcademDate>>
    {
        private final Context context;
        private final ArrayList<ArrayList<AcademDate>> values;

        public WeekAdapter(Context context, ArrayList<ArrayList<AcademDate>> values)
        {
            super(context, R.layout.week_layout, values);
            this.context = context;
            this.values = values;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.week_layout, parent, false);

            TextView mon = (TextView) convertView.findViewById(R.id.day1);
            TextView tue = (TextView) convertView.findViewById(R.id.day2);
            TextView wed = (TextView) convertView.findViewById(R.id.day3);
            TextView thu = (TextView) convertView.findViewById(R.id.day4);
            TextView fri = (TextView) convertView.findViewById(R.id.day5);
            TextView sat = (TextView) convertView.findViewById(R.id.day6);
            TextView sun = (TextView) convertView.findViewById(R.id.day7);

            mon.setText(values.get(position).get(0).date+"");
            tue.setText(values.get(position).get(1).date+"");
            wed.setText(values.get(position).get(2).date+"");
            thu.setText(values.get(position).get(3).date+"");
            fri.setText(values.get(position).get(4).date+"");
            sat.setText(values.get(position).get(5).date+"");
            sun.setText(values.get(position).get(6).date+"");

            if (values.get(position).get(0).isToday)
            {
                mon.setBackgroundResource(R.drawable.calendar_today);
                mon.setTextColor(Color.WHITE);
            }
            if (values.get(position).get(1).isToday)
            {
                tue.setBackgroundResource(R.drawable.calendar_today);
                tue.setTextColor(Color.WHITE);
            }
            if (values.get(position).get(2).isToday)
            {
                wed.setBackgroundResource(R.drawable.calendar_today);
                wed.setTextColor(Color.WHITE);
            }
            if (values.get(position).get(3).isToday)
            {
                thu.setBackgroundResource(R.drawable.calendar_today);
                thu.setTextColor(Color.WHITE);
            }
            if (values.get(position).get(4).isToday)
            {
                fri.setBackgroundResource(R.drawable.calendar_today);
                fri.setTextColor(Color.WHITE);
            }
            if (values.get(position).get(5).isToday)
            {
                sat.setBackgroundResource(R.drawable.calendar_today);
                sat.setTextColor(Color.WHITE);
            }
            if (values.get(position).get(6).isToday)
            {
                sun.setBackgroundResource(R.drawable.calendar_today);
                sun.setTextColor(Color.WHITE);
            }

            if (!values.get(position).get(0).isThisMonth) mon.setTextColor(Color.GRAY);
            if (!values.get(position).get(1).isThisMonth) tue.setTextColor(Color.GRAY);
            if (!values.get(position).get(2).isThisMonth) wed.setTextColor(Color.GRAY);
            if (!values.get(position).get(3).isThisMonth) thu.setTextColor(Color.GRAY);
            if (!values.get(position).get(4).isThisMonth) fri.setTextColor(Color.GRAY);
            if (!values.get(position).get(5).isThisMonth) sat.setTextColor(Color.GRAY);
            if (!values.get(position).get(6).isThisMonth) sun.setTextColor(Color.GRAY);

            return convertView;
        }
    }

    private class getSchedule extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progress;
        Context ctx = CalendarActivity.this;

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
                edit.putInt("dayInWeek", schedule.size());
                for (int i = 1; i <= schedule.size(); ++i)
                {
                    AcademDay today = schedule.get(i - 1);
                    edit.putString("date" + i, today.date);
                    edit.putString("dayOfWeek" + i, today.dayOfWeek);
                    ArrayList<Subject> subjects = schedule.get(i - 1).subjects;
                    for (int j = 1; j < subjects.size() + 1; ++j) {
                        Subject subject = subjects.get(j - 1);
                        edit.putString("time" + i + " " + j, subject.time);
                        edit.putString("subject" + i + " " + j, subject.subject);
                    }
                    edit.putInt("SubjectCount" + i, subjects.size());
                }
                edit.commit();
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

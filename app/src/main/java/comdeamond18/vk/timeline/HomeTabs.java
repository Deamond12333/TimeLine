package comdeamond18.vk.timeline;

import android.app.ActivityGroup;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TabHost;

public class HomeTabs extends ActivityGroup implements View.OnTouchListener
{
    float prev_x, x;
    TabHost tabs;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tabs);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tabs = (TabHost) findViewById(R.id.tabHost);
        tabs.setup(this.getLocalActivityManager());

        TabHost.TabSpec tabSpec;

        tabSpec = tabs.newTabSpec("tag1");
        tabSpec.setIndicator("Препод.\nСостав");
        tabSpec.setContent(new Intent(this, Teachers.class));
        tabs.addTab(tabSpec);

        tabSpec = tabs.newTabSpec("tag2");
        tabSpec.setIndicator("Факультет");
        tabSpec.setContent(new Intent(this, Faculties.class));
        tabs.addTab(tabSpec);

        tabSpec = tabs.newTabSpec("tag3");
        tabSpec.setIndicator("Аудитории");
        tabSpec.setContent(new Intent(this, Rooms.class));
        tabs.addTab(tabSpec);

        tabs.setCurrentTab(1);
        tabs.setOnTouchListener(this);
    }

    private void swipeUp()
    {
        switch(tabs.getCurrentTab())
        {
            case 0: tabs.setCurrentTab(1); break;
            case 1: tabs.setCurrentTab(2); break;
            default:break;
        }
    }

    private void swipeDown()
    {
        switch(tabs.getCurrentTab())
        {
            case 2: tabs.setCurrentTab(1); break;
            case 1: tabs.setCurrentTab(0); break;
            default:break;
        }
    }

    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN: // нажатие
                prev_x = event.getX(); break;
            case MotionEvent.ACTION_MOVE: break; // движение
            case MotionEvent.ACTION_UP: //отпускание
            {
                x = event.getX();
                if ((x - prev_x) > 200)
                {
                    swipeDown();
                    return true;
                }
                else if ((x - prev_x) < -200)
                {
                    swipeUp();
                    return true;
                }
                else break;
            }
        }
        return true;
    }
}

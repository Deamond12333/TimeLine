package comdeamond18.vk.timeline;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class Splash extends Activity
{
    SharedPreferences save;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Thread logoTimer = new Thread()
        {
            public void run()
            {
                try
                {
                    int logoTimer = 0;
                    while(logoTimer < 1000)
                    {
                        sleep(100);
                        logoTimer = logoTimer + 100;
                    }

                    save = getSharedPreferences("Schedule", MODE_PRIVATE);
                    if (!save.getBoolean("isFirstStart", true)) startActivity(new Intent(Splash.this, Schedule.class));
                    else startActivity(new Intent(Splash.this, HomeTabs.class));
                }
                catch (InterruptedException e)
                {
                    e.getStackTrace();
                }
                finally
                {
                    finish();
                }
            }
        };
        logoTimer.start();
    }
}

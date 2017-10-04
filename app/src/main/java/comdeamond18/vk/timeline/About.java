package comdeamond18.vk.timeline;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class About extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button vk = (Button) findViewById(R.id.vk);
        vk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/deamond1996"));
                startActivity(browserIntent);
            }
        });

        Button omstu = (Button) findViewById(R.id.omstu);
        omstu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.omgtu.ru/"));
                startActivity(browserIntent);
            }
        });
    }
}

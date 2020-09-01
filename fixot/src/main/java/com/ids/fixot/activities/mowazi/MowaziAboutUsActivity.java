package com.ids.fixot.activities.mowazi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.mowazi.MowaziCompany;

import java.util.Calendar;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziAboutUsActivity extends AppCompatActivity {


    WebView about;
    ProgressBar progress;
    MowaziCompany company;
    private RelativeLayout main;
    private ImageButton back;

    public MowaziAboutUsActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mowazi_about_us);

        progress = (ProgressBar) findViewById(R.id.progressbar);
        company = getIntent().getExtras().getParcelable("company");
        TextView companyname = (TextView) findViewById(R.id.companyname);
        main = (RelativeLayout) findViewById(R.id.rlMain);
        back = (ImageButton) findViewById(R.id.back);

        ImageButton menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        about = (WebView) findViewById(R.id.aboutwebview);

        about.getSettings().setUseWideViewPort(true);
        about.getSettings().setLoadWithOverviewMode(true);
        about.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View v) {
                return true;
            }
        });

        about.setHapticFeedbackEnabled(false);

//        https://www.almowazi.com/TestPublic/MobileCompnayProfile.aspx?ID=223&language=ar

//        if (MyApplication.lang == MyApplication.ARABIC) {
//
//            companyname.setText(getResources().getString(R.string.mowazi_about) + "-" + company.getSymbolAr());
//
//            String desc = company.getDescAr();
//            if (desc.equals(""))
//                desc = getString(R.string.mowazi_no_details);
//
//            String des = "<html><head><meta name='viewport' device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;'/></head></body>"
//                    + company.getDescAr() + "</body></html>";
//
//            about.loadDataWithBaseURL("file:///android_asset/",
//                    "<html><head>\n"
//                            + "<style type=\"text/css\">\n"
//                            + "@font-face {\n"
//                            + "    font-family: MyFont;\n"
//                            +
//                            // "    src: url(\"file:///android_asset/GE_Dinar_Two_Medium.otf\")\n"
//                            // +
//                            "    src: url(\"file:///android_asset/DroidKufiRegular.ttf\")\n"
//                            + "}\n" + "body {\n" + "    font-family: MyFont;\n"
//                            + "    font-size: 10;\n" + "}\n"
//                            + "</style><body><div align=\"center\">" + des
//                            + "</div></body></html>", "text/html", "UTF-8", "");
//
//
//            about.getSettings().setUseWideViewPort(true);
//            about.getSettings().setLoadWithOverviewMode(true);
//        } else {
//            companyname.setText(getResources().getString(R.string.mowazi_about) + "-"  + company.getSymbolEn());
//            companyname.setTypeface(MyApplication.opensansbold);
//
//            String desc = company.getDescEn();
//            if (desc.equals(""))
//                desc = getString(R.string.mowazi_no_details);
//
//            String des = "<html><head><meta name='viewport' device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;'/></head></body>"
//                    + desc + "</body></html>";
//            about.loadDataWithBaseURL("", des, "text/html", "UTF-8", "");
//
//
//        }

        String url = "https://www.almowazi.com/TestPublic/MobileCompnayProfile.aspx?ID=";
        String companyId = String.valueOf(company.getCompanyId());
        url += companyId + (MyApplication.lang == MyApplication.ARABIC ? "&language=ar" : "&language=en");

        about.loadUrl(url);

        about.getSettings().setUseWideViewPort(true);
        about.getSettings().setLoadWithOverviewMode(true);


        Actions.overrideFonts(this, main, true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void back(View v) {
        finish();
    }
}

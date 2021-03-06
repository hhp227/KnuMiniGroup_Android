package com.hhp227.knu_minigroup.fragment;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.hhp227.knu_minigroup.R;
import com.hhp227.knu_minigroup.app.AppController;
import com.hhp227.knu_minigroup.app.EndPoint;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class BTLDormMealFragment extends Fragment {
    private static final String TAG = "BTL 식단표";

    private TextView[] mMenuView;

    public static BTLDormMealFragment newInstance() {
        return new BTLDormMealFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dormmeal, container, false);
        mMenuView = new TextView[] {
                rootView.findViewById(R.id.breakfast),
                rootView.findViewById(R.id.lunch),
                rootView.findViewById(R.id.dinner)
        };
        String endPoint = EndPoint.URL_KNU_DORM_MEAL.replace("{ID}", "3");

        AppController.getInstance().addToRequestQueue(new StringRequest(Request.Method.GET, endPoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<String> arrayList = new ArrayList<>();

                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();

                    parser.setInput(new StringReader(response));
                    int eventType = parser.getEventType();

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                String startTag = parser.getName();
                                if (startTag.equals("data"))
                                    arrayList.add(new SpannableString(Html.fromHtml(parser.nextText())).toString());
                                break;
                        }
                        eventType = parser.next();
                    }
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < mMenuView.length; i++)
                    mMenuView[i].setText(arrayList.size() > 0 ? arrayList.get(i) : "등록된 식단이 없습니다.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, error.getMessage());
            }
        }));
        return rootView;
    }
}

package com.ids.fixot.model.mowazi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class MoaziViewResizing {
    public static int screenWidth;
    public static Typeface faceLight;

    public MoaziViewResizing(int screenWidth, Typeface faceLight) {
        MoaziViewResizing.screenWidth = screenWidth;
        MoaziViewResizing.faceLight = faceLight;
    }
    public static void setTabletPageTextResizing(Activity activity, String lang) {
        final ViewGroup mContainer = (ViewGroup) activity.findViewById(
                android.R.id.content).getRootView();
        try {
            MoaziViewResizing.setTabViewGroupContentTextSize(activity, mContainer,lang/*
																		 * ,
																		 * app.
																		 * XLARGE
																		 */);

        } catch (NoSuchFieldError e) {

        }
    }
    public static void setTabletListRowTextResizing(View view, Activity activity, String lang) {
        final ViewGroup mContainer = (ViewGroup) view.getRootView();
        try {
            MoaziViewResizing.setTabViewGroupContentTextSize(activity, mContainer,lang/*
                   * ,
                   * app.
                   * XLARGE
                   */);

        } catch (NoSuchFieldError e) {

        }
    }
    public static void setTabViewGroupContentTextSize(Activity activity,
                                                      ViewGroup view, String lang) {
        ViewGroup viewGroup = view;

        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                setTabViewGroupContentTextSize(activity,
                        (ViewGroup) viewGroup.getChildAt(i),lang);
            } else {
                try {

                    final TextView text = (TextView) viewGroup.getChildAt(i);
                    int size = (int) text.getTextSize();
                    if(lang.equals("en"))
                        size+=2;
                    tabTextResize(activity, text, size);

                } catch (Exception e) {

                }
            }
        }
    }

     public static void tabTextResize(Context activity, TextView textView,
                                      int sizePx) {

        // calculate perscentage according 480 px width screen
        double px = convertPixelsToDp(sizePx, activity) * 1.5;
        double percentage = px / 480;
        // float x=textView.getTextSize();
        float f = textView.getTextSize();

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (float) ((screenWidth - 220) * percentage));

        float ff = textView.getTextSize();
        // to force my font , not use font from device
        //   moved to launcher activity

        textView.setTypeface(faceLight);

        // float y=textView.getTextSize();
        // TextView v=textView;

    }
//    public static void setTabletPageTextResizing(Activity activity,String lang) {
//        final ViewGroup mContainer = (ViewGroup) activity.findViewById(
//                android.R.id.content).getRootView();
//        try {
//            MoaziViewResizing.setTabViewGroupContentTextSize(activity, mContainer,lang/*
//                   * ,
//                   * app.
//                   * XLARGE
//                   */);
//
//        } catch (NoSuchFieldError e) {
//
//        }
//    }
//    public static void setTabletViewResizingListRow(View v, Activity c) {
//        Typeface type = null;
//        if (MyApplication.lang.equals("en"))
//            type = MyApplication.opensanregular;
//        else
//            type = MyApplication.gdenartwomeduim;
//        MoaziViewResizing viewResizing = new MoaziViewResizing(MyApplication.screenWidth, type);
//        viewResizing.setTabletListRowTextResizing(v, c,MyApplication.lang);
//    }
    public static void setListRowTextResizing(View view, Activity activity, String lang) {
        final ViewGroup mContainer = (ViewGroup) view.getRootView();
        try {
            MoaziViewResizing.setViewGroupContentTextSize(activity, mContainer,lang/*
																		 * ,
																		 * app.
																		 * XLARGE
																		 */);

        } catch (NoSuchFieldError e) {

        }
    }

    // /from generalized
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;

    }
    public static float getResizedValue(int baseSize, Activity activity){
        float size;

        // calculate perscentage according 480 px width screen
        double px = convertPixelsToDp(baseSize, activity) * 1.5;
        double percentage = px / 480;

        size=		(float) (screenWidth * percentage);
        return size;

    }
    @SuppressWarnings("unused")
    public static void textResize(Context activity, TextView textView,
                                  int sizePx) {

        // calculate perscentage according 480 px width screen
        double px = convertPixelsToDp(sizePx, activity) * 1.5;
        double percentage = px / 480;
        // float x=textView.getTextSize();
        float f = textView.getTextSize();

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                (float) (screenWidth * percentage));
        float ff = textView.getTextSize();
        // to force my font , not use font from device
      //   moved to launcher activity

        textView.setTypeface(faceLight);

        // float y=textView.getTextSize();
        // TextView v=textView;

    }

    public static void setChildViewsSize(Activity activity, int parentLayoutId) {

        ViewGroup layout = activity.findViewById(parentLayoutId);
        for (int j = 0; j < layout.getChildCount(); j++) {
            try {
                final TextView text = (TextView) layout.getChildAt(j);
                int size = (int) text.getTextSize();
                textResize(activity, text, size);
            } catch (Exception e) {

            }
        }
    }

    public static void setViewGroupContentTextSize(Activity activity,
                                                   ViewGroup view, String lang) {
        ViewGroup viewGroup = view;

        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                setViewGroupContentTextSize(activity,
                        (ViewGroup) viewGroup.getChildAt(i),lang);
            } else {
                try {

                    final TextView text = (TextView) viewGroup.getChildAt(i);
                    int size = (int) text.getTextSize();
                    if(lang.equals("en"))
                        size+=2;
                    textResize(activity, text, size);

                } catch (Exception e) {

                }
            }
        }
    }

    public static void setViewGroupContentTextTypeFace(Activity activity, ViewGroup view) {
        ViewGroup viewGroup = view;

        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                setViewGroupContentTextTypeFace(activity,
                        (ViewGroup) viewGroup.getChildAt(i));
            } else {
                try {

                    final TextView text = (TextView) viewGroup.getChildAt(i);

                    //	text.setTypeface(MyApplication.faceBold);
                } catch (Exception e) {

                }
            }
        }
    }

    public static void setDialogViewGroupContentTextSize(Activity activity,
                                                         Dialog dialog, ViewGroup view) {
        ViewGroup viewGroup = view;

        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                setDialogViewGroupContentTextSize(activity, dialog,
                        (ViewGroup) viewGroup.getChildAt(i));
            } else {
                try {

                    final TextView text = (TextView) viewGroup.getChildAt(i);
                    int size = (int) text.getTextSize();
                    textResize(activity, text, size);
                } catch (Exception e) {

                }
            }
        }
    }

    public static void setPageTextResizing(Activity activity, String lang) {
        final ViewGroup mContainer = (ViewGroup) activity.findViewById(
                android.R.id.content).getRootView();
        try {
            MoaziViewResizing.setViewGroupContentTextSize(activity, mContainer,lang/*
																		 * ,
																		 * app.
																		 * XLARGE
																		 */);

        } catch (NoSuchFieldError e) {

        }
    }

    public static void setPageTextTypeFace(Activity activity) {
        final ViewGroup mContainer = (ViewGroup) activity.findViewById(
                android.R.id.content).getRootView();
        try {
            MoaziViewResizing.setViewGroupContentTextTypeFace(activity, mContainer/*
																 * ,
																		 * app.
																		 * XLARGE
																		 */);

        } catch (NoSuchFieldError e) {

        }
    }

    public static void setDialogTextResizing(Activity activity, Dialog dialog) {
        final ViewGroup mContainer = (ViewGroup) dialog.findViewById(
                android.R.id.content).getRootView();
        try {
            MoaziViewResizing.setDialogViewGroupContentTextSize(activity, dialog,
                    mContainer/* , app.XLARGE */);

        } catch (NoSuchFieldError e) {

        }
    }

    @SuppressWarnings("unused")
    public static void setWebViewTextSize(Activity activity, WebView webView) {
        WebSettings webSettings = webView.getSettings();
        int fontSize = webSettings.getDefaultFontSize();

        // calculate perscentage according 480 px width screen
        double px = convertPixelsToDp((float) (fontSize / 1.5), activity) * 1.5;
        double percentage = px / 480;
        // float x=textView.getTextSize();
        webSettings
                .setDefaultFontSize((int) (screenWidth * percentage));

    }

}

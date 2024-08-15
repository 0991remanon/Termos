package com.termux.app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;
import android.widget.Toast;

public class Utils {
    public static void darkToast(Context c, CharSequence ch, int t){
        Toast tst = Toast.makeText(c, ch, t);
        makeUp(c, tst);
        tst.show();
    }

    public static void darkToast(Context c, int r, int t){
        Toast tst = Toast.makeText(c, r, t);
        makeUp(c, tst);
        tst.show();
    }

    private static void makeUp(Context c, Toast tst){
        try {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadii(new float[] { 45, 45, 45, 45, 45, 45, 45, 45 });
            shape.setColor(Color.parseColor("#d0242528"));
            shape.setStroke(1, Color.parseColor("#60ffffff"));
            tst.getView().setBackgroundColor(Color.parseColor("#00000000"));
            TextView tw = tst.getView().findViewById(android.R.id.message);
            tw.setTextColor(Color.parseColor("#ff0096ff"));
            int ff = dpAsPx(c, 25);
            int tf = dpAsPx(c, 15);
            tw.setPadding(ff,tf,ff,tf);
            tw.setBackground(shape);
        }catch (Exception e){}
    }

    public static int dpAsPx(Context c, int i) {
        return Math.round(c.getResources().getDisplayMetrics().density * ((float) i));
    }
}

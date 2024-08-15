package com.termux.app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

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

    public static boolean textToFile(String file, String text) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(text.getBytes()); // Записываем байты в файл
        } catch (Exception e) {return false;}
        return true;
    }

    public static boolean createBashrc(String[] args){
        String text = "export HOME=\"" +
                args[1] +
                "\"\ntmpHOSTNAME=\nfor tH in ro.boot.device ro.build.product ro.lineage.device ro.product.board ro.product.device ro.product.name ro.product.odm.device ro.product.odm.name ro.product.product.device ro.product.product.name ro.product.system.device ro.product.system.name ro.product.system_ext.device ro.product.system_ext.name ro.product.vendor.device ro.product.vendor.name ro.product.vendor_dlkm.device ro.product.vendor_dlkm.name; do\n  tmpHOSTNAME=$(getprop $tH)\n  [ -n $tmpHOSTNAME ] && break\ndone" +
                "\nexport HOSTNAME=$tmpHOSTNAME" +
                "\nexport TERM=xterm" +
                "\nexport TMPDIR=\"" +
                args[2] +
                "\"\nexport USER=$(id -un)" +
                "\nif [[ $- != *i* ]] ; then\n  return\nfi" +
                "\nshopt -s checkwinsize" +
                "\nshopt -s histappend" +
                "\nuse_color=false" +
                "\nif [[ ${EUID} == 0 ]] ; then" +
                "\n  PS1='\\[\\033[01;31m\\]${HOSTNAME:=$(hostname)}\\[\\033[01;34m\\] \\w \\$\\[\\033[00m\\] '" +
                "\nelse" +
                "\n  PS1='\\[\\033[01;32m\\]${USER:=$(id \\-un)}@${HOSTNAME:=$(hostname)}\\[\\033[01;34m\\] \\w \\$\\[\\033[00m\\] '" +
                "\nfi" +
                "\nalias ll='ls -l --color=auto'" +
                "\nalias ls='ls --color=auto'" +
                "\nunset use_color safe_term match_lhs";

        return textToFile(args[0], text);
    }
}

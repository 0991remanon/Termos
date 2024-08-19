package com.termux.app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.termux.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static void darkToast(Context c, CharSequence ch, int t){
        Toast tst = Toast.makeText(c, ch, t);
        makeUp(c, tst);
        tst.show();
    }

    public static Toast doToast(Context c, int r, int t){
        Toast tst = Toast.makeText(c, r, t);
        makeUp(c, tst);
        return tst;
    }

    public static Toast doToast(Context c, CharSequence ch, int t){
        Toast tst = Toast.makeText(c, ch, t);
        makeUp(c, tst);
        return tst;
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
            shape.setColor(Color.parseColor("#d0000000"));
            shape.setStroke(2, Color.parseColor("#60ffffff"));
            tst.getView().setBackgroundColor(Color.parseColor("#00000000"));
            TextView tw = tst.getView().findViewById(android.R.id.message);
            tw.setTextColor(Color.parseColor("#ffffffff"));
            int ff = dpAsPx(c, 25);
            int tf = dpAsPx(c, 15);
            tw.setPadding(ff,tf,ff,tf);
            tw.setBackground(shape);
        }catch (Exception e){}
    }

    public static int dpAsPx(Context c, int i) {
        return Math.round(c.getResources().getDisplayMetrics().density * ((float) i));
    }

    public static String[] parseArguments(String command) {
        if (command == null || command.trim().isEmpty()) return null;
        try {
            List<String> args = new ArrayList<>();
            StringBuilder currentArg = new StringBuilder();
            boolean inQuotes = false;
            char quoteChar = 0;

            for (int i = 0; i < command.length(); i++) {
                char c = command.charAt(i);

                if (inQuotes) {
                    if (c == quoteChar) {
                        inQuotes = false;
                    } else if (c == '\\' && i + 1 < command.length() && (command.charAt(i + 1) == quoteChar || command.charAt(i + 1) == '\\')) {
                        currentArg.append(command.charAt(++i));
                    } else {
                        currentArg.append(c);
                    }
                } else {
                    if (c == '\'' || c == '"') {
                        inQuotes = true;
                        quoteChar = c;
                    } else if (c == '\\' && i + 1 < command.length()) {
                        currentArg.append(command.charAt(++i));
                    } else if (Character.isWhitespace(c)) {
                        if (currentArg.length() > 0) {
                            args.add(currentArg.toString());
                            currentArg.setLength(0);
                        }
                    } else {
                        currentArg.append(c);
                    }
                }
            }

            if (currentArg.length() > 0) {
                args.add(currentArg.toString());
            }

            return args.toArray(new String[0]);
        } catch (Exception e){
            return null;
        }
    }

    public static boolean textToFile(String file, String text) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(text.getBytes());
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
                "\"\nexport SHELL=\"" +
                args[3] +
                "\"\nexport HISTFILE=\"$HOME\"/.bash_history" +
                "\nexport HISTCONTROL=ignoreboth:erasedups" +
                "\nexport HISTSIZE=1000" +
                "\nexport HISTFILESIZE=10000" +
                "\nexport USER=$(id -un)" +
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

    public static boolean isDirOkey(String path) {
        if (path == null || path.trim().isEmpty()) return false;
        File homeDir = new File(path);
        return homeDir.exists() && homeDir.isDirectory();
    }
}

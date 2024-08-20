package com.termux.app.terminal;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.termux.R;
import com.termux.app.ButtonBg;
import com.termux.app.TermuxActivity;
import com.termux.app.Utils;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.terminal.TerminalSession;

import java.util.List;

public class TermuxSessionsListViewController extends ArrayAdapter<TermuxSession> {

    final TermuxActivity mActivity;

    final StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
    final StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);

    public TermuxSessionsListViewController(TermuxActivity activity, List<TermuxSession> sessionList) {
        super(activity.getApplicationContext(), R.layout.item_terminal_sessions_list, sessionList);
        this.mActivity = activity;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View sessionRowView = convertView;
        if (sessionRowView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            sessionRowView = inflater.inflate(R.layout.item_terminal_sessions_list, parent, false);
        }

        TextView sessionTitleView = sessionRowView.findViewById(R.id.session_title);

        TerminalSession sessionAtRow = getItem(position).getTerminalSession();
        if (sessionAtRow == null) {
            sessionTitleView.setText("null session");
            return sessionRowView;
        }
        sessionTitleView.setBackground(new ButtonBg(0, false, true));
        int padding = Utils.dpAsPx(mActivity, 5);
        sessionTitleView.setPadding(padding, padding, padding, padding);

        String name = sessionAtRow.mSessionName;
        String sessionTitle = sessionAtRow.getTitle();

        String numberPart = "[" + (position + 1) + "] ";
        String sessionNamePart = (TextUtils.isEmpty(name) ? "" : name);
        String sessionTitlePart = (TextUtils.isEmpty(sessionTitle) ? "" : ((sessionNamePart.isEmpty() ? "" : "\n") + sessionTitle));

        String fullSessionTitle = numberPart + sessionNamePart + sessionTitlePart;
        SpannableString fullSessionTitleStyled = new SpannableString(fullSessionTitle);
        fullSessionTitleStyled.setSpan(boldSpan, 0, numberPart.length() + sessionNamePart.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fullSessionTitleStyled.setSpan(italicSpan, numberPart.length() + sessionNamePart.length(), fullSessionTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        sessionTitleView.setText(fullSessionTitleStyled);

        boolean sessionRunning = sessionAtRow.isRunning();

        if (sessionRunning) {
            sessionTitleView.setPaintFlags(sessionTitleView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            sessionTitleView.setPaintFlags(sessionTitleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        int color = sessionRunning || sessionAtRow.getExitStatus() == 0 ? Color.WHITE : Color.RED;
        sessionTitleView.setTextColor(color);

        sessionTitleView.setOnClickListener(new OnClickSessionName(sessionAtRow, mActivity));
        sessionTitleView.setOnLongClickListener(new OnClickSessionName(sessionAtRow, mActivity));
        ImageButton closeButton = sessionRowView.findViewById(R.id.close_button);
        closeButton.setBackground(new ButtonBg(0, false));
        closeButton.setPadding(padding, padding, padding, padding);
        closeButton.setOnClickListener(new OnClickCloseBunnon(sessionAtRow));

        return sessionRowView;
    }

    private static class OnClickCloseBunnon implements View.OnClickListener {
        private final TerminalSession gSession;
        private OnClickCloseBunnon(TerminalSession session){
            gSession = session;
        }

        @Override
        public void onClick(View v) {
            gSession.finishIfRunning();
        }
    }

    private static class OnClickSessionName implements View.OnClickListener, View.OnLongClickListener {
        private final TerminalSession gSession;
        private final TermuxActivity gActivity;
        private OnClickSessionName(TerminalSession session, TermuxActivity activity){
            gSession = session;
            gActivity = activity;
        }

        @Override
        public void onClick(View v) {
            gActivity.getTermuxTerminalSessionClient().setCurrentSession(gSession);
            gActivity.getDrawer().closeDrawers();
        }

        @Override
        public boolean onLongClick(View v) {
            gActivity.getTermuxTerminalSessionClient().renameSession(gSession);
            return true;
        }
    }
}

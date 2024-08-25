package com.termux.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ColorPickerDialog extends Dialog implements OnSeekBarChangeListener, TextWatcher{

    public Context mContext;
    private final OnColorChangedListener mListener;
    public int mInitialColor;
    private final int mRequest;
    private final EditText mColorAsText;
    private int lastColor;
    private final SeekBar mAlphaBar;
    private View mColorPickerView;
    private final Paint mCenterPaint;

    public ColorPickerDialog(Context context,
                             OnColorChangedListener listener,
                             int initialColor, int requestCode) {
        super(context);
        mContext = context;
        mListener = listener;
        mInitialColor = initialColor;
        mRequest = requestCode;
        mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorAsText = new EditText(mContext);
        mAlphaBar = new SeekBar(mContext);
    }

    @Override
    public void show() {
        mCenterPaint.setColor(mInitialColor);
        super.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnColorChangedListener l = new OnColorChangedListener() {
            public void colorChanged(int color, int requestCode) {
                mListener.colorChanged(color, requestCode);
                dismiss();
            }
        };

        int padding = dpAsPx(mContext, 8);
        LinearLayout ll = new LinearLayout(mContext);
        LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(padding, padding, padding, padding);
        lp = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = android.view.Gravity.CENTER;  
        mColorAsText.setLayoutParams(lp);
        mColorAsText.setSingleLine();
        mColorAsText.setGravity(Gravity.CENTER_HORIZONTAL);
        mColorAsText.setMinEms(3);
        setTextViewTextAppearance(mColorAsText, android.R.style.TextAppearance_Small);
        mColorAsText.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        mColorAsText.setPadding(0, padding, 0, padding);
        mColorAsText.setText(colorToString(mInitialColor));
        ll.addView(mColorAsText);
        mColorPickerView = new ColorPickerView(l, mInitialColor, mRequest, mColorAsText, mCenterPaint, this);
        mColorPickerView.setLayoutParams(lp);
        ll.addView(mColorPickerView);
        lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mAlphaBar.setLayoutParams(lp);
        mAlphaBar.setPadding(padding, padding, padding, padding);
        mAlphaBar.setMax(255);
        mAlphaBar.setProgress(colorToProgress(mInitialColor));
        mAlphaBar.setOnSeekBarChangeListener(this);
        onProgressChanged(mAlphaBar, colorToProgress(mInitialColor), false);
        ll.addView(mAlphaBar);
        setContentView(ll);
        mColorAsText.setFilters(new InputFilter[]{
            new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                String whatWeHave = source.toString().toUpperCase();
                if ( dest.length() < 8 && whatWeHave.matches("[0-9A-F]*") ){
                    return whatWeHave;
                }
                return "" ;
            }
            }
         });
         mColorAsText.addTextChangedListener(this);
    }

    @Override
    public void afterTextChanged(Editable p1)
    {
        if (p1.length() == 8){
            try{
                int newColor = Color.parseColor("#" + p1.toString());
                if (lastColor == newColor) return;
                lastColor = newColor;
                mCenterPaint.setColor(newColor);
                onProgressChanged(mAlphaBar, colorToProgress(newColor), true);
                mAlphaBar.setProgress(colorToProgress(newColor));
            }catch(Exception e){}
        }
    }

    @Override
    public void onProgressChanged(SeekBar p1, int p2, boolean p3){
        int tmp = (mCenterPaint.getColor() & 0x00FFFFFF) | (p2 << 24);
        mCenterPaint.setColor(tmp);
        mColorAsText.setText(colorToString(tmp));
        mColorAsText.setSelection(mColorAsText.getText().length());
        mColorPickerView.invalidate();
    }

    private static class ColorPickerView extends View {
		public Context context;
        public final ColorPickerDialog colorPickerDialog;
        private final Paint mPaint;
        private final Paint mCenterPaint;
        private final int[] mColors;
        private final OnColorChangedListener mListener;
        private final int mReturnCode;
        private final EditText ColorAsText;
		private final int CENTER_X;
		private final int CENTER_Y;
		private final int CENTER_RADIUS;

        ColorPickerView(OnColorChangedListener l, int color, int requestCode, EditText tv, Paint centerPaint, ColorPickerDialog cpd) {
            super(cpd.getContext());
            context = cpd.getContext();
            colorPickerDialog = cpd;
            mReturnCode = requestCode;
            ColorAsText = tv;
			int temp = dpAsPx(context, 170);
			CENTER_X = temp;
			CENTER_Y = temp;
			CENTER_RADIUS = dpAsPx(context, 51);
            mListener = l;
            mColors = new int[] {
                0xFF808080, 0xFFFFFFFF, 0xFFFF3333, 0xFFFF33FF, 0xFF3333FF, 0xFF33FFFF, 0xFF33FF33,
                0xFFFFFF33, 0xFFFF3333, 0xFF000000, 0xFF808080
            };
            Shader s = new SweepGradient(0, 0, mColors, null);

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setShader(s);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(dpAsPx(context, 102));

            mCenterPaint = centerPaint;
            mCenterPaint.setColor(color);
            mCenterPaint.setStrokeWidth(dpAsPx(context, 8));
        }

        private boolean mTrackingCenter;
        private boolean mHighlightCenter;

        @SuppressLint("DrawAllocation")
        @Override
        protected void onDraw(Canvas canvas) {
            float r = CENTER_X - mPaint.getStrokeWidth()*0.5f;
            canvas.translate(CENTER_X, CENTER_X);
            canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
            canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);
            if (mTrackingCenter) {
                int c = mCenterPaint.getColor();
                mCenterPaint.setStyle(Paint.Style.STROKE);
                if (mHighlightCenter) {
                    mCenterPaint.setAlpha(0xFF);
                } else {
                    mCenterPaint.setAlpha(0x80);
                }
                canvas.drawCircle(0, 0,
                                  CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
                                  mCenterPaint);
                mCenterPaint.setStyle(Paint.Style.FILL);
                mCenterPaint.setColor(c);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(CENTER_X*2, CENTER_Y*2);
        }

        private int ave(int s, int d, float p) {
            return s + java.lang.Math.round(p * (d - s));
        }

        private int interpColor(int[] colors, float unit) {
            if (unit <= 0) {
                return colors[0];
            }
            if (unit >= 1) {
                return colors[colors.length - 1];
            }

            float p = unit * (colors.length - 1);
            int i = (int)p;
            p -= i;

            // now p is just the fractional part [0...1) and i is the index
            int c0 = colors[i];
            int c1 = colors[i+1];
            int a = (mCenterPaint.getColor() >> 24) & 0xFF; // ave(Color.alpha(c0), Color.alpha(c1), p);
            int r = ave(Color.red(c0), Color.red(c1), p);
            int g = ave(Color.green(c0), Color.green(c1), p);
            int b = ave(Color.blue(c0), Color.blue(c1), p);

            return Color.argb(a, r, g, b);
        }

        private static final float PI = 3.1415926f;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX() - CENTER_X;
            float y = event.getY() - CENTER_Y;
            boolean inCenter = java.lang.Math.sqrt(x*x + y*y) <= CENTER_RADIUS;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTrackingCenter = inCenter;
                    if (inCenter) {
                        mHighlightCenter = true;
                        invalidate();
                        break;
                    }
                case MotionEvent.ACTION_MOVE:
                    if (mTrackingCenter) {
                        if (mHighlightCenter != inCenter) {
                            mHighlightCenter = inCenter;
                            invalidate();
                        }
                    } else {
                        float angle = (float)java.lang.Math.atan2(y, x);
                        // need to turn angle [-PI ... PI] into unit [0....1]
                        float unit = angle/(2*PI);
                        if (unit < 0) {
                            unit += 1;
                        }
                        int nowColor = interpColor(mColors, unit);
                        mCenterPaint.setColor(nowColor);
                        ColorAsText.setText(colorToString(nowColor));
                        ColorAsText.setSelection(ColorAsText.getText().length());
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mTrackingCenter) {
                        if (inCenter) {
                            colorPickerDialog.mInitialColor = mCenterPaint.getColor();
                            mListener.colorChanged(colorPickerDialog.mInitialColor, mReturnCode);
                        }
                        mTrackingCenter = false;    // so we draw w/o halo
                        invalidate();
                    }
                    break;
            }
            return true;
        }
    }

    private static String colorToString(int i){
        return String.format("%08x", (i & 0xFFFFFFFF)).toUpperCase();
    }

    private static int colorToProgress(int i){
        return (i >> 24) & 0xFF;
    }

    private static int dpAsPx(Context c, int i) {
        return Math.round(c.getResources().getDisplayMetrics().density * ((float) i));
    }

    private static void setTextViewTextAppearance(View tv, int resId) {
        try{
            if (tv instanceof TextView || tv instanceof EditText){
                ((TextView) tv).setTextAppearance(resId);
            }
        }catch(Exception e){}
	}

    public interface OnColorChangedListener {
        void colorChanged(int color, int requestCode);
    }

    @Override
    public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4){
    }

    @Override
    public void onTextChanged(CharSequence p1, int p2, int p3, int p4){
    }

    @Override
    public void onStartTrackingTouch(SeekBar p1){
    }

    @Override
    public void onStopTrackingTouch(SeekBar p1){
    }
}

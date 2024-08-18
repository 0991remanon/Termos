package com.termux.app;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;


public class ButtonBg extends StateListDrawable
{
	public ButtonBg(int color, int stroke){
		boolean b = true;
		for (GradientDrawable tmp : new GradientDrawable[]{new GradientDrawable(),new GradientDrawable()}){
			tmp.setShape(GradientDrawable.RECTANGLE);
			tmp.setCornerRadius(12.0f);
			tmp.setStroke(stroke, 1620086928);  //Color.parseColor("#60909090"));
			if (b){
				b=false;
				tmp.setColor(color);
				this.addState(new int[]{android.R.attr.state_pressed}, tmp);
				this.addState(new int[]{android.R.attr.state_focused}, tmp);
				this.addState(new int[]{android.R.attr.state_selected}, tmp);
				this.addState(new int[]{android.R.attr.state_activated}, tmp);
			}else{
				tmp.setColor(color);
				this.addState(new int[]{}, tmp);
			}
		}
	}

	public ButtonBg(int color, boolean useStroke){
		boolean b = true;
		for (GradientDrawable tmp : new GradientDrawable[]{new GradientDrawable(),new GradientDrawable()}){
			tmp.setShape(GradientDrawable.RECTANGLE);
			tmp.setCornerRadius(12.0f);
			if (useStroke) tmp.setStroke(2, 1620086928);  //Color.parseColor("#60909090"));
			if (b){
				b=false;
				tmp.setColor(-1872732064);  //Color.parseColor("#90606060"));
				this.addState(new int[]{android.R.attr.state_pressed}, tmp);
				this.addState(new int[]{android.R.attr.state_focused}, tmp);
				this.addState(new int[]{android.R.attr.state_selected}, tmp);

			}else{
				tmp.setColor(color);
				this.addState(new int[]{}, tmp);
			}
		}
	}

	public ButtonBg(int color, boolean useStroke, boolean inverse){
		boolean b = true;
		for (GradientDrawable tmp : new GradientDrawable[]{new GradientDrawable(),new GradientDrawable()}){
			tmp.setShape(GradientDrawable.RECTANGLE);
			tmp.setCornerRadius(9.0f);
			if (useStroke) tmp.setStroke(2, 1620086928);  //Color.parseColor("#60909090"));
			if (b){
				b=false;
				tmp.setColor(-2142220208);  //Color.parseColor("#80505050"));
				this.addState(new int[]{android.R.attr.state_pressed}, tmp);
				this.addState(new int[]{android.R.attr.state_focused}, tmp);
				this.addState(new int[]{android.R.attr.state_selected}, tmp);
				this.addState(new int[]{android.R.attr.state_activated}, tmp);
			}else{
				tmp.setColor(color);
				this.addState(new int[]{}, tmp);
			}
		}
	}

	@Override
	public void draw(Canvas canvas){
		super.draw(canvas);
		/*
		paint.setColor(0x33000000);
		paint.setMaskFilter(new BlurMaskFilter(0, BlurMaskFilter.Blur.OUTER));
		canvas.drawRoundRect(0,
							 0,
							 getIntrinsicWidth(),
							 getIntrinsicHeight(),
							 0, 0,
							 paint);
		paint.reset();
		paint.setColor(Color.parseColor("#30242528"));
		canvas.drawRoundRect(0,
							 0,
							 getIntrinsicWidth(),
							 getIntrinsicHeight(),
							 0, 0,
							 paint);
							 */
	}

	@Override
	public void setAlpha(int p1)
	{

	}

	@Override
	public void setColorFilter(ColorFilter p1)
	{

	}

	@Override
	public int getOpacity()
	{
		return PixelFormat.UNKNOWN;
	}
	
	/*
	public static ColorStateList toColorStateList(int normalColor, int pressedColor, int focusedColor, int unableColor) {
        int[] colors = new int[]{pressedColor, focusedColor, normalColor, focusedColor, unableColor, normalColor};
        int[][] states = new int[6][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{android.R.attr.state_focused};
        states[4] = new int[]{android.R.attr.state_window_focused};
        states[5] = new int[]{};
        return new ColorStateList(states, colors);
    }

    public static ColorStateList toColorStateList(int normalColor, int pressedColor) {
        return toColorStateList(normalColor, pressedColor, pressedColor, normalColor);
    }
	*/
}

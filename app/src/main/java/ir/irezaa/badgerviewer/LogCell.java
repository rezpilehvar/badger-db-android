package ir.irezaa.badgerviewer;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LogCell extends FrameLayout {
    private TextView textView;

    public LogCell(Context context) {
        super(context);
        textView = new TextView(context);
        textView.setTextColor(Color.WHITE);
        addView(textView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LayoutParams) textView.getLayoutParams()).bottomMargin = 15;
    }


    public void setText(String text) {
        String finalString = "$ " + text;

        Spannable stringToSet = new SpannableString(finalString);

        stringToSet.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(stringToSet);
    }
}

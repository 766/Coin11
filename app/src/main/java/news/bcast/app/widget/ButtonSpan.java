package news.bcast.app.widget;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by KangWei on 2018/4/4.
 * 2018/4/4 21:10
 * Coin11
 * com.bitcast.app.widget
 */
class ButtonSpan extends ClickableSpan {

    View.OnClickListener onClickListener;
    private Context context;
    private int colorId;

    public ButtonSpan(Context context, View.OnClickListener onClickListener) {
        this(context, onClickListener, android.R.color.background_dark);
    }

    public ButtonSpan(Context context, View.OnClickListener onClickListener, int colorId) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.colorId = colorId;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(context.getResources().getColor(colorId));
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
        if (onClickListener != null) {
            onClickListener.onClick(widget);
        }
    }
}

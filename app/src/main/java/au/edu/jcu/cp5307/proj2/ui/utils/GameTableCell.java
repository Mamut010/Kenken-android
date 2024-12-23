package au.edu.jcu.cp5307.proj2.ui.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import au.edu.jcu.cp5307.proj2.R;
import au.edu.jcu.cp5307.proj2.constants.Constants;
import au.edu.jcu.cp5307.proj2.utils.helpers.UiHelpers;

public class GameTableCell {
    public static final float DEFAULT_SUPERSCRIPT_SCALE = 1/3f;

    private static final float SUPERSCRIPT_HORIZONTAL_BIAS = 0.1f;

    private final Context context;
    private final ConstraintLayout cellLayout;
    private final ConstraintLayout wrapperLayout;
    private final Button valueButton;
    private TextView superscriptTextView;
    private final float initialTextSize;

    public GameTableCell(Context context, String superscript) {
        this.context = context;
        this.superscriptTextView = null;
        this.cellLayout = new ConstraintLayout(context);
        this.wrapperLayout = new ConstraintLayout(context);
        this.valueButton = new Button(context);
        this.initialTextSize = valueButton.getTextSize();
        prepareViews(superscript);
    }

    public View getView() {
        return wrapperLayout;
    }

    public void setValueText(String valueText) {
        valueButton.setText(valueText);
    }

    public void setValueTextColor(int color) {
        valueButton.setTextColor(color);
    }

    public void setValueTextScale(float scale) {
        setTextScale(valueButton, scale);
    }

    public void setBackground(Drawable background) {
        wrapperLayout.setBackground(background);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        wrapperLayout.setOnClickListener(listener);
    }

    private void setTextScale(TextView textView, float scale) {
        textView.setTextSize(initialTextSize * scale);
    }

    private void prepareViews(String superscript) {
        configLayout();
        configWrapperLayout();
        configValueButton();
        if (superscript != null) {
            configSuperscriptTextView(superscript);
            addSuperscriptTextViewToLayout();
        }
        addValueTextViewToLayout();
    }

    private void configLayout() {
        UiHelpers.generateIdForView(cellLayout);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                0,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.dimensionRatio = Constants.Ui.DIMENSION_RATIO_SQUARE;
        cellLayout.setLayoutParams(layoutParams);
    }

    private void configWrapperLayout() {
        wrapperLayout.addView(cellLayout);
        wrapperLayout.setClickable(true);
        wrapperLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void configValueButton() {
        UiHelpers.generateIdForView(valueButton);
        valueButton.setClickable(false);
        UiHelpers.setViewBackground(valueButton, context, android.R.color.transparent);
    }

    private void configSuperscriptTextView(String superscript) {
        superscriptTextView = new TextView(context);
        UiHelpers.generateIdForView(superscriptTextView);
        setTextScale(superscriptTextView, DEFAULT_SUPERSCRIPT_SCALE);
        setTextViewColorBlack(superscriptTextView);
        superscriptTextView.setText(superscript);
    }

    private void addSuperscriptTextViewToLayout() {
        cellLayout.addView(superscriptTextView);

        int tvId = superscriptTextView.getId();
        int parentId = cellLayout.getId();

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(cellLayout);
        constraintSet.setHorizontalBias(tvId, SUPERSCRIPT_HORIZONTAL_BIAS);
        constraintSet.constrainedWidth(tvId, true);
        constraintSet.connect(tvId, ConstraintSet.START, parentId, ConstraintSet.START);
        constraintSet.connect(tvId, ConstraintSet.END, parentId, ConstraintSet.END);
        constraintSet.connect(tvId, ConstraintSet.TOP, parentId, ConstraintSet.TOP);
        constraintSet.applyTo(cellLayout);
    }

    private void addValueTextViewToLayout() {
        cellLayout.addView(valueButton);

        int tvId = valueButton.getId();
        int parentId = cellLayout.getId();

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(cellLayout);
        constraintSet.connect(tvId, ConstraintSet.START, parentId, ConstraintSet.START);
        constraintSet.connect(tvId, ConstraintSet.END, parentId, ConstraintSet.END);
        constraintSet.connect(tvId, ConstraintSet.TOP, parentId, ConstraintSet.TOP);
        constraintSet.connect(tvId, ConstraintSet.BOTTOM, parentId, ConstraintSet.BOTTOM);
        constraintSet.applyTo(cellLayout);
    }

    private void setTextViewColorBlack(TextView textView) {
        textView.setTextColor(UiHelpers.getColor(context, R.color.black));
    }
}

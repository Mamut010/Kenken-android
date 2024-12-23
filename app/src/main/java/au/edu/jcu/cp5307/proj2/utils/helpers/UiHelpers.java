package au.edu.jcu.cp5307.proj2.utils.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;

public final class UiHelpers {
    private UiHelpers() {}

    public static void generateIdForView(View view) {
        int newId = View.generateViewId();
        view.setId(newId);
    }

    public static void consumeViewGroupAndChildren(ViewGroup viewGroup, Consumer<View> consumer) {
        consumer.accept(viewGroup);

        Queue<ViewGroup> viewGroups = new ArrayDeque<>();
        viewGroups.add(viewGroup);
        while (!viewGroups.isEmpty()) {
            viewGroup = viewGroups.remove();
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                consumer.accept(child);
                if (child instanceof ViewGroup) {
                    viewGroups.add((ViewGroup) child);
                }
            }
        }
    }

    public static Drawable getDrawable(Context context, int drawableId) {
        return ResourcesCompat.getDrawable(
                context.getResources(),
                drawableId,
                context.getTheme()
        );
    }

    public static int getColor(Context context, int colorId) {
        return ResourcesCompat.getColor(
                context.getResources(),
                colorId,
                context.getTheme()
        );
    }

    public static int getInsetStrokeIgnoredOffset(int strokeWidth) {
        return -strokeWidth;
    }

    public static void setViewBackground(View view, Context context, int backgroundId) {
        Drawable background = getDrawable(context, backgroundId);
        view.setBackground(background);
    }

    public static AlertDialog createInfoDialog(Context context, String title, CharSequence message,
                                      DialogInterface.OnClickListener onOkClickedListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, onOkClickedListener);
        return dialogBuilder.create();
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    public static <T extends Serializable> T getSerializable(
            Bundle bundle,
            String key,
            Class<T> serializableClass
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return bundle.getSerializable(key, serializableClass);
        }
        else {
            return (T) bundle.getSerializable(key);
        }
    }
}

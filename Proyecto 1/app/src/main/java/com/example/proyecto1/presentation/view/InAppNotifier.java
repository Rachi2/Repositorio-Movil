package com.example.proyecto1.presentation.view;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto1.R;

// Muestra el banner propio de Kairo arriba de la pantalla que esté abierta
public class InAppNotifier {

    private static final String BANNER_TAG = "in_app_banner";
    private static final long VISIBLE_TIME_MS = 3500;

    public static void show(Activity activity, String senderId, String senderName, String body) {
        ViewGroup root = activity.findViewById(android.R.id.content);
        if (root == null) return;

        View previous = root.findViewWithTag(BANNER_TAG);
        if (previous != null) root.removeView(previous);

        View banner = LayoutInflater.from(activity).inflate(R.layout.view_in_app_notification, root, false);
        banner.setTag(BANNER_TAG);
        ((TextView) banner.findViewById(R.id.tvBannerInitial)).setText(AvatarUtils.getInitial(senderName));
        ((TextView) banner.findViewById(R.id.tvBannerName)).setText(senderName);
        ((TextView) banner.findViewById(R.id.tvBannerBody)).setText(body);

        WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(root);
        int statusBarHeight = insets != null
                ? insets.getInsets(WindowInsetsCompat.Type.statusBars()).top : 0;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP);
        params.topMargin = statusBarHeight + 8;

        banner.setOnClickListener(v -> {
            root.removeView(banner);
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_USER_ID, senderId);
            intent.putExtra(ChatActivity.EXTRA_USER_NAME, senderName);
            activity.startActivity(intent);
             if (activity instanceof ChatActivity) activity.finish();
        });

        root.addView(banner, params);

        banner.setTranslationY(-400f);
        banner.animate().translationY(0f).setDuration(250).start();

        banner.postDelayed(() -> banner.animate()
                .translationY(-400f)
                .alpha(0f)
                .setDuration(250)
                .withEndAction(() -> root.removeView(banner))
                .start(), VISIBLE_TIME_MS);
    }
}
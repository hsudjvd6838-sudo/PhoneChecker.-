
package com.phonechecker.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

public class MainActivity extends Activity {

    private final int BG = Color.rgb(10, 14, 18);
    private final int CARD = Color.rgb(20, 27, 33);
    private final int GREEN = Color.rgb(68, 255, 153);
    private final int WHITE = Color.WHITE;
    private final int MUTED = Color.rgb(150, 165, 175);
    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.RIGHT);
        t.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        t.setPadding(0, 5, 0, 5);
        return t;
    }

    private void addLabel(String value, int size, int color) {
        content.addView(text(value, size, color));
    }

    private void addCard(String title, String subtitle,
                         String buttonText, Runnable action) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 16, 20, 16);
        card.setBackground(shape(CARD, 24));

        TextView heading = text(title, 19, WHITE);
        heading.setTypeface(null, Typeface.BOLD);
        card.addView(heading);

        TextView desc = text(subtitle, 13, MUTED);
        card.addView(desc);

        TextView button = text(buttonText, 15, GREEN);
        button.setGravity(Gravity.CENTER);
        button.setTypeface(null, Typeface.BOLD);
        button.setPadding(12, 12, 12, 12);
        button.setBackground(shape(Color.rgb(29, 54, 43), 16));
        LinearLayout.LayoutParams bp =
            new LinearLayout.LayoutParams(-1, -2);
        bp.topMargin = 12;
        card.addView(button, bp);
        button.setOnClickListener(v -> action.run());

        LinearLayout.LayoutParams cp =
            new LinearLayout.LayoutParams(-1, -2);
        cp.bottomMargin = 14;
        content.addView(card, cp);
    }

    private void showHome() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(22, 26, 22, 28);
        scroll.addView(content);

        addLabel("PHONE CHECKER", 27, GREEN);
        addLabel("مركز فحص الجهاز والأمان", 15, WHITE);
        addLabel("────────────────────", 14, GREEN);

        LinearLayout status = new LinearLayout(this);
        status.setOrientation(LinearLayout.VERTICAL);
        status.setPadding(18, 18, 18, 18);
        status.setBackground(shape(CARD, 22));

        TextView s1 = text("●  النظام يعمل", 17, GREEN);
        status.addView(s1);
        status.addView(text("فحوصات معلومات الجهاز", 13, MUTED));
        LinearLayout.LayoutParams sp =
            new LinearLayout.LayoutParams(-1, -2);
        sp.topMargin = 18;
        sp.bottomMargin = 22;
        content.addView(status, sp);

        addLabel("أدوات الفحص", 21, WHITE);
        addLabel("اختر الفحص الذي تريد تشغيله", 13, MUTED);

        addCard("📱  معلومات الجهاز",
            "إصدار أندرويد والمعالج والبنية",
            "عرض المعلومات", () -> showDevice());

        addCard("🛡️  مؤشرات الروت",
            "فحص مؤشرات متاحة دون صلاحيات خاصة",
            "بدء الفحص", () -> showRoot());

        addCard("💾  مساحة التخزين",
            "عرض المساحة المتاحة للتطبيق",
            "فحص التخزين", () -> showStorage());

        addLabel("PhoneChecker  •  الإصدار 1.0", 12, MUTED);
        addLabel("النتائج إرشادية ولا تضمن سلامة الجهاز", 11, MUTED);

        setContentView(scroll);
    }

    private void showPage(String title, String details) {
        content.removeAllViews();

        addLabel("PHONE CHECKER", 25, GREEN);
        addLabel(title, 22, WHITE);
        addLabel("────────────────────", 14, GREEN);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 18, 18, 18);
        card.setBackground(shape(CARD, 22));

        TextView result = text(details, 16, WHITE);
        result.setGravity(Gravity.RIGHT);
        result.setTextIsSelectable(true);
        card.addView(result);
        content.addView(card);

        TextView back = text("←  العودة للرئيسية", 16, GREEN);
        back.setGravity(Gravity.CENTER);
        back.setPadding(12, 22, 12, 22);
        content.addView(back);
        back.setOnClickListener(v -> showHome());
    }

    private void showDevice() {
        String info =
            "الشركة: " + Build.MANUFACTURER +
            "\n\nالجهاز: " + Build.MODEL +
            "\n\nإصدار أندرويد: " + Build.VERSION.RELEASE +
            "\n\nمستوى API: " + Build.VERSION.SDK_INT +
            "\n\nنوع البناء: " + Build.TYPE +
            "\n\nالمعمارية: " + Build.SUPPORTED_ABIS[0];

        showPage("معلومات الجهاز", info);
    }

    private void showRoot() {
        boolean testKeys = Build.TAGS != null &&
                           Build.TAGS.contains("test-keys");

        String[] paths = {
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/app/Superuser.apk"
        };

        StringBuilder found = new StringBuilder();
        for (String path : paths) {
            if (new File(path).exists()) {
                found.append("\nمؤشر موجود: ").append(path);
            }
        }

        String result =
            "مؤشرات فحص الروت\n\n" +
            "بناء test-keys: " + (testKeys ? "نعم" : "لا") +
            "\n\n" +
            (found.length() == 0
                ? "لم تظهر مؤشرات الروت في المسارات المفحوصة."
                : found.toString()) +
            "\n\nمهم: عدم ظهور المؤشرات لا يثبت أن الجهاز غير مروّت.";

        showPage("فحص مؤشرات الروت", result);
    }

    private void showStorage() {
        try {
            File path = getFilesDir();
            StatFs stat = new StatFs(path.getAbsolutePath());

            long total = stat.getTotalBytes();
            long free = stat.getAvailableBytes();

            String result =
                "المساحة المتاحة للتطبيق\n\n" +
                "الإجمالي: " + formatBytes(total) +
                "\n\nالمتاح: " + formatBytes(free) +
                "\n\nهذه معلومات مساحة القسم الذي توجد فيه ملفات التطبيق.";

            showPage("مساحة التخزين", result);
        } catch (Exception e) {
            showPage("مساحة التخزين",
                "تعذر قراءة معلومات التخزين.");
        }
    }

    private String formatBytes(long bytes) {
        double gb = bytes / (1024.0 * 1024 * 1024);
        return String.format(Locale.US, "%.2f GB", gb);
    }
}

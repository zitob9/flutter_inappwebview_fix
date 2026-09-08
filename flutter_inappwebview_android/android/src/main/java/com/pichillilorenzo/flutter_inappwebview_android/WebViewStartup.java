package com.pichillilorenzo.flutter_inappwebview_android;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewOutcomeReceiver;
import androidx.webkit.WebViewStartUpConfig;
import androidx.webkit.WebViewStartUpResult;
import androidx.webkit.WebViewStartupException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/** Process-wide startup executor; AndroidX coalesces repeated startup requests. */
public final class WebViewStartup {
  private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
    Thread thread = new Thread(runnable, "InAppWebView-Startup");
    thread.setDaemon(true);
    return thread;
  });

  public interface Callback {
    void complete(@Nullable String error);
  }

  private WebViewStartup() {}

  public static void start(@NonNull Context context, @NonNull Callback callback) {
    AtomicBoolean completed = new AtomicBoolean(false);
    Callback once = error -> {
      if (completed.compareAndSet(false, true)) callback.complete(error);
    };
    // Do not feature-check first: that can itself synchronously start Chromium.
    // AndroidX handles older providers internally and delivers callbacks on main.
    try {
      WebViewCompat.startUpWebView(context.getApplicationContext(),
          new WebViewStartUpConfig.Builder(EXECUTOR).build(),
          new WebViewOutcomeReceiver<WebViewStartUpResult, WebViewStartupException>() {
            @Override
            public void onResult(@NonNull WebViewStartUpResult result) {
              once.complete(null);
            }

            @Override
            public void onError(@NonNull WebViewStartupException error) {
              once.complete(error.getMessage() != null ? error.getMessage() : "WebView startup failed");
            }
          });
    } catch (RuntimeException error) {
      once.complete(error.getMessage() != null ? error.getMessage() : "WebView startup failed");
    }
  }
}

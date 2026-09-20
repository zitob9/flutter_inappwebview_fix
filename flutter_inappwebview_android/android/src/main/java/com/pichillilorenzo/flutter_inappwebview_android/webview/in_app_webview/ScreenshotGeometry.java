package com.pichillilorenzo.flutter_inappwebview_android.webview.in_app_webview;

/** Pixel-space geometry, computed before allocating a bitmap. */
public final class ScreenshotGeometry {
  public final int x, y, sourceWidth, sourceHeight, width, height;

  public ScreenshotGeometry(int viewWidth, int viewHeight, int x, int y,
      int sourceWidth, int sourceHeight, int requestedWidth) {
    if (viewWidth <= 0 || viewHeight <= 0 || x < 0 || y < 0 ||
        x >= viewWidth || y >= viewHeight || sourceWidth <= 0 || sourceHeight <= 0 ||
        requestedWidth <= 0) {
      throw new IllegalArgumentException("Invalid screenshot bounds");
    }
    this.x = x;
    this.y = y;
    this.sourceWidth = Math.min(sourceWidth, viewWidth - x);
    this.sourceHeight = Math.min(sourceHeight, viewHeight - y);
    this.width = requestedWidth;
    long scaledHeight = Math.max(1L, Math.round((double) this.sourceHeight * width / this.sourceWidth));
    // Return an unavailable screenshot rather than risking OOM. App previews
    // request 480 physical pixels, far below this general-purpose API ceiling.
    if (scaledHeight > Integer.MAX_VALUE || width * scaledHeight > 4_000_000L) {
      throw new IllegalArgumentException("Screenshot exceeds allocation budget");
    }
    this.height = (int) scaledHeight;
  }
}

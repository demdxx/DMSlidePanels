/**
 * @project DMSlidePanels
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Dmitry Ponomarev <demdxx@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.demdxx.ui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

public abstract class DMSlidePanelBaseView extends FrameLayout
{
  protected float overlayAlpha = 0.f;
  protected boolean fixed = false;

  public DMSlidePanelBaseView(Context context) {
    super(context);
  }

  public DMSlidePanelBaseView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DMSlidePanelBaseView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setOverlayAlpha(float v) {
    overlayAlpha = Math.min(1.f, Math.max(v, 0.f));
  }

  public void fixed(boolean state) {
    fixed = state;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Events
  //////////////////////////////////////////////////////////////////////////////////////////////////

  @SuppressWarnings("UnusedParameters")
  protected void onBeforeGoingInto(boolean lead) {
    // ...
  }

  @SuppressWarnings("UnusedParameters")
  protected void onAfterGoingInto(boolean lead) {
    // ...
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Helpers
  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * This method converts dp unit to equivalent pixels, depending on device density.
   *
   * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
   * @return A float value to represent px equivalent to dp depending on device density
   */
  public float convertDpToPixel(float dp){
    Resources r = getResources();
    if (null != r) {
      DisplayMetrics metrics = r.getDisplayMetrics();
      return dp * (metrics.densityDpi / 160f);
    }
    return dp;
  }
}

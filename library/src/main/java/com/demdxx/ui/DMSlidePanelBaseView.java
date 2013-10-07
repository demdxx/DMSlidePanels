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
import android.view.View;
import android.widget.FrameLayout;

public abstract class DMSlidePanelBaseView extends FrameLayout
{
  protected float visibleX1 = 0;
  protected float visibleX2 = 0;
  protected float leftOffset = 0;
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

  public void setPanelVisiblePoints(float x1, float x2, float lOffset) {
    visibleX1 = x1;
    visibleX2 = x2;
    leftOffset = lOffset;
  }

  public void fixed(boolean state) {
    fixed = state;
  }

  public void updateByCentralTranslation(DMSlideAnimator.Translation cur, DMSlideAnimator.Translation translation)
  {
    updateByCentralTranslation(cur.left, cur.top, cur.right, cur.bottom, translation.left, translation.right);
  }

  public void updateByCentralTranslation(float l, float t, float r, float b, float cl, float cr)
  {
    // Calculate panels visible bounds
    View parent = (View) getParent();
    if (null != parent) {
      int curWidth = parent.getWidth();
      if (r >= 0 && l <= curWidth) {
        final float left = l < 0 ? 0 : l;
        final float right = r > curWidth ? curWidth : r;

        if (cl < left) {
          cl = left;
        }
        if (cr > right) {
          cr = right;
        } else if (cr < cl) {
          cr = cl;
        }

        if (left < cl) {
          setPanelVisiblePoints(left, cl > right ? right : cl, cl);
        } else if (right > cr) {
          setPanelVisiblePoints(cr < left ? left : cr, right, cl);
        } else {
          setPanelVisiblePoints(0, 0, 0);
        }
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Events
  //////////////////////////////////////////////////////////////////////////////////////////////////

  protected void onBeforeGoingInto(boolean lead) {
    // ...
  }

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

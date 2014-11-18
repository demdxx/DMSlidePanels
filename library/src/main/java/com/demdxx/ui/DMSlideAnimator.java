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

import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

public class DMSlideAnimator extends Animation
{
  protected View panelLeft = null;
  protected View panelRight = null;
  protected View panelCenter = null;

  protected Translation panelLeftTranslationStart = null;
  protected Translation panelLeftTranslationEnd = null;

  protected Translation panelRightTranslationStart = null;
  protected Translation panelRightTranslationEnd = null;

  protected Translation panelCenterTranslationStart = null;
  protected Translation panelCenterTranslationEnd = null;

  protected Translation panelCenterTranslationCur = null;

  public DMSlideAnimator(
          View pLeft, Translation tLFrom, Translation tLTo,
          View pRight, Translation tRFrom, Translation tRTo,
          View pCenter, Translation tCFrom, Translation tCTo)
  {
    super();
    panelLeft = pLeft;
    panelLeftTranslationStart = tLFrom;
    panelLeftTranslationEnd = tLTo;
    panelRight = pRight;
    panelRightTranslationStart = tRFrom;
    panelRightTranslationEnd = tRTo;
    panelCenter = pCenter;
    panelCenterTranslationStart = tCFrom;
    panelCenterTranslationEnd = tCTo;

    setInterpolator(new DMSlidePageHintInterpolator());
  }

  @Override
  protected void applyTransformation(float interpolatedTime, Transformation t) {
    super.applyTransformation(interpolatedTime, t);

    panelCenterTranslationCur = panelCenterTranslationStart.translationTo(panelCenterTranslationEnd, interpolatedTime);

    applyLayout(interpolatedTime, panelLeft, panelLeftTranslationStart, panelLeftTranslationEnd);
    applyLayout(interpolatedTime, panelRight, panelRightTranslationStart, panelRightTranslationEnd);
    applyLayout(interpolatedTime, panelCenter, panelCenterTranslationStart, panelCenterTranslationEnd);
  }

  protected void applyLayout(float interpolatedTime, View v, Translation from, Translation to) {
    if (null != v && null != from && null != to) {

      Translation t = from == panelCenterTranslationStart
        ? panelCenterTranslationCur
        : from.translationTo(to, interpolatedTime);
      t.update(v);

      if (v instanceof DMSlidePanelView) {
        int w = panelCenterTranslationCur.width();
        float a = (float) panelCenterTranslationCur.widthOnDisplay(w) / (float) w;
        //Log.d("Alpha", a + " -> " + (a * 0.4f));
        if (from == panelCenterTranslationStart) {
          ((DMSlidePanelView) v).setOverlayAlpha(1.f - a);
        } else {
          ((DMSlidePanelView) v).setOverlayAlpha(a);
        }
        v.postInvalidateDelayed(10);
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Translation
  //////////////////////////////////////////////////////////////////////////////////////////////////

  public static class Translation {
    public int left, right, top, bottom;

    public Translation set(int left, int top, int right, int bottom) {
      this.left = left;
      this.top = top;
      this.right = right;
      this.bottom = bottom;
      return this;
    }

    public float interpolated(Translation from, Translation to, int offset) {
      if (from.left > to.left) {
        Translation f = from;
        from = to;
        to = f;
      }
      return (float)(left+offset-from.left) / (float)(to.left-from.left);
    }

    public Translation update(View v) {
      if (null != v) {
        v.layout(left, top, right, bottom);
      }
      return this;
    }

    public Translation translationTo(Translation to, float interpolatedTime) {
      return copy().update(this, to, interpolatedTime);
    }

    public Translation update(Translation from, Translation to, float interpolatedTime) {
      this.left = (int)(div(from.left, to.left) * interpolatedTime + from.left);
      this.top = (int)(div(from.top, to.top) * interpolatedTime + from.top);
      this.right = (int)(div(from.right, to.right) * interpolatedTime + from.right);
      this.bottom = (int)(div(from.bottom, to.bottom) * interpolatedTime + from.bottom);
      return this;
    }

    public Translation updateSize(View v) {
      if (null != v) {
        ViewGroup.LayoutParams l = v.getLayoutParams();
        l.width = (right + 999999) - (left + 999999);
        l.height = (bottom + 999999) - (top + 999999);
        if (l instanceof FrameLayout.LayoutParams) {
          ((FrameLayout.LayoutParams) l).leftMargin = left;
          ((FrameLayout.LayoutParams) l).topMargin = top;
          if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ((FrameLayout.LayoutParams) l).gravity = Gravity.LEFT;
          } else {
            ((FrameLayout.LayoutParams) l).gravity = Gravity.START;
          }
        }
        v.requestLayout();
      }
      return this;
    }

    public Translation copy() {
      return new Translation().set(left, top, right, bottom);
    }

    public int width() {
      int offset = Math.min(left, right);
      if (offset > 0) { offset = -offset; }
      return (right + offset) - (left + offset);
    }

    public int height() {
      return this.bottom - this.top;
    }

    public int widthOnDisplay(int displayWidth) {
      if (displayWidth <= 0) {
        displayWidth = width();
      }
      if (left > 0) {
        displayWidth -= left;
      }
      if (right > displayWidth) {
        return displayWidth;
      }
      return right;
    }

    protected static int div(int a, int b) {
      final int m = Math.min(a, b);
      if (m < 0) { a -= m; b -= m; }
      return b - a;
    }
  }
}

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

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

class DMSlideAnimator extends Animation
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

    applyLayout(interpolatedTime, panelLeft, panelLeftTranslationStart, panelLeftTranslationEnd);
    applyLayout(interpolatedTime, panelRight, panelRightTranslationStart, panelRightTranslationEnd);
    applyLayout(interpolatedTime, panelCenter, panelCenterTranslationStart, panelCenterTranslationEnd);
  }

  protected void applyLayout(float interpolatedTime, View v, Translation from, Translation to) {
    if (null != v && null != from && null != to) {
      final float l = (to.left - from.left) * interpolatedTime + from.left;
      final float t = (to.top - from.top) * interpolatedTime + from.top;
      final float r = (to.right - from.right) * interpolatedTime + from.right;
      final float b = (to.bottom - from.bottom) * interpolatedTime + from.bottom;
      v.layout((int)l, (int)t, (int)r, (int)b);

      // Calculate panels visible bounds
      if (v instanceof DMSlidePanelBaseView && panelCenter != v) {
        float cl = (panelCenterTranslationEnd.left - panelCenterTranslationStart.left)
                 * interpolatedTime + panelCenterTranslationStart.left;
        float cr = (panelCenterTranslationEnd.right - panelCenterTranslationStart.right)
                 * interpolatedTime + panelCenterTranslationStart.right;
        ((DMSlidePanelBaseView) v).updateByCentralTranslation(l, t, r, b, cl, cr);
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Translation
  //////////////////////////////////////////////////////////////////////////////////////////////////

  static class Translation {
    int left, right, top, bottom;

    Translation set(int left, int top, int right, int bottom) {
      this.left = left;
      this.top = top;
      this.right = right;
      this.bottom = bottom;
      return this;
    }

    Translation update(View v) {
      if (null != v) {
        v.layout(left, top, right, bottom);
      }
      return this;
    }

    Translation updateSize(View v) {
      if (null != v) {
        ViewGroup.LayoutParams l = v.getLayoutParams();
        l.width = (right + 0xffffff) - (left + 0xffffff);
        if (l instanceof FrameLayout.LayoutParams) {
          ((FrameLayout.LayoutParams) l).leftMargin = left;
          ((FrameLayout.LayoutParams) l).gravity = Gravity.LEFT;
        }
        v.requestLayout();
      }
      return this;
    }

    Translation copy() {
      return new Translation().set(left, top, right, bottom);
    }
  }
}

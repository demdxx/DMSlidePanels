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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

public abstract class DMSlidePanelView extends DMSlidePanelBaseView
{
  public DMSlidePanelView(Context context) {
    super(context);
  }

  public DMSlidePanelView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DMSlidePanelView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Draw
  //////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  protected void dispatchDraw(Canvas canvas) {
    // Draw the children
    super.dispatchDraw(canvas);

    if (View.VISIBLE == getVisibility()) {
      final int width = getMeasuredWidth();
      final int startShadow = shadowLeftPosition();

      canvas.save();
      canvas.translate(startShadow, 0);
      GradientDrawable d = shadowDrawable();
      d.setBounds(0, 0, shadowSize(), getMeasuredHeight());
      d.draw(canvas);
      canvas.restore();

      if (width != 0) {
        final float opennessRatio = (width - (visibleX2 - visibleX1)) / (float) width;
        onDrawPanelOverlay(canvas, opennessRatio);
        onDrawArrow(canvas, opennessRatio);
      }
    }
  }

  /**
   * Draw arrow
   * @param canvas canvas
   * @param opennessRatio ratio
   */
  protected abstract void onDrawArrow(Canvas canvas, float opennessRatio);

  private static final int MAXIMUM_MENU_ALPHA_OVERLAY = 170;
  static Paint menuOverlayPaint;

  /**
   * Draw overlay
   * @param canvas canvas
   * @param opennessRatio ratio
   */
  protected void onDrawPanelOverlay(Canvas canvas, float opennessRatio) {
    if (null == menuOverlayPaint) {
      menuOverlayPaint = new Paint();
    }
    final int alpha = (int) (MAXIMUM_MENU_ALPHA_OVERLAY * opennessRatio);
    if (alpha > 0) {
      menuOverlayPaint.setColor(Color.argb(alpha, 0, 0, 0));
      canvas.drawRect(canvas.getClipBounds(), menuOverlayPaint);
      if (opennessRatio < 1.0f) {
        postInvalidateDelayed(10);
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Helpers
  //////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public void setPanelVisiblePoints(float x1, float x2) {
    super.setPanelVisiblePoints(x1, x2);
    postInvalidateDelayed(7);
  }

  /**
   * Get shadow gradient
   * @return gradient
   */
  protected abstract GradientDrawable shadowDrawable();

  /**
   * Position from
   * @return position
   */
  protected abstract int shadowLeftPosition();

  /**
   * Get shadow size
   * @return pixels
   */
  protected int shadowSize() {
    return (int) convertDpToPixel(7);
  }
}

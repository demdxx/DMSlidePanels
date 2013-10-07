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
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class DMSlidePanelRightView extends DMSlidePanelView
{
  private GradientDrawable shadowGradientDrawable = new GradientDrawable(
          GradientDrawable.Orientation.LEFT_RIGHT, new int[] { 0x99000000, 0x00000000 });

  public DMSlidePanelRightView(Context context) {
    super(context);
  }

  public DMSlidePanelRightView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DMSlidePanelRightView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onDrawArrow(Canvas canvas, float opennessRatio) {
    // ...
  }

  /**
   * Get shadow gradient
   * @return gradient
   */
  @Override
  protected GradientDrawable shadowDrawable() {
    return shadowGradientDrawable;
  }

  /**
   * Position from
   * @return position
   */
  @Override
  protected int shadowLeftPosition() {
    if (fixed) {
      return (int) (visibleX1 - leftOffset);
    }
    return 0;
  }
}

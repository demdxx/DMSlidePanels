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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

public class DMSlidePanelsView extends FrameLayout implements Animation.AnimationListener, View.OnClickListener {
  protected View leftSidePanel = null;
  protected View rightSidePanel = null;
  protected View centerPanel = null;

  protected DMSlideAnimator.Translation leftSidePanelTranslation = new DMSlideAnimator.Translation();
  protected DMSlideAnimator.Translation rightSidePanelTranslation = new DMSlideAnimator.Translation();
  protected DMSlideAnimator.Translation centerPanelTranslation = new DMSlideAnimator.Translation();

  protected boolean sidebarFixed = true;
  protected long slideAnimationDuration = 300;
  protected int swipe = 0; // 0 - none, 1 - arbitrary, 2 - strict
  protected int activeView = 0; // 0 - center, 1 - left, 2 - right

  protected GestureDetector.SimpleOnGestureListener gestureListener;
  protected GestureDetector gestureDetector;

  protected ContainerListener listener;

  public DMSlidePanelsView(Context context) {
    super(context);
    if (!isInEditMode()) {
      initControl(context, null);
    }
  }

  public DMSlidePanelsView(Context context, AttributeSet attrs) {
    super(context, attrs);
    if (!isInEditMode()) {
      initControl(context, attrs);
    }
  }

  public DMSlidePanelsView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    if (!isInEditMode()) {
      initControl(context, attrs);
    }
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    if (null == centerPanel) {
      initPanels();
    }
  }

  /**
   * Init control params
   *
   * @param context Application context
   * @param attrs   AttributeSet
   */
  protected void initControl(Context context, AttributeSet attrs) {
    if (null != context && null != attrs) {
      // Set default params
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DMSidePanelsView);
      if (null != typedArray) {
        slideAnimationDuration = (long) typedArray.getInt(R.styleable.DMSidePanelsView_duration, (int) slideAnimationDuration);
        sidebarFixed = typedArray.getBoolean(R.styleable.DMSidePanelsView_fixed, sidebarFixed);
        swipe = typedArray.getInt(R.styleable.DMSidePanelsView_swipe, swipe);
        typedArray.recycle();
      }
    }

    // Init events
    initEvents();
  }

  /**
   * Accept view panels
   */
  public void initPanels() {
    if (null != centerPanel) {
      return;
    }

    // Assign views
    leftSidePanel = findViewById(R.id.dmslidepanels_leftside_panel);
    rightSidePanel = findViewById(R.id.dmslidepanels_rightside_panel);
    centerPanel = findViewById(R.id.dmslidepanels_central_panel);

    if (null == leftSidePanel || null == rightSidePanel || null == centerPanel) {
      for (int i = 0; i < getChildCount(); i++) {
        View v = getChildAt(i);
        if (v instanceof DMSlidePanelLeftView) {
          if (null == leftSidePanel) {
            leftSidePanel = v;
          }
        } else if (v instanceof DMSlidePanelRightView) {
          if (null == rightSidePanel) {
            rightSidePanel = v;
          }
        } else if (v instanceof DMSlidePanelCenterView) {
          if (null == centerPanel) {
            centerPanel = v;
          }
        }
      }
    }

    if (null != centerPanel) {
      centerPanel.bringToFront();

      if (null != rightSidePanel && rightSidePanel instanceof DMSlidePanelView) {
        ((DMSlidePanelView) rightSidePanel).fixed(sidebarFixed);
      }
      if (null != leftSidePanel && leftSidePanel instanceof DMSlidePanelView) {
        ((DMSlidePanelView) leftSidePanel).fixed(sidebarFixed);
      }

      // Update position
      getLeftSidePanelTranslation(true).updateSize(leftSidePanel);
      getRightSidePanelTranslation(true).updateSize(rightSidePanel);

      // Hide sidebars
      showLeftSideBar(false, false);
      showRightSideBar(false, false);

      // Init events
      initEvents();
    }
  }

  /**
   * Init events
   */
  protected void initEvents() {
    if (1 == swipe) {
      initSwipeEvents();
    } else if (2 == swipe) {
      initStrictSwipeEvents();
    } else {
      setOnTouchListener(null);
    }
  }

  /**
   * Init as draggable central panel
   */
  protected void initStrictSwipeEvents() {
    if (null != gestureDetector) {
      return;
    }
    // Init gesture director
    gestureListener = new SwipeStrictGesture();
    gestureDetector = new GestureDetector(getContext(), gestureListener);
  }

  /**
   * Init as post swipe events
   */
  protected void initSwipeEvents() {
    if (null != gestureDetector) {
      return;
    }
    // Init gesture director
    gestureListener = new SwipeGestureDetector() {
      @Override
      public void swipe2Left() {
        if (isLeftSideBarVisible()) {
          showLeftSideBar(false, true);
        } else {
          showRightSideBar(true, true);
        }
      }

      @Override
      public void swipe2Right() {
        if (isRightSideBarVisible()) {
          showRightSideBar(false, true);
        } else {
          showLeftSideBar(true, true);
        }
      }
    };
    gestureDetector = new GestureDetector(getContext(), gestureListener);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Getters/Setters
  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Set fixed state
   *
   * @param fixed boolean
   */
  public void setSidebarFixed(boolean fixed) {
    sidebarFixed = fixed;

    if (null != rightSidePanel && rightSidePanel instanceof DMSlidePanelView) {
      ((DMSlidePanelView) rightSidePanel).fixed(sidebarFixed);
    }
    if (null != leftSidePanel && leftSidePanel instanceof DMSlidePanelView) {
      ((DMSlidePanelView) leftSidePanel).fixed(sidebarFixed);
    }
  }

  /**
   * Set animation duration
   *
   * @param duration milliseconds
   */
  public void setSlideAnimationDuration(long duration) {
    slideAnimationDuration = duration;
  }

  /**
   * Set swipe type
   *
   * @param type 0 - none, 1 - arbitrary, 2 - strict
   */
  public void setSwipe(int type) {
    swipe = type;
    gestureDetector = null;
    initEvents();
  }

  /**
   * Set listener container
   *
   * @param l listener
   */
  public void setContainerListener(ContainerListener l) {
    listener = l;
  }

  public View getLeftSidePanel() {
    return leftSidePanel;
  }

  public View getRightSidePanel() {
    return rightSidePanel;
  }

  public View getCenterPanel() {
    return centerPanel;
  }

  protected int panelIndexAtPosition(float x, float y) {
    if (0 != activeView
     && x >= centerPanelTranslation.left && x <= centerPanelTranslation.right
     && y >= centerPanelTranslation.top  && y <= centerPanelTranslation.bottom)
    {
      return 0;
    }
    return activeView;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Display panels actions
  //////////////////////////////////////////////////////////////////////////////////////////////////

  public int getLeftPanelWidth() {
    return (int) (Math.min(getMeasuredWidth(), getMeasuredHeight()) * 0.8f);
  }

  public int getRightPanelWidth() {
    return (int) (Math.min(getMeasuredWidth(), getMeasuredHeight()) * 0.8f);
  }

  public boolean isLeftSideBarVisible() {
    return null != leftSidePanel && View.VISIBLE == leftSidePanel.getVisibility() && leftSidePanelTranslation.right > 0;
  }

  public boolean isRightSideBarVisible() {
    return null != rightSidePanel && View.VISIBLE == rightSidePanel.getVisibility() && rightSidePanelTranslation.left < getMeasuredWidth();
  }

  /**
   * Display left sidebar panel
   *
   * @param show boolean
   * @param animated boolean
   */
  public void showLeftSideBar(boolean show, boolean animated) {
    initPanels();
    if (null == leftSidePanel) {
      show = false;
    } else {
      leftSidePanel.setVisibility(View.VISIBLE);
    }

    activeView = show ? 1 : 0;

    onBeforeShowLeftSidebar(show);
    moveSidebars(getLeftSidePanelTranslation(show),
            getRightSidePanelTranslation(false),
            getCenterPanelTranslation(!show, false),
            animated);
  }

  /**
   * Show right sidebar panel
   *
   * @param show boolean
   * @param animated boolean
   */
  public void showRightSideBar(boolean show, boolean animated) {
    initPanels();
    if (null == rightSidePanel) {
      show = false;
    } else {
      rightSidePanel.setVisibility(View.VISIBLE);
    }

    activeView = show ? 2 : 0;

    onBeforeShowRightSidebar(show);
    moveSidebars(getLeftSidePanelTranslation(false),
            getRightSidePanelTranslation(show),
            getCenterPanelTranslation(!show, true),
            animated);
  }

  /**
   * Show central panel
   *
   * @param animated boolean
   */
  public void showCentralPanel(boolean animated) {
    if (isLeftSideBarVisible()) {
      showLeftSideBar(false, animated);
    } else if (isRightSideBarVisible()) {
      showRightSideBar(false, animated);
    }
  }

  /**
   * Left sidebar panel display toggle
   * @param animated boolean
   */
  public void toggleLeft(boolean animated) {
    showLeftSideBar(!isLeftSideBarVisible(), animated);
  }

  /**
   * Right sidebar panel display toggle
   * @param animated boolean
   */
  public void toggleRight(boolean animated) {
    showRightSideBar(!isRightSideBarVisible(), animated);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Move sidebar to
  //////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Get left position at
   *
   * @param show boolean
   * @return translation
   */
  protected DMSlideAnimator.Translation getLeftSidePanelTranslation(boolean show) {
    DMSlideAnimator.Translation l = new DMSlideAnimator.Translation();
    View left = getLeftSidePanel();
    int top = null != left ? left.getTop() : 0;
    if (show) {
      l.set(0, top, getLeftPanelWidth(), (top > 0 ? top : 0) + getMeasuredHeight());
    } else {
      l.set(-getLeftPanelWidth(), top, 0, (top > 0 ? top : 0) + getMeasuredHeight());
    }
    return l;
  }

  /**
   * Get right position at
   *
   * @param show boolean
   * @return translation
   */
  protected DMSlideAnimator.Translation getRightSidePanelTranslation(boolean show) {
    int w = getMeasuredWidth();
    DMSlideAnimator.Translation r = new DMSlideAnimator.Translation();
    View right = getRightSidePanel();
    int top = null != right ? right.getTop() : 0;
    if (show) {
      r.set(w - getRightPanelWidth(), top, w, (top > 0 ? top : 0) + getMeasuredHeight());
    } else {
      r.set(w, top, w + getRightPanelWidth(), (top > 0 ? top : 0) + getMeasuredHeight());
    }
    return r;
  }

  /**
   * Get end position at
   *
   * @param show  boolean
   * @param right boolean
   * @return translation
   */
  protected DMSlideAnimator.Translation getCenterPanelTranslation(boolean show, boolean right) {
    DMSlideAnimator.Translation c = centerPanelTranslation.copy();
    c.top = getCenterPanel().getTop();
    c.bottom = c.top + getMeasuredHeight();
    if (show) {
      c.right = getMeasuredWidth();
      c.left = 0;
    } else if (right) {
      c.left = -getLeftPanelWidth();
      c.right = getMeasuredWidth() + c.left;
    } else {
      c.left = getLeftPanelWidth();
      c.right = getMeasuredWidth() + c.left;
    }
    return c;
  }

  /**
   * Change layers position
   *
   * @param left     translation
   * @param right    translation
   * @param center   translation
   * @param animated boolean
   */
  protected void moveSidebars(DMSlideAnimator.Translation left,
                              DMSlideAnimator.Translation right,
                              DMSlideAnimator.Translation center,
                              boolean animated) {
    if (animated) {
      // Clear animation
      clearAnimation();

      DMSlideAnimator animator;
      if (sidebarFixed) {
        leftSidePanelTranslation = getLeftSidePanelTranslation(centerPanelTranslation.left > 0 || 0 == left.left);
        rightSidePanelTranslation = getRightSidePanelTranslation(centerPanelTranslation.left < 0 || center.left < 0);
        animator = new DMSlideAnimator(leftSidePanel, leftSidePanelTranslation, leftSidePanelTranslation,
                                       rightSidePanel, rightSidePanelTranslation, rightSidePanelTranslation,
                                       centerPanel, centerPanelTranslation.copy(), center);
      } else {
        animator = new DMSlideAnimator(leftSidePanel, leftSidePanelTranslation.copy(), left,
                                       rightSidePanel, rightSidePanelTranslation.copy(), right,
                                       centerPanel, centerPanelTranslation.copy(), center);
      }

      // update translation
      centerPanelTranslation = center;
      leftSidePanelTranslation = left;
      rightSidePanelTranslation = right;

      animator.setDuration(slideAnimationDuration);
      animator.setInterpolator(new LinearInterpolator());
      animator.setAnimationListener(this);
      startAnimation(animator);
    } else {
      leftSidePanelTranslation = left;
      rightSidePanelTranslation = right;
      centerPanelTranslation = center;
      updateLayout();
    }
  }

  /**
   * Update position and delete animations from panels
   */
  protected void updateLayout() {
    clearAnimation();
    if (null != leftSidePanel) {
      if (!sidebarFixed || leftSidePanelTranslation.left >= 0) {
        leftSidePanelTranslation.updateSize(leftSidePanel);
      }
      if (leftSidePanelTranslation.left < 0) {
        getLeftSidePanelTranslation(false).updateSize(leftSidePanel);
      }
    }
    if (null != rightSidePanel) {
      int width = getMeasuredWidth();
      if (!sidebarFixed || rightSidePanelTranslation.right <= width) {
        rightSidePanelTranslation.updateSize(rightSidePanel);
      }
      if (rightSidePanelTranslation.left >= width) {
        getRightSidePanelTranslation(false).updateSize(rightSidePanel);
      }
    }
    if (null != centerPanel) {
      centerPanelTranslation.updateSize(centerPanel);
    }

    // End events
    onAfterShowSidebar();
  }

  /**
   * Update panels state by central panel state
   */
  protected void updatePanelsPosition() {
    if (null != leftSidePanel) {
      if (centerPanelTranslation.left > 0) {
        leftSidePanel.setVisibility(View.VISIBLE);
        leftSidePanelTranslation.update(leftSidePanel);
        if (leftSidePanel instanceof DMSlidePanelBaseView) {
          ((DMSlidePanelBaseView) leftSidePanel)
                  .updateByCentralTranslation(
                          getLeftSidePanelTranslation(true),
                          centerPanelTranslation);
        }
      } else {
        getLeftSidePanelTranslation(false).update(leftSidePanel);
      }
    }
    if (null != rightSidePanel) {
      if (centerPanelTranslation.right < getMeasuredWidth()) {
        rightSidePanel.setVisibility(View.VISIBLE);
        rightSidePanelTranslation.update(rightSidePanel);
        if (rightSidePanel instanceof DMSlidePanelBaseView) {
          ((DMSlidePanelBaseView) rightSidePanel)
                  .updateByCentralTranslation(
                          getRightSidePanelTranslation(true),
                          centerPanelTranslation);
        }
      } else {
        getRightSidePanelTranslation(false).update(rightSidePanel);
      }
    }
  }

  /**
   * To complete reposition
   */
  protected void updatePanelsPositionAnimation() {
    long oldDuration = slideAnimationDuration;
    slideAnimationDuration /= 2;
    if (1 == activeView) {
      showLeftSideBar(centerPanelTranslation.left >= getLeftPanelWidth() / 1.3f, true);
    } else if (2 == activeView) {
      showRightSideBar(getMeasuredWidth() - centerPanelTranslation.right >= getLeftPanelWidth() / 1.3f, true);
    } else {
      // If invisible panels
      int left = centerPanelTranslation.left;
      int right = getMeasuredWidth() - centerPanelTranslation.right;
      if (left > right) {
        showLeftSideBar(centerPanelTranslation.left >= getLeftPanelWidth() / 4.0f, true);
      } else if (left < right) {
        showRightSideBar(right >= getLeftPanelWidth() / 4.0f, true);
      } else {
        showCentralPanel(true);
      }
    }

    // Restore defaults
    if (null != rightSidePanel && rightSidePanel instanceof DMSlidePanelView) {
      ((DMSlidePanelView) rightSidePanel).fixed(sidebarFixed);
    }
    if (null != leftSidePanel && leftSidePanel instanceof DMSlidePanelView) {
      ((DMSlidePanelView) leftSidePanel).fixed(sidebarFixed);
    }

    slideAnimationDuration = oldDuration;

    // Event
    onAfterShowSidebar();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Events
  //////////////////////////////////////////////////////////////////////////////////////////////////

  @Override
  public boolean dispatchTouchEvent (MotionEvent ev) {
    if (null != gestureDetector) {
      if (MotionEvent.ACTION_UP == ev.getAction()) {
        if (gestureListener instanceof SwipeStrictGesture
        && ((SwipeStrictGesture) gestureListener).isDragged()) {
          ev.setAction(MotionEvent.ACTION_CANCEL);
          updatePanelsPositionAnimation();
        }
      }

      // Process event
      gestureDetector.onTouchEvent(ev);

      // Check tap event
      if (MotionEvent.ACTION_UP == ev.getAction()) {
        boolean tapFlag;
        if (gestureListener instanceof SwipeStrictGesture) {
          tapFlag = ((SwipeStrictGesture) gestureListener).isTap();
        } else {
          tapFlag = ((SwipeGestureDetector) gestureListener).isTap();
        }

        // Display panel
        if (tapFlag && 0 != activeView) {
          if (0 == panelIndexAtPosition(ev.getX(), ev.getY())) {
            showCentralPanel(true);
            ev.setAction(MotionEvent.ACTION_CANCEL);
          }
        } else if (2 == swipe) {
          updatePanelsPositionAnimation();
        }
      }
    }
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public void onAnimationStart(Animation animation) {
    // hollow...
  }

  @Override
  public void onAnimationEnd(Animation animation) {
    updateLayout();
  }

  @Override
  public void onAnimationRepeat(Animation animation) {
    // hollow...
  }

  @Override
  public void onClick(View view) {
    if (view == centerPanel) {
      showCentralPanel(true);
    }
  }

  protected void onBeforeShowLeftSidebar(boolean show) {
    if (null != leftSidePanel && leftSidePanel instanceof DMSlidePanelBaseView) {
      ((DMSlidePanelBaseView) leftSidePanel).onBeforeGoingInto(show);
    }
    if (null != rightSidePanel && rightSidePanel instanceof DMSlidePanelBaseView) {
      ((DMSlidePanelBaseView) rightSidePanel).onBeforeGoingInto(false);
    }
    if (centerPanel instanceof DMSlidePanelBaseView) {
      ((DMSlidePanelBaseView) centerPanel).onBeforeGoingInto(!show);
    }
    if (null != listener) {
      listener.onSideContainerBeforeShowLeftSidebar(show);
    }
  }

  protected void onBeforeShowRightSidebar(boolean show) {
    if (null != leftSidePanel && leftSidePanel instanceof DMSlidePanelBaseView) {
      ((DMSlidePanelBaseView) leftSidePanel).onBeforeGoingInto(false);
    }
    if (null != rightSidePanel && rightSidePanel instanceof DMSlidePanelBaseView) {
      ((DMSlidePanelBaseView) rightSidePanel).onBeforeGoingInto(show);
    }
    if (centerPanel instanceof DMSlidePanelBaseView) {
      ((DMSlidePanelBaseView) centerPanel).onBeforeGoingInto(show);
    }
    if (null != listener) {
      listener.onSideContainerBeforeShowRightSidebar(show);
    }
  }

  protected void onAfterShowSidebar() {
    boolean shownLeft = View.VISIBLE == leftSidePanel.getVisibility();
    boolean shownRight = View.VISIBLE == leftSidePanel.getVisibility();
    if (leftSidePanel instanceof DMSlidePanelBaseView) {
      ((DMSlidePanelBaseView) leftSidePanel).onAfterGoingInto(shownLeft);
    }
    if (rightSidePanel instanceof DMSlidePanelBaseView) {
      ((DMSlidePanelBaseView) rightSidePanel).onAfterGoingInto(shownRight);
    }
    if (centerPanel instanceof DMSlidePanelBaseView) {
      ((DMSlidePanelBaseView) centerPanel).onAfterGoingInto(!shownLeft && !shownRight);
    }
    if (null != listener) {
      listener.onSideContainerAfterShowSidebar(shownLeft, shownRight);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Swipe gesture
  //////////////////////////////////////////////////////////////////////////////////////////////////

  class SwipeStrictGesture extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 5;

    float oldXPosition = 0.0f;
    float oldYPosition = 0.0f;
    Boolean horizontalDrag = null;
    boolean isTap = false;

    @Override
    public boolean onDown(MotionEvent e) {
      clearAnimation();
      if (null == centerPanel) {
        initPanels();
      }
      oldXPosition = e.getX();
      oldYPosition = e.getY();
      horizontalDrag = null;
      isTap = false;
      return true;
    }

    public boolean isDragged() {
      return null != horizontalDrag && horizontalDrag;
    }

    public boolean isTap() {
      return isTap;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
      isTap = true;
      return super.onSingleTapUp(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      if (null != horizontalDrag && !horizontalDrag) {
        return true;
      }

      // Calculate offset
      int width = getMeasuredWidth();
      int offset = (int) (e2.getX() - oldXPosition);

      if (null == horizontalDrag) {
        // Calculate vertical offset
        int vOffset = (int) Math.abs(oldYPosition - e2.getY());
        if (vOffset >= SWIPE_MIN_DISTANCE && vOffset > offset) {
          horizontalDrag = false;
          return true;
        }
        if (Math.abs(offset) >= SWIPE_MIN_DISTANCE) {
          horizontalDrag = true;
        } else {
          return true;
        }
      }

      // Update central panel
      centerPanelTranslation.left += offset;
      centerPanelTranslation.right += offset;

      if ((null == leftSidePanel && centerPanelTranslation.left > 0)
       || (null == rightSidePanel && centerPanelTranslation.left < 0)) {
        centerPanelTranslation.right = width;
        centerPanelTranslation.left = 0;
      }

      if (!sidebarFixed) {
        // Calc left panel position
        leftSidePanelTranslation.left += offset;
        leftSidePanelTranslation.right += offset;

        if (leftSidePanelTranslation.left > 0 || centerPanelTranslation.left > leftSidePanelTranslation.right) {
          leftSidePanelTranslation.right = getLeftPanelWidth();
          leftSidePanelTranslation.left = 0;
        }
        if (leftSidePanelTranslation.right > centerPanelTranslation.left) {
          int off = leftSidePanelTranslation.right - centerPanelTranslation.left;
          leftSidePanelTranslation.right = centerPanelTranslation.left;
          leftSidePanelTranslation.left -= off;
        }

        leftSidePanelTranslation.update(leftSidePanel);

        // Calc right panel position
        rightSidePanelTranslation.left += offset;
        rightSidePanelTranslation.right += offset;

        if (rightSidePanelTranslation.right < width || centerPanelTranslation.right < rightSidePanelTranslation.left) {
          rightSidePanelTranslation.right = width;
          rightSidePanelTranslation.left = width - getRightPanelWidth();
        }
        if (centerPanelTranslation.right > rightSidePanelTranslation.left) {
          int off = centerPanelTranslation.right - rightSidePanelTranslation.left;
          rightSidePanelTranslation.right += off;
          rightSidePanelTranslation.left = centerPanelTranslation.right;
        }

        rightSidePanelTranslation.update(rightSidePanel);
      } else {
        if (centerPanelTranslation.left > 0 && leftSidePanelTranslation.left < 0) {
          leftSidePanelTranslation = getLeftSidePanelTranslation(true);
        } else if (centerPanelTranslation.left < 0 && rightSidePanelTranslation.right > width) {
          rightSidePanelTranslation = getRightSidePanelTranslation(true);
        }

        // Correct bounds
        if (centerPanelTranslation.left > leftSidePanelTranslation.right) {
          centerPanelTranslation.left = leftSidePanelTranslation.right;
          centerPanelTranslation.right = centerPanelTranslation.left + width;
        } else if (centerPanelTranslation.right < rightSidePanelTranslation.left) {
          centerPanelTranslation.right = rightSidePanelTranslation.left;
          centerPanelTranslation.left = centerPanelTranslation.right - width;
        }
      }
      centerPanelTranslation.update(centerPanel);

      oldXPosition = e2.getX();

      // Restore Y position
      e2.setLocation(oldXPosition, oldYPosition);

      // Update panels
      updatePanelsPosition();

      return true;
    }
  }

  abstract class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    boolean isTap = false;

    public SwipeGestureDetector() {
      super();
    }

    public boolean isTap() {
      return isTap;
    }

    @Override
    public boolean onDown(MotionEvent e) {
      isTap = false;
      return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
      isTap = true;
      return super.onSingleTapUp(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      try {
        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
          return false;
        }
        // right to left swipe
        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
          swipe2Left();
        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
          swipe2Right();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return false;
    }

    public abstract void swipe2Left();

    public abstract void swipe2Right();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  /// Listener interface
  //////////////////////////////////////////////////////////////////////////////////////////////////

  public interface ContainerListener {
    public void onSideContainerBeforeShowLeftSidebar(boolean show);
    public void onSideContainerBeforeShowRightSidebar(boolean show);
    public void onSideContainerAfterShowSidebar(boolean isLeft, boolean isRight);
  }
}

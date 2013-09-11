DMSlidePanels
=============

Android Sliding App Panels.

    @copyright Dmitry Ponomarev <demdxx@gmail.com> 2013
    @license MIT

    NOTE: Some parts of the code are taken here
          http://cyrilmottier.com/2012/06/08/the-making-of-prixing-3-polishing-the-sliding-app-menu/

Example
-------

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.demdxx.ui.DMSlidePanelsView
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:panel="http://schemas.android.com/apk/res-auto/com.demdxx.ui"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/panels"
        panel:duration="300"
        panel:fixed="false"
        panel:swipe="strict">

  <com.demdxx.ui.DMSlidePanelLeftView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/leftmenu_background">
  
    <!-- ... -->
  
  </com.demdxx.ui.DMSlidePanelLeftView>
  
  <com.demdxx.ui.DMSlidePanelRightView
          android:layout_width="match_parent"
          android:layout_height="match_parent">
        
    <!-- ... -->
        
  </com.demdxx.ui.DMSlidePanelRightView>

  <com.demdxx.ui.DMSlidePanelCenterView
          android:layout_width="match_parent"
          android:layout_height="match_parent">

     <!-- ... -->

  </com.demdxx.ui.DMSlidePanelCenterView>
</com.demdxx.ui.DMSlidePanelsView>
```


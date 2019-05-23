package com.demo.douyinvideoplay.viewpager2;


import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;



/**
 * {@link ViewPager2} adapters should implement this interface to be called during
 * {@link View#onSaveInstanceState()} and {@link View#onRestoreInstanceState(Parcelable)}
 */
public interface StatefulAdapter {
    /** Saves adapter state */
    @NonNull
    Parcelable saveState();

    /** Restores adapter state */
    void restoreState(@NonNull Parcelable savedState);
}

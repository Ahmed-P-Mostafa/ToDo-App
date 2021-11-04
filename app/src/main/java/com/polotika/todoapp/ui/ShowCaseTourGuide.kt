package com.polotika.todoapp.ui

import android.content.Context
import android.view.View
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity

object ShowCaseTourGuide {
    private var listener: TourGuideCallbacks? = null
    fun setListener(callbacks: TourGuideCallbacks) {
        listener = callbacks
    }


    fun showCaseNewNoteButton(view: View, context: Context) {
        GuideView.Builder(context).setTitle("New note")
            .setContentText("Click here to navigate for new note page so you can add new note")
            .setTargetView(view)
            .setContentTextSize(12)//optional
            .setTitleTextSize(14)//optional
            .setGravity(Gravity.auto)
            .setDismissType(DismissType.targetView) //optional - default dismissible by TargetView
            .setGuideListener {
                listener?.onNewNoteDoneCallback()
            }
            .build()
            .show()

    }

    fun showCaseSwipeToDelete(view: View, context: Context) {
        GuideView.Builder(context).setTitle("").setContentText("").setTargetView(view)
            .setContentTextSize(12)//optional
            .setTitleTextSize(14)//optional
            .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
            .setGuideListener {
                listener?.onSwipeDoneCallback()
            }
            .build()
            .show()
    }

    fun showCaseOverflowMenu(view: View, context: Context) {
        GuideView.Builder(context).setTitle("").setContentText("").setTargetView(view)
            .setContentTextSize(12)//optional
            .setTitleTextSize(14)//optional
            .setDismissType(DismissType.targetView) //optional - default dismissible by TargetView
            .setGuideListener {
                listener?.onOverflowMeuDoneCallback()
            }
            .build()
            .show()
    }

    fun showCaseSearch(view: View, context: Context) {
        GuideView.Builder(context).setTitle("New note")
            .setContentText("Click here to navigate for new note page so you can add new note")
            .setTargetView(view)
            .setContentTextSize(12)//optional
            .setTitleTextSize(14)//optional
            .setDismissType(DismissType.anywhere) //optional - default dismissible by TargetView
            .setGuideListener {
                listener?.onSearchDoneCallback()
            }
            .build()
            .show()
    }


}

interface TourGuideCallbacks {
    fun onNewNoteDoneCallback()
    fun onSwipeDoneCallback()
    fun onDeleteAllDoneCallback()
    fun onSearchDoneCallback()
    fun onOverflowMeuDoneCallback()
}
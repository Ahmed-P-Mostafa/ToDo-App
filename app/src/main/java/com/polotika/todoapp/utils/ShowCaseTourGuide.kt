package com.polotika.todoapp.utils

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

    fun showCaseSearch(view: View, context: Context) {
        GuideView.Builder(context).setTitle("Search for note")
            .setContentText("Here you can look up for any note by its title.")
            .setTargetView(view)
            .setContentTextSize(12)//optional
            .setTitleTextSize(14)//optional
            .setDismissType(DismissType.selfView) //optional - default dismissible by TargetView
            .setGuideListener {
                listener?.onSearchDoneCallback()
            }
            .build()
            .show()
    }

    fun showCaseOverflowMenu(view: View, context: Context) {
        GuideView.Builder(context).setTitle("other options menu")
            .setContentText("Click here to expand the menu and show the other options like:\n\'Sorting types\' and \n\'Delete All the notes\'")
            .setTargetView(view)
            .setContentTextSize(12)//optional
            .setTitleTextSize(14)//optional
            .setDismissType(DismissType.targetView) //optional - default dismissible by TargetView
            .setGuideListener {
               // listener?.onOverflowMeuDoneCallback()
            }
            .build()
            .show()
    }

    fun showCaseSwipeToDelete(view: View, context: Context) {
        GuideView.Builder(context).setTitle("Swipe to delete")
            .setContentText("Swipe note to left side so you can delete it easily")
            .setTargetView(view)
            .setContentTextSize(12)//optional
            .setTitleTextSize(14)//optional
            .setDismissType(DismissType.selfView) //optional - default dismissible by TargetView
            .setGuideListener {
                listener?.onSwipeDoneCallback()
            }
            .build()
            .show()
    }

    fun showCaseNewNoteButton(view: View, context: Context) {
        // ToDo make it target view before production
        GuideView.Builder(context).setTitle("New note")
            .setContentText("Click here to navigate for new note page so you can add new note")
            .setTargetView(view)
            .setContentTextSize(12)//optional
            .setTitleTextSize(14)//optional
            .setGravity(Gravity.auto)
            .setDismissType(DismissType.selfView) //optional - default dismissible by TargetView
            .setGuideListener {
                listener?.onNewNoteDoneCallback()
            }
            .build()
            .show()

    }
}

interface TourGuideCallbacks {
    fun onNewNoteDoneCallback()
    fun onSwipeDoneCallback()
    fun onSearchDoneCallback()
    //fun onOverflowMeuDoneCallback()
}
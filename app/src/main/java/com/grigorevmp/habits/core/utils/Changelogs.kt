package com.grigorevmp.habits.core.utils

import android.content.Context
import com.grigorevmp.habits.R

object Changelogs {

    const val version = 3

    fun getVersions(context: Context) = arrayListOf(
        context.getString(R.string.changelog_v_1),
        context.getString(R.string.changelog_v_2),
        context.getString(R.string.changelog_v_3),
        context.getString(R.string.changelog_v_4),
    )
}
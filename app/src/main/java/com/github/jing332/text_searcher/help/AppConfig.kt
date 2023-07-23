package com.github.jing332.text_searcher.help

import android.content.Context
import com.funny.data_saver.core.DataSaverPreferences
import com.funny.data_saver.core.mutableDataSaverStateOf
import com.github.jing332.text_searcher.app
import com.github.jing332.text_searcher.const.ConfigConst


object AppConfig {
    val dataSaverPref = DataSaverPreferences(app.getSharedPreferences("app", 0))

    var lastSourceId = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_LAST_SOURCE_ID,
        initialValue = 0L
    )

    var fontDir = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_FONT_DIR,
        initialValue = ""
    )

    var isWindowFullScreen = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_IS_WINDOW_FULL_SCREEN,
        initialValue = false
    )

    fun fillDefaultValues(context: Context) {
//        if (systemPrompt.value.isEmpty())
//            systemPrompt.value = context.getString(R.string.gpt_system_prompt)
//
//        if (msgTemplate.value.isEmpty())
//            msgTemplate.value = context.getString(R.string.message_template)
//
//        if (testText.value.isEmpty())
//            testText.value = context.getString(R.string.test_text_sample)
//
//        if (openAiModel.value.isEmpty())
//            openAiModel.value = ConfigConst.VALUE_OPEN_AI_MODEL
    }
}
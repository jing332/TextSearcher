package com.github.jing332.text_searcher.help

import android.content.Context
import com.funny.data_saver.core.DataSaverPreferences
import com.funny.data_saver.core.mutableDataSaverStateOf
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.app
import com.github.jing332.text_searcher.const.ConfigConst


object AppConfig {
    val dataSaverPref = DataSaverPreferences(app.getSharedPreferences("app", 0))

    val openAiApiKey = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_OPEN_AI_API_KEY,
        initialValue = ""
    )

    val openAiModel = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_OPEN_AI_MODEL,
        initialValue = ConfigConst.VALUE_OPEN_AI_MODEL
    )

    val msgTemplate = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_MSG_TEMPLATE,
        initialValue = ""
    )

    val systemPrompt = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_SYSTEM_PROMPT,
        initialValue = ""
    )

    var fontDir = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_FONT_DIR,
        initialValue = ""
    )

    var gptTitleFontSize = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_GPT_TITLE_FONT_SIZE,
        initialValue = ConfigConst.VALUE_GPT_TITLE_FONT_SIZE
    )

    var gptTitleFontWeight = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_GPT_TITLE_FONT_WEIGHT,
        initialValue = ConfigConst.VALUE_GPT_TITLE_FONT_WEIGHT
    )

    var gptTitleLineHeight = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_GPT_TITLE_LINE_HEIGHT,
        initialValue = ConfigConst.VALUE_GPT_TITLE_LINE_HEIGHT
    )

    var gptFontSize = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_GPT_FONT_SIZE,
        initialValue = ConfigConst.VALUE_GPT_FONT_SIZE
    )

    var gptFontWeight = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_GPT_FONT_WEIGHT,
        initialValue = ConfigConst.VALUE_GPT_FONT_WEIGHT
    )

    var gptLineHeightScale = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_GPT_LINE_HEIGHT_SCALE,
        initialValue = ConfigConst.VALUE_GPT_LINE_HEIGHT_SCALE
    )

    val testText = mutableDataSaverStateOf(
        dataSaverInterface = dataSaverPref,
        key = ConfigConst.KEY_TEST_TEXT,
        initialValue = ""
    )

    fun fillDefaultValues(context: Context) {
        if (systemPrompt.value.isEmpty())
            systemPrompt.value = context.getString(R.string.system_prompt)

        if (msgTemplate.value.isEmpty())
            msgTemplate.value = context.getString(R.string.message_template)

        if (testText.value.isEmpty())
            testText.value = context.getString(R.string.test_text_sample)

        if (openAiModel.value.isEmpty())
            openAiModel.value = ConfigConst.VALUE_OPEN_AI_MODEL
    }
}
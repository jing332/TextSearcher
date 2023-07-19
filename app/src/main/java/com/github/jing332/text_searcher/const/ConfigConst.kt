package com.github.jing332.text_searcher.const

import androidx.compose.ui.text.font.FontWeight

object ConfigConst {
    const val KEY_FONT_DIR = "font_dir"
    const val KEY_GPT_TITLE_FONT_SIZE = "gpt_title_font_size"
    const val KEY_GPT_TITLE_FONT_WEIGHT = "gpt_title_font_weight"
    const val KEY_GPT_TITLE_LINE_HEIGHT = "gpt_title_line_height"

    const val KEY_GPT_FONT_SIZE = "gpt_font_size"
    const val KEY_GPT_FONT_WEIGHT = "gpt_font_weight"
    const val KEY_GPT_LINE_HEIGHT_SCALE = "gpt_line_height_scale"

    const val KEY_OPEN_AI_MODEL = "open_ai_model"
    const val KEY_OPEN_AI_API_KEY = "open_ai_api_key"
    const val KEY_MSG_TEMPLATE = "message_template"
    const val KEY_SYSTEM_PROMPT = "system_prompts"

    const val KEY_TEST_TEXT = "test_text"

    const val VALUE_OPEN_AI_MODEL = "gpt-3.5-turbo"
    const val VALUE_GPT_FONT_SIZE = 16
    const val VALUE_GPT_TITLE_FONT_SIZE = 18

    /* 👇 默认值 */
    const val VALUE_GPT_LINE_HEIGHT_SCALE = 1.2f
    const val VALUE_GPT_TITLE_LINE_HEIGHT = 1f
    val VALUE_GPT_TITLE_FONT_WEIGHT = FontWeight.Normal.weight
    val VALUE_GPT_FONT_WEIGHT = FontWeight.Normal.weight
}

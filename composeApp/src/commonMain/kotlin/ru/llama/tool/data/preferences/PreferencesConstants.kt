package ru.llama.tool.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey

object PreferencesConstants {

    val PREF_KEY_IS_DARK_MODEL = booleanPreferencesKey(SettingConst.KEY_IS_DARK_MODE)

    val PREF_KEY_AI_PROP_TEMP = doublePreferencesKey(SettingConst.AI_PROP_TEMP)

    val PREF_KEY_AI_PROP_TOP_P = doublePreferencesKey(SettingConst.AI_PROP_TOP_P)

    val PREF_KEY_AI_PROP_MAX_TOKENS = doublePreferencesKey(SettingConst.AI_PROP_MAX_TOKENS)


}
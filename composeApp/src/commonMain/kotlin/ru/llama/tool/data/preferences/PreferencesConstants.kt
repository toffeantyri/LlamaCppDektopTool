package ru.llama.tool.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesConstants {

    val PREF_KEY_IS_DARK_MODEL = booleanPreferencesKey(SettingConst.KEY_IS_DARK_MODE)

    val PREF_KEY_DEFAULT_SYSTEM_PROMPT = stringPreferencesKey(SettingConst.DEFAULT_SYSTEM_PROMPT)



}
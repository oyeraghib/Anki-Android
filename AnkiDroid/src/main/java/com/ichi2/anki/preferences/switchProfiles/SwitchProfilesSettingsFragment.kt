/***************************************************************************************
 * Copyright (c) 2025 Mohd Raghib <raghib.khan76@gmail.com>                           *  *
 *                                                                                      *
 * This program is free software; you can redistribute it and/or modify it under        *
 * the terms of the GNU General Public License as published by the Free Software        *
 * Foundation; either version 3 of the License, or (at your option) any later           *
 * version.                                                                             *
 *                                                                                      *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY      *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A      *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.             *
 *                                                                                      *
 * You should have received a copy of the GNU General Public License along with         *
 * this program.  If not, see <http://www.gnu.org/licenses/>.                           *
 ****************************************************************************************/
package com.ichi2.anki.preferences.switchProfiles

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.ichi2.anki.R
import com.ichi2.anki.preferences.SettingsFragment
import com.ichi2.anki.preferences.requirePreference
import com.ichi2.anki.showThemedToast
import timber.log.Timber

class SwitchProfilesSettingsFragment : SettingsFragment() {
    override val preferenceResource: Int
        get() = R.xml.preferences_switch_profiles

    override val analyticsScreenNameConstant: String
        get() = "pref.switchProfiles"

    private val profilesSharedPrefs = "profiles_prefs"

    override fun initSubscreen() {
        requirePreference<ProfileListPreference>(getString(R.string.pref_switch_profiles_screen_key)).apply {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("inside onCreate")
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val prefs = requireContext().getSharedPreferences(profilesSharedPrefs, Context.MODE_PRIVATE)
        if (!prefs.contains("AnkiDroid")) {
            prefs.edit().putString("AnkiDroid", "default").apply() // folder name -> display_name
        }

        val profiles = getAllProfiles(requireContext())
        for ((folder, name) in profiles) {
            Timber.d("folder: $folder, profile: $name")
        }
    }

    private fun getAllProfiles(context: Context): Map<String, String> {
        val prefs = context.getSharedPreferences(profilesSharedPrefs, Context.MODE_PRIVATE)
        return prefs.all.filterValues { it is String }.mapValues { it.value as String }
    }

    private val menuProvider =
        object : MenuProvider {
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater,
            ) {
                menuInflater.inflate(R.menu.switch_profiles_menu, menu)
                Timber.d("Menu created")
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                Timber.d("Menu item selected: ${menuItem.title}")
                return when (menuItem.itemId) {
                    R.id.action_add_profile -> {
                        showThemedToast(requireContext(), "Add Profile", true)
                        true
                    }
                    else -> false
                }
            }
        }
}

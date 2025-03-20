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

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.ichi2.anki.CollectionHelper
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
        Timber.d("inside onViewCreated")

        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
        Timber.d("Fragment attached to: ${requireActivity()::class.java.simpleName}")

        // TODO: We trigger this via the dialog to add a profile
        val path = CollectionHelper.getCurrentAnkiDroidDirectory(requireContext())
        // TODO: using hardcoded value of path for testing purpose
        val tempPath = "/storage/emulated/0/Android/data/com.ichi2.anki.debug/files/"
        val profileName = "oyeraghib"
        CollectionHelper.createProfileDirectory(tempPath, profileName)
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

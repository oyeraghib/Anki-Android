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
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ichi2.anki.R
import com.ichi2.anki.showThemedToast

class ProfileListPreference(
    context: Context,
    attrs: AttributeSet,
) : Preference(context, attrs) {
    private val profileList = mutableListOf<Profile>()
    private lateinit var adapter: SwitchProfilesAdapter

    init {
        layoutResource = R.layout.switch_profiles_list_item
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val recyclerView = holder.findViewById(R.id.recycler_profiles) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter =
            SwitchProfilesAdapter(context, profileList) { action ->
            }
        recyclerView.adapter = adapter

        loadProfiles()
    }

    private fun loadProfiles() {
        profileList.clear()
        profileList.addAll(
            listOf(
                Profile(1, "David"),
                Profile(2, "oyeraghib"),
                Profile(3, "Ashish"),
                Profile(4, "Mike"),
                Profile(5, "Arthur"),
            ),
        )
        adapter.notifyDataSetChanged()
    }

    private fun renameProfile(profile: Profile) {
        // Implement rename functionality
        showThemedToast(context, "Rename: $profile", true)
    }

    private fun deleteProfile(profile: Profile) {
        // Implement delete functionality
        showThemedToast(context, "Delete: $profile", true)
    }
}

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ichi2.anki.R
import com.ichi2.anki.showThemedToast

class SwitchProfilesAdapter(
    private val context: Context,
    private val profiles: List<Profile>,
    private val onActionClick: (Profile) -> Unit,
    private val onProfileRenameClicked: (Profile) -> Unit,
    private val onProfileDeleteClicked: (Profile) -> Unit,
) : RecyclerView.Adapter<SwitchProfilesAdapter.ProfileViewHolder>() {
    private var selectedPos = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.switch_profile_item, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProfileViewHolder,
        position: Int,
    ) {
        val profile = profiles[position]
        holder.bind(
            profile = profile,
            context = context,
            onActionClick = onActionClick,
            onProfileRenameClicked = onProfileRenameClicked,
            onProfileDeleteClicked = onProfileDeleteClicked,
        )

        // update selection state
        holder.itemView.isSelected = (position == selectedPos)

        holder.itemView.setOnClickListener {
            selectedPos = position
            notifyDataSetChanged() // TODO: Use list adapter
            showThemedToast(context, "Profile ${profile.name} selected", true)
        }
    }

    override fun getItemCount() = profiles.size

    class ProfileViewHolder(
        view: View,
    ) : RecyclerView.ViewHolder(view) {
        private val profileAvatar: TextView = view.findViewById(R.id.profile_avatar)
        private val profileName: TextView = view.findViewById(R.id.profile_name)
        private val profileRename: FrameLayout = view.findViewById(R.id.rename_profile)
        private val profileDelete: FrameLayout = view.findViewById(R.id.delete_profile)

        fun bind(
            profile: Profile,
            onActionClick: (Profile) -> Unit,
            onProfileRenameClicked: (Profile) -> Unit,
            onProfileDeleteClicked: (Profile) -> Unit,
            context: Context,
        ) {
            profileAvatar.text = getInitials(profile.name)
            profileName.text = profile.name

            profileRename.setOnClickListener {
                onProfileRenameClicked(profile)
            }

            profileDelete.setOnClickListener {
                onProfileDeleteClicked(profile)
            }
        }

        private fun getInitials(name: String): String =
            name
                .split(" ")
                .mapNotNull {
                    it.firstOrNull()?.toString()
                }.joinToString("")
                .take(2)
                .uppercase()
    }
}

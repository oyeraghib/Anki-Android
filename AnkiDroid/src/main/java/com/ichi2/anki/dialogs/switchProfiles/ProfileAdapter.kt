/***************************************************************************************
 * Copyright (c) 2025 Mohd Raghib <raghib.khan76@gmail.com>                              *
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

package com.ichi2.anki.dialogs.switchProfiles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ichi2.anki.R

class ProfileAdapter(
    private val profiles: List<String>,
    private val onClick: (String) -> Unit,
    private val context: Context,
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {
    private var selectedPosition = 0 // Default: First profile selected

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_items_switch_profiles, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProfileViewHolder,
        position: Int,
    ) {
        val profileName = profiles[position]
        holder.bind(profileName, onClick, context, position == selectedPosition)

        // Update selection state
        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelected) // Refresh old selection
            notifyItemChanged(selectedPosition) // Refresh new selection

            onClick(profileName)
        }
    }

    override fun getItemCount(): Int = profiles.size

    class ProfileViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val profileText: TextView = itemView.findViewById(R.id.profile_name)

        fun bind(
            profileName: String,
            onClick: (String) -> Unit,
            context: Context,
            isSelected: Boolean,
        ) {
            profileText.text = profileName
            // Highlight selected profile
            itemView.setBackgroundColor(
                ContextCompat.getColor(context, if (isSelected) R.color.material_blue_grey_100 else R.color.white),
            )
            itemView.setOnClickListener {
                onClick(profileName)
            }
        }
    }
}

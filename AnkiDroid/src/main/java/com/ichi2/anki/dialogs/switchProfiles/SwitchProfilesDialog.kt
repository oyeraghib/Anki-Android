/***************************************************************************************
 * Copyright (c) 2025 Mohd Raghib <raghib.khan76@gmail.com>                              *
 *
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

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ichi2.anki.R
import com.ichi2.utils.create
import com.ichi2.utils.negativeButton
import timber.log.Timber

class SwitchProfilesDialog : DialogFragment() {
    private lateinit var profileList: List<String>

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        super.onCreate(savedInstanceState)

        profileList = requireArguments().getStringArrayList(KEY_PROFILES) ?: emptyList()

        // Inflate the custom layout
        val view = layoutInflater.inflate(R.layout.dialog_switch_profiles, null)

        // Initialize UI components
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_profiles)

        // Bottom action buttons
        val btnOpen: Button = view.findViewById(R.id.btn_open)
        val btnAdd: Button = view.findViewById(R.id.btn_add)
        val btnRename: Button = view.findViewById(R.id.btn_rename)
        val btnDelete: Button = view.findViewById(R.id.btn_delete)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        Timber.d("Setting adapter with profiles: $profileList")
        recyclerView.adapter =
            ProfileAdapter(profileList) { selectedProfile ->
                Timber.d("Profile selected: $selectedProfile")

                // Send the selected profile to the parent fragment
                parentFragmentManager.setFragmentResult(
                    REQUEST_PROFILE_SWITCH,
                    bundleOf(KEY_SELECTED_PROFILE to selectedProfile),
                )

                dismiss() // Close the dialog after selection
            }

        // Handle button clicks (TODO: Implement actual functionality)
        btnOpen.setOnClickListener { dismiss() } // Open selected profile
        btnAdd.setOnClickListener { dismiss() } // Add new profile
        btnRename.setOnClickListener { dismiss() } // Rename selected profile
        btnDelete.setOnClickListener { dismiss() } // Delete selected profile

        val titleView =
            TextView(context).apply {
                text = "Switch Profile"
                textSize = 20f
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                gravity = Gravity.CENTER
                setPadding(20, 40, 20, 20) // Adjust padding as needed
            }

        return AlertDialog.Builder(requireContext()).create {
            setCustomTitle(titleView)
            negativeButton(R.string.dialog_cancel)
            setView(view) // Attach custom layout
        }
    }

    companion object {
        const val REQUEST_PROFILE_SWITCH = "request_profile_switch"
        const val KEY_SELECTED_PROFILE = "key_selected_profile"
        private const val KEY_PROFILES = "key_profiles"

        fun newInstance(profiles: List<String>): SwitchProfilesDialog =
            SwitchProfilesDialog().apply {
                arguments = bundleOf(KEY_PROFILES to ArrayList(profiles))
            }
    }
}

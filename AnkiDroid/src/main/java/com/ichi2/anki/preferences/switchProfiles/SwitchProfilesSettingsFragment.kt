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
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.ichi2.anki.CollectionHelper
import com.ichi2.anki.R
import com.ichi2.anki.preferences.SettingsFragment
import com.ichi2.anki.preferences.requirePreference
import com.ichi2.anki.showThemedToast
import timber.log.Timber
import java.io.File

class SwitchProfilesSettingsFragment : SettingsFragment() {
    override val preferenceResource: Int
        get() = R.xml.preferences_switch_profiles

    override val analyticsScreenNameConstant: String
        get() = "pref.switchProfiles"

    /**
     * Profile level shared preferences to store folder name -> profile name
     */
    private val profilesSharedPrefs = "profiles_prefs"

    override fun initSubscreen() {
        requirePreference<ProfileListPreference>(getString(R.string.pref_switch_profiles_screen_key)).apply {
            val profiles =
                getAllProfiles(requireContext()).map { (folder, name) ->
                    Profile(folder, name)
                }
            setProfiles(profiles)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("inside onCreate")
        val prefs = requireContext().getSharedPreferences(profilesSharedPrefs, Context.MODE_PRIVATE)
        if (!prefs.contains("AnkiDroid")) {
            prefs.edit { putString("AnkiDroid", "default") } // folder name -> display_name
        }

        val profiles = getAllProfiles(requireContext())
        val profile: MutableList<String> = mutableListOf()
        for ((folder, name) in profiles) {
            Timber.d("folder: $folder, profile: $name")
            profile.add(name)
        }

        Timber.d("Profiles available: $profile")
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("inside onViewCreated")

        Timber.d("Fragment attached to: ${requireActivity()::class.java.simpleName}")

        // TODO: We trigger this via the dialog to add a profile
        val path = CollectionHelper.getCurrentAnkiDroidDirectory(requireContext())
        Timber.d("current path: $path")
        // TODO: using hardcoded value of path for testing purpose
        val tempPath = "/storage/emulated/0/Android/data/com.ichi2.anki.debug/files/"
        val folderPath = "/storage/emulated/0/Android/data/com.ichi2.anki.debug/files/user2"
        val profileName = "oyeraghib"

        val rootFoldersPath = "/storage/emulated/0/Android/data/com.ichi2.anki.debug/files/"

//        CollectionHelper.renameProfile(folderPath, "oyeraghib")

//        CollectionHelper.deleteProfile(rootFoldersPath, "oyeraghib")
    }

    fun onAddProfileClicked(rootFolderPath: String) {
        Timber.d("Add Profile button clicked")
        showThemedToast(requireContext(), "Add Profile clicked", true)

        showAddProfileDialog(rootFolderPath)
    }

    private fun showAddProfileDialog(rootFoldersPath: String) {
        val context = requireContext()
        val editText =
            EditText(context).apply {
                hint = "Enter profile name"
                inputType = InputType.TYPE_CLASS_TEXT
                setPadding(32, 32, 32, 32)
            }

        AlertDialog
            .Builder(context)
            .setTitle("Add a New Profile")
            .setView(editText)
            .setPositiveButton("Add") { dialog, _ ->
                val profileName = editText.text.toString().trim()
                if (profileName.isNotEmpty()) {
                    Timber.d("Adding profile: $profileName")

                    val profileId = generateUniqueProfileFolder(profilesDir = File(rootFoldersPath))

                    // save to global prefs file
                    saveProfileToPrefs(profileId = profileId, profileName)
                    // Working on adding a profile
                    CollectionHelper.createProfileDirectory(
                        path = rootFoldersPath,
                        profileId = profileId,
                        profileName = profileName,
                    )
                } else {
                    showThemedToast(context, "Profile name cannot be empty", true)
                }
                dialog.dismiss()
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    fun collectionPathInValidFolder(dir: String): String {
        CollectionHelper.initializeAnkiDroidDirectory(dir)
        return File(dir, "collection.anki2").absolutePath
    }

    // Function to save profile info to SharedPreferences
    private fun saveProfileToPrefs(
        profileId: String,
        displayName: String,
    ) {
        // TODO: here implement creating a new shared prefs for that profile
        val prefs = requireContext().getSharedPreferences(profilesSharedPrefs, Context.MODE_PRIVATE)
        if (!prefs.contains(profileId)) {
            prefs.edit { putString(profileId, displayName) } // folder name -> display_name
        }
    }

    // Function to get app-wide SharedPreferences
    private fun getAppSharedPreferences(): SharedPreferences =
        requireContext().getSharedPreferences(profilesSharedPrefs, Context.MODE_PRIVATE)

    /**
     * generates a new folder user{x+1} if a user{x} already exists taking the folder with
     * highest value of x.
     * If no folder with user{x} exits creates a folder called as user1
     */
    private fun generateUniqueProfileFolder(profilesDir: File): String {
        val existingProfiles =
            profilesDir
                .listFiles { file -> file.isDirectory && file.name.matches(Regex("user\\d+")) }
                ?.map { it.name.removePrefix("user").toIntOrNull() }
                ?.filterNotNull()
                ?.sorted() ?: emptyList()

        Timber.d("existing profile : $existingProfiles")

        val nextIndex = if (existingProfiles.isEmpty()) 1 else existingProfiles.maxOrNull()!! + 1
        return "user$nextIndex"
    }

    private fun getAllProfiles(context: Context): Map<String, String> {
        val prefs = context.getSharedPreferences(profilesSharedPrefs, Context.MODE_PRIVATE)
        return prefs.all.filterValues { it is String }.mapValues { it.value as String }
    }
}

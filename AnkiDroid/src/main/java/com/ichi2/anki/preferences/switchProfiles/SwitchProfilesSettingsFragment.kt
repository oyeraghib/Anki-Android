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
import java.io.File

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

        val profilesDir = File(CollectionHelper.getCurrentAnkiDroidDirectory(requireContext())).parent

        // Creating a new profile
        val profileName = "oyeraghib"
//        createNewProfile(profilesDir, profileName)
        val newProfileName = "arthur"
        renameProfile(profileDir = profilesDir!!, "user4", newProfileName)
    }

    private fun renameProfile(
        profileDir: String,
        folderName: String,
        newProfileName: String,
    ) {
        Timber.d("$profileDir, $folderName, $newProfileName")

        renameProfileInPrefs(folderName, newProfileName)
        updateProfileConfigFile(profileDir, newProfileName, folderName)
    }

    fun renameProfileInPrefs(
        folderName: String,
        newProfileName: String,
    ) {
        val profilesPrefs = requireContext().getSharedPreferences("profiles_prefs", Context.MODE_PRIVATE)

        // Find the key with the old name and update its value
        val allEntries = profilesPrefs.all
        val editor = profilesPrefs.edit()

        allEntries.forEach { (key, value) ->
            if (key == folderName) {
                editor.putString(folderName, newProfileName) // Update to new name
            }
        }

        editor.apply()
    }

    private fun updateProfileConfigFile(
        profileDir: String,
        newName: String,
        folderName: String,
    ) {
        val dir = File(profileDir, folderName)
        Timber.d("dir: $dir")
        val configFile = File(dir, ".ankidroidprofile")

        if (configFile.exists()) {
            val lines = configFile.readLines().toMutableList()
            for (i in lines.indices) {
                if (lines[i].startsWith("display_name=")) {
                    lines[i] = "display_name=$newName"
                    break
                }
            }
            configFile.writeText(lines.joinToString("\n"))
        } else {
            // If file doesn’t exist, create it with the new name
            configFile.writeText("display_name=$newName")
        }
    }

    private fun createNewProfile(
        profilesDir: String,
        newProfileName: String,
    ): Boolean {
        // generates a unique folder name
        val userFolder = generateUniqueProfileFolder(File(profilesDir!!))

        // creates a new folder
        val newProfileDir = File(profilesDir, userFolder)
        if (!newProfileDir.mkdirs()) {
            Timber.e("ProfileCreation", "Failed to create directory: ${newProfileDir.absolutePath}")
            return false
        }

        // Creates a new set of SharedPreferences for this profile
        val profilePrefs = requireContext().getSharedPreferences("${userFolder}_shared_prefs", Context.MODE_PRIVATE)
        profilePrefs
            .edit()
            .putString("display_name", newProfileName)
            .apply()

        // add mock ankidroid collection, data and other files
        collectionPathInValidFolder(newProfileDir.toString())

        // Create .config file and store metadata
        val configFile = File(newProfileDir, ".ankidroidprofile")
        configFile.writeText("display_name=$newProfileName")

        // Save to profiles_prefs.xml SharedPreferences
        saveProfileToPrefs(userFolder, newProfileName)

        Timber.d("ProfileCreation", "Profile $newProfileDir created successfully at ${newProfileDir.absolutePath}")
        return true
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
        val sharedPrefs = getAppSharedPreferences()
        sharedPrefs.edit().putString(profileId, displayName).apply()
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

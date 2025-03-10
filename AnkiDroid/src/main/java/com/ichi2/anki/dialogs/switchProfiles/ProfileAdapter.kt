import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ichi2.anki.R

class ProfileAdapter(
    private val profiles: List<String>,
    private val onClick: (String) -> Unit,
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {
    private var selectedPosition = 0 // Default: First profile selected

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProfileViewHolder {
        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_items_switch_profiles, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProfileViewHolder,
        position: Int,
    ) {
        holder.profileText.text = profiles[position]

        // Highlight only the TextView instead of the entire item
        holder.profileText.setBackgroundResource(
            if (position == selectedPosition) {
                R.drawable.selected_profile_background
            } else {
                android.R.color.transparent
            },
        )

        // Handle click event
        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = position

            notifyItemChanged(previousSelected) // Remove highlight from old selection
            notifyItemChanged(selectedPosition) // Highlight the new selection

            onClick(profiles[position])
        }
    }

    override fun getItemCount(): Int = profiles.size

    // Inner class ViewHolder
    inner class ProfileViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val profileText: TextView = itemView.findViewById(R.id.profile_name)
    }
}

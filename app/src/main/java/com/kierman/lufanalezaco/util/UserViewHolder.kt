
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kierman.lufanalezaco.R


class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)

    fun bind(user: UserModel) {
        userNameTextView.text = user.imie
    }
}
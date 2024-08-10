import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.careerx_kotlin.SurveyOption
import com.example.careerx_kotlin.R
import com.example.careerx_kotlin.databinding.SurveyButtonBinding

class SurveyAdapter(
    private val options: List<SurveyOption>,
    private val onItemClick: (SurveyOption) -> Unit
) : RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyViewHolder {
        val binding = SurveyButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SurveyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SurveyViewHolder, position: Int) {
        holder.bind(options[position], position, selectedPosition, onItemClick)

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onItemClick(options[position])
        }
    }

    override fun getItemCount() = options.size

    class SurveyViewHolder(private val binding: SurveyButtonBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(option: SurveyOption, position: Int, selectedPosition: Int, onItemClick: (SurveyOption) -> Unit) {
            binding.optionTitle.text = option.title
            binding.optionDescription.text = option.description
            binding.optionImage.setImageResource(option.imageResId)

            // Apply a purple border if this item is selected
            if (position == selectedPosition) {
                val purpleBorder: Drawable? = ContextCompat.getDrawable(binding.root.context, R.drawable.purple_border)
                binding.root.background = purpleBorder
            } else {
                binding.root.background = null
            }

            binding.root.setOnClickListener {
                onItemClick(option)
            }
        }
    }
}

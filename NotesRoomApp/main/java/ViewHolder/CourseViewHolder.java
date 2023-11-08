package ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bscs19072_reesnotes.R;

import Interface.ItemClickListener;

public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView txtCourseName, txtCourseDescription, txtCourseID;
    public ImageView imageView;
    public ItemClickListener iListner;


    public CourseViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.course_image);
        txtCourseName = (TextView) itemView.findViewById(R.id.course_name);
        txtCourseID = (TextView) itemView.findViewById(R.id.course_id);
        txtCourseDescription = (TextView) itemView.findViewById(R.id.course_description);
    }

    public void setItemClickListner(ItemClickListener listner)
    {
        this.iListner = listner;
    }

    @Override
    public void onClick(View view) {

        iListner.onClick(view, getAdapterPosition(), false);
    }
}

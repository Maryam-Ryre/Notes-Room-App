package ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bscs19072_reesnotes.R;

import Interface.ItemClickListener;

public class LectureNumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtLecNo;
    public ItemClickListener itemClickListner;

    public LectureNumViewHolder(@NonNull View itemView)
    {
        super(itemView);

        txtLecNo = (TextView) itemView.findViewById(R.id.lecture_num);
    }

    @Override
    public void onClick(View v) {

        itemClickListner.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListner(ItemClickListener listner)
    {
        this.itemClickListner = listner;
    }
}

package ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bscs19072_reesnotes.R;

import Interface.ItemClickListener;

public class DocsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtDocName;
    public ItemClickListener itemClickListner;

    public DocsViewHolder(@NonNull View itemView) {
        super(itemView);

        txtDocName = (TextView) itemView.findViewById(R.id.notes_name);
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


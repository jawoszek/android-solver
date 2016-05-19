package januszek.solver.Recyclers;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import januszek.solver.Activities.SingleProblem;
import januszek.solver.Problems.Problem;
import januszek.solver.R;

/**
 * Created by Januszek on 2016-04-09.
 */
public class ProblemsAdapter extends RecyclerView.Adapter<ProblemsAdapter.ViewHolder> {
    private List<Problem> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public Problem item;
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
            mTextView = (TextView) v.findViewById(R.id.textNa);
        }
    }

    public ProblemsAdapter(List<Problem> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ProblemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_problems, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = mDataset.get(position);
        holder.mTextView.setText(holder.item.getFunc());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Problem.sideStep = holder.item;
                Intent intent= new Intent(v.getContext(), SingleProblem.class);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

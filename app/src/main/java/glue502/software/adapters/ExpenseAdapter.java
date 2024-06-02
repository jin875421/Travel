package glue502.software.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import glue502.software.R;
import glue502.software.models.Expense;

public class ExpenseAdapter extends BaseAdapter {
    private Context context;
    private int expense_layout_id;
    private List<Expense> expenseList;
    public ExpenseAdapter(Context context, int expense_layout_id, List<Expense> expenseList)
    {
        this.context = context;
        this.expense_layout_id = expense_layout_id;
        this.expenseList = expenseList;
    }
    @Override
    public int getCount() {
        return expenseList.size();
    }

    @Override
    public Object getItem(int position) {
        return expenseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, expense_layout_id, null);
        TextView expenseDescribe = view.findViewById(R.id.description);
        expenseDescribe.setText(expenseList.get(position).getDescribe()+"  ¥"+expenseList.get(position).getPrice());
        return view;
    }
}

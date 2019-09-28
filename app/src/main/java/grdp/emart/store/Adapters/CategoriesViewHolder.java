package grdp.emart.store.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import grdp.emart.store.Fragments.CategoryList;
import grdp.emart.store.Fragments.Home;
import grdp.emart.store.Fragments.ProductsList;
import grdp.emart.store.Activities.MainActivity;
import grdp.emart.store.R;


/**
 * Created by AbhiAndroid
 */
public class CategoriesViewHolder extends RecyclerView.ViewHolder {

    ImageView image;
    TextView catName;
    CardView cardView;

    public CategoriesViewHolder(final Context context, View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.categoryIcon);
        catName = (TextView) itemView.findViewById(R.id.categoryName);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Home.swipeRefreshLayout.isRefreshing())
                    if (getAdapterPosition() == 3) {
                        ((MainActivity) context).loadFragment(new CategoryList(), true);
                    } else {
                        ProductsList.categoryPosition = getAdapterPosition();
                        ((MainActivity) context).loadFragment(new ProductsList(), true);
                    }
            }
        });
    }
}

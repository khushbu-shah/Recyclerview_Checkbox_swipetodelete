package com.example.blemoduletest.recyclercheckboxdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;

/**
 * Use RecyclerSwipeAdapter for swipe adapter iteam instead of RecyclerView.Adapter
 */
public class ContactSelectionAdapter extends RecyclerSwipeAdapter
{
    private OnItemClickListener itemClickListener;

    private static final int TYPE_LETTER = 0;
    private static final int TYPE_MEMBER = 1;

    private ArrayList<ContactBean> alContact;

    private OnCheckBoxClickListener checkBoxClickListener;

    public ContactSelectionAdapter(Context context, ArrayList<ContactBean> alContact, OnItemClickListener itemClickListener, OnCheckBoxClickListener checkBoxClickListener)
    {
        this.itemClickListener = itemClickListener;

        this.alContact = alContact;

        this.checkBoxClickListener = checkBoxClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_LETTER:
                ViewGroup vGroupImage = (ViewGroup) mInflater.inflate(R.layout.layout_seperate_header, parent, false);
                ViewHolderLetter image = new ViewHolderLetter(vGroupImage);
                return image;
            case TYPE_MEMBER:
                ViewGroup vGroupText = (ViewGroup) mInflater.inflate(R.layout.row_contact_item, parent, false);
                ProjectHolder text = new ProjectHolder(vGroupText);
                return text;
            default:
                ViewGroup vGroupText2 = (ViewGroup) mInflater.inflate(R.layout.row_contact_item, parent, false);
                ProjectHolder text1 = new ProjectHolder(vGroupText2);
                return text1;
        }
    }

    /*https://stackoverflow.com/questions/34142289/display-namelist-in-recyclerview-under-each-letter-in-alphabetic-order-android*/
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position)
    {
        final ContactBean contactBean = alContact.get(position);

        if(holder instanceof ProjectHolder)
        {
            final ProjectHolder viewHolderProject = (ProjectHolder) holder;

            viewHolderProject.cbContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CheckBox chk = (CheckBox) v;

                    contactBean.setChecked(chk.isChecked());

                    checkBoxClickListener.onCheckBoxClick(true);
                }
            });

            viewHolderProject.tvContactName.setText(contactBean.getContactName());

            viewHolderProject.cbContact.setChecked(contactBean.isChecked());

            {
                viewHolderProject.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolderProject.swipeLayout.findViewById(R.id.llSwipeView));
            }
            /*Click Listener of Swipe Layout :: Remove*/
            final RecyclerView.ViewHolder finalHolder = holder;
            viewHolderProject.llRemove.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick( View v )
                {
                    mItemManger.removeShownLayouts(((ProjectHolder) finalHolder).swipeLayout);
                    alContact.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, alContact.size());
                    mItemManger.closeAllItems();

                    ((ProjectHolder) finalHolder).swipeLayout.close();
                }
            });

            /*Click Listener of Item View :: row item*/
            viewHolderProject.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick( View view )
                {
                    if (view != null) {
                        CheckBox checkBox = (CheckBox)view.findViewById(R.id.cbContact);
                        checkBox.setChecked(!checkBox.isChecked());
                    }
                }
            });

            mItemManger.bindView(holder.itemView, position);

        }
        else if(holder instanceof ViewHolderLetter)
        {
            ViewHolderLetter viewHolderLetter = (ViewHolderLetter) holder;

            viewHolderLetter.tvCompanyName.setText(contactBean.getContactName());
        }
    }

    @Override
    public int getItemCount()
    {
        return alContact.size();
    }

    @Override
    public int getItemViewType(int position) {

        int viewType = 0;
        if (alContact.get(position).getType() == TYPE_LETTER) {
            viewType = TYPE_LETTER;
        } else if (alContact.get(position).getType() == TYPE_MEMBER) {
            viewType = TYPE_MEMBER;
        }

        return viewType;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class ProjectHolder extends RecyclerView.ViewHolder
    {
        public SwipeLayout swipeLayout;

        private LinearLayout llRemove;

        private TextView tvContactName;

        private CheckBox cbContact;

        public ProjectHolder(View itemView)
        {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            llRemove = (LinearLayout) itemView.findViewById(R.id.llRemove);

            tvContactName = itemView.findViewById(R.id.tvContactName);
            cbContact = itemView.findViewById(R.id.cbContact);
        }
    }
    public class ViewHolderLetter extends RecyclerView.ViewHolder
    {

        private TextView tvCompanyName;

        public ViewHolderLetter(View itemView)
        {
            super(itemView);

            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
        }
    }


    /**
     * Get the list of Selected Contact(selected Checkbox)
     * @return
     */
    public ArrayList<ContactBean> getSelectContactList(){
        ArrayList<ContactBean> list = new ArrayList<>();
        for(int i=0;i<alContact.size();i++){
            if(alContact.get(i).isChecked())
                list.add(alContact.get(i));
        }
        return list;
    }

}

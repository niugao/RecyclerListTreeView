package niuedu.com.treeviewtest;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.niuedu.ListTree;
import com.niuedu.ListTreeAdapter;
import com.niuedu.ListTreeViewHolder;

/**
 * 为RecyclerView提供数据
 */
public class ExampleListTreeAdapter extends
        ListTreeAdapter<ExampleListTreeAdapter.BaseViewHolder> {
    //保存子行信息的类
    public static class ContactInfo{
        //头像,用于设置给ImageView。
        private Bitmap bitmap;
        //标题
        private String title;
        //描述
        private String detail;

        public ContactInfo(Bitmap bitmap, String title, String detail) {
            this.bitmap = bitmap;
            this.title = title;
            this.detail = detail;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public String getTitle() {
            return title;
        }

        public String getDetail() {
            return detail;
        }
    }

    //构造方法
    public ExampleListTreeAdapter(ListTree tree){
        super(tree);
    }

    @Override
    protected BaseViewHolder onCreateNodeView(ViewGroup parent, int viewType){
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BaseViewHolder vh;
        //创建不同的行View
        if(viewType==R.layout.contacts_group_item){
            //最后一个参数必须传true
            view = inflater.inflate(viewType,parent,true);
            vh=new GroupViewHolder(view);
        }else if(viewType == R.layout.contacts_contact_item){
            view = inflater.inflate(viewType,parent,true);
            vh=new ContactViewHolder(view);
        }else{
            return null;
        }

        return vh;

    }

    @Override
    protected void onBindNodeViewHolder(BaseViewHolder holder, int position) {
        View view = holder.itemView;
        //get node at the position
        ListTree.TreeNode node = tree.getNodeByPlaneIndex(position);

        if(node.getLayoutResId() == R.layout.contacts_group_item){
            //group node
            String title = (String)node.getData();

            GroupViewHolder gvh= (GroupViewHolder) holder;
            gvh.textViewTitle.setText(title);
            gvh.textViewCount.setText("0/"+node.getChildrenCount());
            gvh.aSwitch.setChecked(node.isChecked());

            gvh.aSwitch.setTag(node);
        }else if(node.getLayoutResId() == R.layout.contacts_contact_item){
            //child node
            ContactInfo info = (ContactInfo) node.getData();

            ContactViewHolder cvh= (ContactViewHolder) holder;
            cvh.imageViewHead.setImageBitmap(info.getBitmap());
            cvh.textViewTitle.setText(info.getTitle());
            cvh.textViewDetail.setText(info.getDetail());
            cvh.aSwitch.setChecked(node.isChecked());

            cvh.aSwitch.setTag(node);
        }
    }

    class BaseViewHolder extends ListTreeViewHolder{
        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    //将ViewHolder声明为Adapter的内部类，反正外面也用不到
    class GroupViewHolder extends BaseViewHolder {
        TextView textViewTitle;
        TextView textViewCount;
        Switch aSwitch;
        public GroupViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewCount = itemView.findViewById(R.id.textViewCount);
            aSwitch = itemView.findViewById(R.id.switchChecked);

            //应响应点击事件而不是CheckedChange事件，因为那样会引起事件的递归触发
            Switch aSwitch = itemView.findViewById(R.id.switchChecked);
            aSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ListTree.TreeNode node = (ListTree.TreeNode) view.getTag();
                    node.setChecked(!node.isChecked());
                    int planeIndex = tree.getNodePlaneIndex(node);
                    //改变所有的子孙们的状态
                    int count =tree.setDescendantChecked(planeIndex,node.isChecked());
                    notifyItemRangeChanged(planeIndex,count+1);
                }
            });
//        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//            }
//        });
        }
    }

    class ContactViewHolder extends BaseViewHolder{
        ImageView imageViewHead;
        TextView textViewTitle;
        TextView textViewDetail;
        Switch aSwitch;

        public ContactViewHolder(View itemView) {
            super(itemView);

            imageViewHead = itemView.findViewById(R.id.imageViewHead);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDetail = itemView.findViewById(R.id.textViewDetail);
            aSwitch = itemView.findViewById(R.id.switchChecked);

            //应响应点击事件而不是CheckedChange事件，因为那样会引起事件的递归触发
            Switch aSwitch = itemView.findViewById(R.id.switchChecked);
            aSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ListTree.TreeNode node = (ListTree.TreeNode) view.getTag();
                    node.setChecked(!node.isChecked());
                    int planeIndex = tree.getNodePlaneIndex(node);
                    //改变所有的子孙们的状态
                    int count =tree.setDescendantChecked(planeIndex,node.isChecked());
                    notifyItemRangeChanged(planeIndex,count+1);
                }
            });

        }
    }
}
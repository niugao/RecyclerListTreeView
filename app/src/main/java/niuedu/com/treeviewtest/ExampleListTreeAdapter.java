package niuedu.com.treeviewtest;

import android.graphics.Bitmap;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.niuedu.ListTree;
import com.niuedu.ListTreeAdapter;

/**
 * 为RecyclerView提供数据
 */
public class ExampleListTreeAdapter extends
        ListTreeAdapter<ExampleListTreeAdapter.BaseViewHolder> {

    //行上弹出菜单的侦听器
    private PopupMenu.OnMenuItemClickListener itemMenuClickListener;
    //记录弹出菜单是在哪个行上出现的
    private ListTree.TreeNode currentNode;

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
        public void setTitle(String title) {
            this.title = title;
        }

        public String getDetail() {
            return detail;
        }

    }

    //构造方法
    public ExampleListTreeAdapter(ListTree tree,
                                  PopupMenu.OnMenuItemClickListener listener){
        super(tree);
        this.itemMenuClickListener=listener;
    }

    public ListTree.TreeNode getCurrentNode() {
        return currentNode;
    }

    @Override
    protected BaseViewHolder onCreateNodeView(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        //创建不同的行View
        if(viewType==R.layout.contacts_group_item){
            //注意！此处有一个不同！最后一个参数必须传true！
            View view = inflater.inflate(viewType,parent,true);
            //用不同的ViewHolder包装
            return new GroupViewHolder(view);
        }else if(viewType == R.layout.contacts_contact_item){
            //注意！此处有一个不同！最后一个参数必须传true！
            View view = inflater.inflate(viewType,parent,true);
            //用不同的ViewHolder包装
            return  new ContactViewHolder(view);
        }else{
            return null;
        }
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
        }else if(node.getLayoutResId() == R.layout.contacts_contact_item){
            //child node
            ContactInfo info = (ContactInfo) node.getData();

            ContactViewHolder cvh= (ContactViewHolder) holder;
            cvh.imageViewHead.setImageBitmap(info.getBitmap());
            cvh.textViewTitle.setText(info.getTitle());
            cvh.textViewDetail.setText(info.getDetail());
            cvh.aSwitch.setChecked(node.isChecked());
        }
    }

    //组行和联系人行的Holder基类
    class BaseViewHolder extends ListTreeAdapter.ListTreeViewHolder{
        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    //将ViewHolder声明为Adapter的内部类，反正外面也用不到
    class GroupViewHolder extends BaseViewHolder {

        TextView textViewTitle;
        TextView textViewCount;
        Switch aSwitch;
        TextView textViewMenu;

        public GroupViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewCount = itemView.findViewById(R.id.textViewCount);
            aSwitch = itemView.findViewById(R.id.switchChecked);
            textViewMenu = itemView.findViewById(R.id.textViewMenu);

            //应响应点击事件而不是CheckedChange事件，因为那样会引起事件的递归触发
            aSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int planeIndex = getAdapterPosition();
                    ListTree.TreeNode node = tree.getNodeByPlaneIndex(planeIndex);
                    node.setChecked(!node.isChecked());
                    //改变所有的子孙们的状态
                    int count =tree.setDescendantChecked(planeIndex,node.isChecked());
                    notifyItemRangeChanged(planeIndex,count+1);
                }
            });

            //点了PopMenu控件，弹出PopMenu
            textViewMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nodePlaneIndex = getAdapterPosition();
                    ListTree.TreeNode node = tree.getNodeByPlaneIndex(nodePlaneIndex);
                    currentNode=node;
                    PopupMenu popup = new PopupMenu(v.getContext(), v);
                    popup.setOnMenuItemClickListener(itemMenuClickListener);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_item, popup.getMenu());
                    popup.show();
                }
            });
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
            aSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int nodePlaneIndex = getAdapterPosition();
                    ListTree.TreeNode node = tree.getNodeByPlaneIndex(nodePlaneIndex);
                    node.setChecked(!node.isChecked());
                    //改变所有的子孙们的状态
                    int count =tree.setDescendantChecked(nodePlaneIndex,node.isChecked());
                    notifyItemRangeChanged(nodePlaneIndex,count+1);
                }
            });
        }
    }
}
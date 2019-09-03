package qst.com.app4cotlin

import android.graphics.Bitmap
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.niuedu.ListTree
import com.niuedu.ListTreeAdapter

class ExampleListTreeAdapter(tree: ListTree, listener : PopupMenu.OnMenuItemClickListener) :
        ListTreeAdapter<ExampleListTreeAdapter.BaseViewHolder>(tree){

    //行上弹出菜单的侦听器
    private val itemMenuClickListener : PopupMenu.OnMenuItemClickListener

    //记录弹出菜单是在哪个行上出现的
    var currentNode: ListTree.TreeNode? = null

    //保存子行信息的类
    class ContactInfo(
            val bitmap: Bitmap, //头像,用于设置给ImageView
            var title: String, //标题
            var detail: String //描述
    )

    init{
        itemMenuClickListener = listener
    }

    override fun onCreateNodeView(parent: ViewGroup?, viewType: Int): BaseViewHolder? {
        val inflater = LayoutInflater.from(parent!!.getContext())

        //创建不同的行View
        if (viewType == R.layout.contacts_group_item) {
            //注意！此处有一个不同！最后一个参数必须传true！
            val view = inflater.inflate(viewType, parent, true)
            //用不同的ViewHolder包装
            return GroupViewHolder(view)
        } else if (viewType == R.layout.contacts_contact_item) {
            //注意！此处有一个不同！最后一个参数必须传true！
            val view = inflater.inflate(viewType, parent, true)
            //用不同的ViewHolder包装
            return ContactViewHolder(view)
        } else {
            return null
        }
    }

    override fun onBindNodeViewHolder(viewHoler: BaseViewHolder?, position: Int) {
        //get node at the position
        val node = tree.getNodeByPlaneIndex(position)

        if (node.layoutResId == R.layout.contacts_group_item) {
            //group node
            val title = node.data as String

            val gvh = viewHoler as GroupViewHolder
            gvh.textViewTitle.text = title
            gvh.textViewCount.text = "0/" + node.childrenCount
            gvh.aSwitch.isChecked = node.isChecked
        } else if (node.layoutResId == R.layout.contacts_contact_item) {
            //child node
            val info = node.data as ContactInfo

            val cvh = viewHoler as ContactViewHolder
            cvh.imageViewHead.setImageBitmap(info.bitmap)
            cvh.textViewTitle.text = info.title
            cvh.textViewDetail.text = info.detail
            cvh.aSwitch.isChecked = node.isChecked
        }
    }

    //组行和联系人行的Holder基类
    open inner class BaseViewHolder(itemView: View) : ListTreeViewHolder(itemView)

    //将ViewHolder声明为Adapter的内部类，反正外面也用不到
    internal inner class GroupViewHolder(itemView: View) : BaseViewHolder(itemView) {

        var textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        var textViewCount: TextView = itemView.findViewById(R.id.textViewCount)
        var aSwitch: Switch = itemView.findViewById(R.id.switchChecked)
        var textViewMenu: TextView = itemView.findViewById(R.id.textViewMenu)

        init {

            //应响应点击事件而不是CheckedChange事件，因为那样会引起事件的递归触发
            aSwitch.setOnClickListener {
                val planeIndex = adapterPosition
                val node = tree.getNodeByPlaneIndex(planeIndex)
                node.isChecked = !node.isChecked
                //改变所有的子孙们的状态
                val count = tree.setDescendantChecked(planeIndex, node.isChecked)
                notifyItemRangeChanged(planeIndex, count + 1)
            }

            //点了PopMenu控件，弹出PopMenu
            textViewMenu.setOnClickListener { v ->
                val nodePlaneIndex = adapterPosition
                val node = tree.getNodeByPlaneIndex(nodePlaneIndex)
                currentNode = node
                val popup = PopupMenu(v.context, v)
                popup.setOnMenuItemClickListener(itemMenuClickListener)
                val inflater = popup.menuInflater
                inflater.inflate(R.menu.menu_item, popup.menu)
                popup.show()
            }
        }
    }

    internal inner class ContactViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var imageViewHead: ImageView = itemView.findViewById(R.id.imageViewHead)
        var textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        var textViewDetail: TextView = itemView.findViewById(R.id.textViewDetail)
        var aSwitch: Switch = itemView.findViewById(R.id.switchChecked)

        init {

            //应响应点击事件而不是CheckedChange事件，因为那样会引起事件的递归触发
            aSwitch.setOnClickListener {
                val nodePlaneIndex = adapterPosition
                val node = tree.getNodeByPlaneIndex(nodePlaneIndex)
                node.isChecked = !node.isChecked
                //改变所有的子孙们的状态
                val count = tree.setDescendantChecked(nodePlaneIndex, node.isChecked)
                notifyItemRangeChanged(nodePlaneIndex, count + 1)
            }
        }
    }

}
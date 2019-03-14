package qst.com.app4cotlin

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.Menu
import android.view.MenuItem
import com.niuedu.ListTree
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() , PopupMenu.OnMenuItemClickListener{

    //保存数据的集合
    private val tree = ListTree()

    //从ListTreeAdapter派生的Adapter
    internal var adapter: ExampleListTreeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        //创建后台数据：一棵树
        //创建组们，是root node，所有parent为null
        val groupNode1 = tree.addNode(null, "特别关心", R.layout.contacts_group_item)
        val groupNode2 = tree.addNode(null, "我的好友", R.layout.contacts_group_item)
        val groupNode3 = tree.addNode(null, "朋友", R.layout.contacts_group_item)
        val groupNode4 = tree.addNode(null, "家人", R.layout.contacts_group_item)
        val groupNode5 = tree.addNode(null, "同学", R.layout.contacts_group_item)

        //第二层
        var contact: ExampleListTreeAdapter.ContactInfo
        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "王二", "[在线]我是王二")
        val contactNode1 = tree.addNode(groupNode2, contact, R.layout.contacts_contact_item)
        val contactNode2 = tree.addNode(groupNode5, contact, R.layout.contacts_contact_item)
        //再添加一个
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "王三", "[离线]我没有状态")
        tree.addNode(groupNode2, contact, R.layout.contacts_contact_item)
        tree.addNode(groupNode5, contact, R.layout.contacts_contact_item)

        //第三层
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "东邪", "[离线]出来还价")
        var n: ListTree.TreeNode = tree.addNode(contactNode1, contact, R.layout.contacts_contact_item)
        n.isShowExpandIcon = false
        //再添加一个
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "李圆圆", "[离线]昨天出门没出去")
        n = tree.addNode(contactNode1, contact, R.layout.contacts_contact_item)
        n.isShowExpandIcon = false

        adapter = ExampleListTreeAdapter(tree, this)
        listView.layoutManager = LinearLayoutManager(this)
        listView.setAdapter(adapter)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_del_selected) {
            //删除选中的Nodes，删一个Node时会将其子孙一起删掉
            tree.removeCheckedNodes()
            adapter?.notifyDataSetChanged()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_add_item -> {
                //向当前行增加一个儿子
                val node = adapter!!.currentNode
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
                val contact = ExampleListTreeAdapter.ContactInfo(
                        bitmap, "New contact", "[离线]我没有状态")
                val childNode = tree.addNode(node, contact, R.layout.contacts_contact_item)
                adapter!!.notifyTreeItemInserted(node, childNode)
                return true
            }
            R.id.action_clear_children -> {
                //清空所有的儿子们
                val node = adapter!!.currentNode
                val range = tree.clearDescendant(node)
                adapter!!.notifyItemRangeRemoved(range!!.first, range.second)
                return true
            }
            else -> return false
        }
    }
}

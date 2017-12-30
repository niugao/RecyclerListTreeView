package niuedu.com.treeviewtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.niuedu.ListTree;

import java.util.SortedMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private ListTree tree=new ListTree();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView listView = findViewById(R.id.listview);

        //创建后台数据：一棵树
        //创建组们，是root node，所有parent为null
        ListTree.TreeNode groupNode1=tree.addNode(null,"特别关心", R.layout.contacts_group_item);
        ListTree.TreeNode groupNode2=tree.addNode(null,"我的好友", R.layout.contacts_group_item);
        ListTree.TreeNode groupNode3=tree.addNode(null,"朋友", R.layout.contacts_group_item);
        ListTree.TreeNode groupNode4=tree.addNode(null,"家人", R.layout.contacts_group_item);
        ListTree.TreeNode groupNode5=tree.addNode(null,"同学", R.layout.contacts_group_item);

        //第二层
        ExampleListTreeAdapter.ContactInfo contact;
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.contacts_normal);
        contact = new ExampleListTreeAdapter.ContactInfo(
                bitmap,"西门广大","[在线]咱长病的人，就是不一样");
        ListTree.TreeNode contactNode1=tree.addNode(groupNode2,contact,R.layout.contacts_contact_item);
        ListTree.TreeNode contactNode2=tree.addNode(groupNode5,contact,R.layout.contacts_contact_item);
        //再添加一个
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.contacts_normal);
        contact=new ExampleListTreeAdapter.ContactInfo(bitmap,"东邪吸毒","[离线]出来卖，总是要还价的");
        tree.addNode(groupNode2,contact,R.layout.contacts_contact_item);
        tree.addNode(groupNode5,contact,R.layout.contacts_contact_item);

        //第三层
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.contacts_normal);
        contact=new ExampleListTreeAdapter.ContactInfo(bitmap,"东邪吸毒","[离线]出来卖，总是要还价的");
        ListTree.TreeNode n=tree.addNode(contactNode1,contact,R.layout.contacts_contact_item);
        n.setShowExpandIcon(false);
        //再添加一个
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.contacts_normal);
        contact=new ExampleListTreeAdapter.ContactInfo(bitmap,"东邪吸毒","[离线]出来卖，总是要还价的");
        n=tree.addNode(contactNode1,contact,R.layout.contacts_contact_item);
        n.setShowExpandIcon(false);

        ExampleListTreeAdapter adapter=new ExampleListTreeAdapter(tree);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

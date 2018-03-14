package niuedu.com.treeviewtest;

import com.niuedu.ListTree;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 */
public class ListTreeTest {
    private ListTree tree;
    private ListTree.TreeNode groupNode1;
    private ListTree.TreeNode groupNode2;
    private ListTree.TreeNode groupNode3;
    private ListTree.TreeNode groupNode4;
    private ListTree.TreeNode groupNode5;

    @Before
    public void setUp(){
        this.tree=new ListTree();
        groupNode1=tree.addNode(null,"root 1",0);
        groupNode2=tree.addNode(null,"root 2",0);
        groupNode3=tree.addNode(null,"root 3",0);
        groupNode4=tree.addNode(null,"root 4",0);
        groupNode5=tree.addNode(null,"root 5",0);

        ListTree.TreeNode contactNode1=tree.addNode(groupNode2,"2-1",0);
        ListTree.TreeNode contactNode2=tree.addNode(groupNode5,"5-1",0);

        //再添加一个
        tree.addNode(groupNode2,"2-2",0);
        tree.addNode(groupNode5,"5-2",0);
        //第三层
        tree.addNode(contactNode1,"2-1-1",0);
        tree.addNode(contactNode1,"2-1-2",0);
    }

    @After
    public void tearDown(){

    }

    @Test
    public void testExpandNodes(){
        assertEquals(tree.getNodePlaneIndex(groupNode1),0);
        assertEquals(tree.getNodePlaneIndex(groupNode5),4);
        tree.expandNode(groupNode2);
        assertEquals(tree.getNodePlaneIndex(groupNode5),6);
        tree.collapseNode(groupNode2);
    }

    @Test
    public void testInsertNodes(){
        ListTree.TreeNode node = tree.insertNode(groupNode2,0,"2-3",-1);
        assertEquals(tree.getNodePlaneIndex(groupNode3),2);
        tree.expandNode(groupNode2);
        assertEquals(tree.getNodePlaneIndex(groupNode3),5);
    }

    @Test
    public void testEnumTree(){
        ListTree.EnumPos pos = tree.startEnumNode();
        int count=0;
        while (pos!=null){
            count++;
            ListTree.TreeNode node = tree.getNodeByEnumPos(pos);
            //System.out.println(node.getData().toString());
            pos = tree.enumNext(pos);
        }

        assertEquals(count,11);
    }

    @Test
    public void testRemoveOneNode(){
        this.tree.removeNode(groupNode3);

        ListTree.EnumPos pos = tree.startEnumNode();
        int count=0;
        while (pos!=null){
            count++;
            ListTree.TreeNode node = tree.getNodeByEnumPos(pos);
            //System.out.println(node.getData().toString());
            pos = tree.enumNext(pos);
        }
        assertEquals(count,10);
    }

    @Test
    public void testRemoveNodes(){
        this.tree.removeNode(groupNode2);

        ListTree.EnumPos pos = tree.startEnumNode();
        int count=0;
        while (pos!=null){
            count++;
            ListTree.TreeNode node = tree.getNodeByEnumPos(pos);
            System.out.println(node.getData().toString());
            pos = tree.enumNext(pos);
        }
        assertEquals(count,6);
    }
}
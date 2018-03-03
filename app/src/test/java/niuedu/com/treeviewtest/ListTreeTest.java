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
    private ListTree tree=new ListTree();
    ListTree.TreeNode groupNode1;
    ListTree.TreeNode groupNode2;
    ListTree.TreeNode groupNode3;
    ListTree.TreeNode groupNode4;
    ListTree.TreeNode groupNode5;

    @Before
    public void setUp(){
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
    public void testAddNodes(){
        assertEquals(tree.getNodePlaneIndex(groupNode1),0);
        assertEquals(tree.getNodePlaneIndex(groupNode5),4);
        tree.expandNode(tree.getNodePlaneIndex(groupNode2));
        assertEquals(tree.getNodePlaneIndex(groupNode5),6);
    }
    
    public void testEnumTree(){
        ListTree.EnumPos pos = tree.startEnumNode();
        int c=0;
        while (pos!=null){
            c++;
            ListTree.TreeNode node = tree.getNodeByEnumPos(pos);
            System.out.println(node.getData().toString());
            pos=tree.getNextNode(pos);
        }

        assertEquals(c,11);
    }
}
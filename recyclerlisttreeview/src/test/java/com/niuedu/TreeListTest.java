package com.niuedu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.Before;
import org.junit.Test;


public class TreeListTest {
    //保存数据的集合
    ListTree tree = new ListTree();
    ListTree.TreeNode n101 = null;

    @Before
    public void init(){
        //创建后台数据：一棵树
        //创建组们，是root node，所有parent为null
        ListTree.TreeNode n0 = tree.addNode(null, "0", 0);
        ListTree.TreeNode n1 = tree.addNode(null, "1", 0);
        ListTree.TreeNode n2 = tree.addNode(null, "2", 0);
        ListTree.TreeNode n3 = tree.addNode(null, "3", 0);
        ListTree.TreeNode n4 = tree.addNode(null, "4", 0);

        //n0
        ListTree.TreeNode n00 = tree.addNode(n0, "0-0", 0);
        ListTree.TreeNode n01 = tree.addNode(n0, "0-1", 0);
        ListTree.TreeNode n02 = tree.addNode(n0, "0-2", 0);

        //n1
        ListTree.TreeNode n10=tree.addNode(n1, "1-0", 0);
        ListTree.TreeNode n11=tree.addNode(n1, "1-1", 0);
        ListTree.TreeNode n12=tree.addNode(n1, "1-2", 0);

        //n2
        ListTree.TreeNode n20=tree.addNode(n2, "2-0", 0);
        ListTree.TreeNode n21=tree.addNode(n2, "2-1", 0);
        ListTree.TreeNode n22=tree.addNode(n2, "2-2", 0);

        //n10
        ListTree.TreeNode n100=tree.addNode(n10, "1-0-0", 0);
        n101=tree.addNode(n10, "1-0-1", 0);
        ListTree.TreeNode n102=tree.addNode(n10, "1-0-2", 0);

        //n12
        ListTree.TreeNode n120=tree.addNode(n12, "1-2-0", 0);
        ListTree.TreeNode n121=tree.addNode(n12, "1-2-1", 0);
        ListTree.TreeNode n122=tree.addNode(n12, "1-2-2", 0);

        //n22
        ListTree.TreeNode n220=tree.addNode(n22, "2-2-0", 0);
        ListTree.TreeNode n221=tree.addNode(n22, "2-2-1", 0);
        ListTree.TreeNode n222=tree.addNode(n22, "2-2-2", 0);

        //n101
        ListTree.TreeNode n1010=tree.addNode(n101, "1-0-1-0", 0);
        ListTree.TreeNode n1011=tree.addNode(n101, "1-0-1-1", 0);
        ListTree.TreeNode n1012=tree.addNode(n101, "1-0-1-2", 0);

        //n222
        ListTree.TreeNode n2220=tree.addNode(n222, "2-2-2-0", 0);
        ListTree.TreeNode n2221=tree.addNode(n222, "2-2-2-1", 0);
        ListTree.TreeNode n2222=tree.addNode(n222, "2-2-2-2", 0);
    }

//    @Test
//    public void testExpandAll(){
//        tree.expandAllNodes();
//        tree.printList();
//    }
//
//    @Test
//    public void testCollapseAll(){
//        tree.collapseAllNodes();
//        tree.printList();
//    }

//    @Test
//    public void testEnumAll(){
//        tree.forEach(node -> {
//            System.out.println(node.getData().toString());
//        });
//    }

//    @Test
//    public void testInsertRootAtFirst(){
//        tree.insertNode(null,0,"-1",0);
//        tree.forEach(node -> {
//            System.out.println(node.getData().toString());
//        });
//    }

    @Test
    public void testInverseEnumRootSibling(){
        //先找到最后一个
        ListTree.TreeNode node=tree.getFirstNode();
        for (;;){
            ListTree.TreeNode tempNode = tree.getNextSibling(node);
            if(tempNode == null){
                break;
            }
            node = tempNode;
        }

        while (node!=null){
            System.out.println(node.getData().toString());
            node=tree.getPrevSibling(node);
        }
    }

//    @Test
//    public void testInsertRootAtLast(){
//        ListTree.TreeNode node=tree.getFirstNode();
//        int i=0;
//        while (node!=null){
//            i++;
//            node=tree.getNextSibling(node);
//        }
//
//        tree.insertNode(null,i,"new Node",0);
//        tree.forEach(n -> {
//            System.out.println(n.getData().toString());
//        });
//    }

//    @Test
//    public void testInsertChildAtFirst(){
//        tree.insertNode(n101,0,"1-0-1-new",0);
//        tree.forEach(node -> {
//            System.out.println(node.getData().toString());
//        });
//    }

//    @Test
//    public void testInsertChildAtMiddle(){
//        tree.insertNode(n101,2,"1-0-1-new",0);
//        tree.forEach(node -> {
//            System.out.println(node.getData().toString());
//        });
//    }

//    @Test
//    public void testInsertChildAtLast(){
//        tree.addNode(n101,"1-0-1-new",0);
//        tree.forEach(node -> {
//            System.out.println(node.getData().toString());
//        });
//    }

//    @Test
//    public void testGetNodeIndexes(){
//        ListTree.TreeNode node = tree.addNode(n101,"1-0-1-new",0);
//        tree.forEach(n -> {
//            System.out.println(n.getData().toString());
//        });
//        System.out.println("new node rand:"+tree.getNodeRank(node));
//        System.out.println("new node plane index:"+tree.getNodePlaneIndex(node));
//    }

    @Test
    public void testEnumChildren(){
        ListTree.TreeNode child = tree.getFirstChild(n101);
        while (child!=null){
            System.out.println(child.getData().toString());
            child = tree.getNextSibling(child);
        }
    }

}

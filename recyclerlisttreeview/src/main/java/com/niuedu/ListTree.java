package com.niuedu;

import android.support.annotation.TransitionRes;
import android.util.Pair;
import android.util.Range;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by nkm on 27/12/2017.
 * <p>
 * 原则：儿子一定是在爸爸后面并紧靠爸爸的
 * <p>
 * 以List的形式表式树。用起来感觉是树，但实际上是个List。这带来很多好处：
 * 1 首先就是没有递归算法。该用递归的地方全部变成了循环。
 * 2 其次是有序，插入节点时，可以指定它是它爸爸的第几个儿子。
 * 3 最后就是极其适合在RecyclerView中使用。使用此类做后台数据，
 * 跟List无异，无论根节点行还是子节点行都是RecyclerView中的一行。
 * 4 不需对RecyclerView做任何改动
 * <p>
 * 有诗为证：
 * 远看像棵树
 * 近看不是树
 * 似树而非树
 * 是为牛逼树
 */
public class ListTree {


    public class TreeNode {
        //实际的数据
        private Object data;

        //本node所使用的layout id
        private int layoutResId;

        //儿子们的数量，儿子们是紧挨着爸爸依次放置的，
        // 注意与expandDescendantCount不一样，不论收起还是展开，都有效
        private int childrenCount = 0;

        //其所有子孙们的数量，指的是所有在Nodes中的子孙的数量，不展开的不计，这个在收起必须为0,在展开时才有效
        private int expandDescendantCount = 0;

        private TreeNode parent = null;

        private boolean checked;

        //是否显示展开－收起图标
        private boolean showExpandIcon = true;

        //当此节点折叠时，其子孙们不能再位于List中，所以移到这里来保存。
        //当此节点展开时，再移到List中
        private List<TreeNode> collapseDescendant;
        private boolean expand;

        private TreeNode(TreeNode parent, Object data, int layoutResId) {
            this.parent = parent;
            this.data = data;
            this.layoutResId = layoutResId;
        }

        //把子孙们收回
        void retractDescendant(List<TreeNode> collapseChildren) {
            //Must clone a array
            this.collapseDescendant = new ArrayList<>();
            for (TreeNode node : collapseChildren) {
                this.collapseDescendant.add(node);
            }
        }

        //把子孙们抽走
        List<TreeNode> extractDescendant() {
            List<TreeNode> ret = this.collapseDescendant;
            this.collapseDescendant = null;
            return ret;
        }

        public TreeNode getParent() {
            return parent;
        }

        void setExpand(boolean expand) {
            this.expand = expand;
        }

        public Object getData() {
            return data;
        }

        public int getLayoutResId() {
            return layoutResId;
        }

        public boolean isExpand() {
            return expand;
        }

        public boolean isChecked() {
            return checked;
        }

        public boolean isShowExpandIcon() {
            return showExpandIcon;
        }

        public void setShowExpandIcon(boolean b) {
            showExpandIcon = b;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        void setDescendantChecked(boolean b) {
            if (this.collapseDescendant == null) {
                return;
            }

            if (this.expand) {
                throw new IllegalStateException("Only can be invoked when node is collapsed");
            }

            for (TreeNode node : this.collapseDescendant) {
                node.setChecked(b);
            }
        }

        public int getChildrenCount() {
            return childrenCount;
        }

        public int getExpandDescendantCount() {
            return expandDescendantCount;
        }


        //仅在处于收起状态时才被调用
        void enumCheckedNodes(EnumOptionFunc optFunc) {
            if (this.expand) {
                throw new IllegalStateException("Only can be invoked when node is collapsed");
            }

            if(this.collapseDescendant == null){
                return ;
            }

            for (TreeNode node : this.collapseDescendant) {
                optFunc.option(node);
                if (!node.isExpand()) {
                    node.enumCheckedNodes(optFunc);
                }
            }
        }

        void removeCheckedChildren() {
            if(this.collapseDescendant == null) {
                return;
            }

            for(int i=0;i<this.collapseDescendant.size();i++){
                if(this.collapseDescendant.get(i).checked){
                    this.collapseDescendant.remove(i);
                    i--;
                }else{
                    //如果没有被check，要查看起孩子是否被check
                    this.collapseDescendant.get(i).removeCheckedChildren();
                }
            }
        }
    }

    /**
     * 用于遍历，代表遍历的位置
     * 注意，在树遍历过程中切不可改变树结构！！！！！！！！！！！！！！
     */
    @Deprecated
    public class EnumPos {
        private class TreeEnumInfo {
            //当前的Node List，因为收起的Node的儿子或儿孙也是位于一个List中的
            private List<TreeNode> nodeList;
            //当前nodeList上的第几个
            private int planeIndex;

            public TreeEnumInfo(List<TreeNode> nodeList, int planeIndex) {
                this.nodeList = nodeList;
                this.planeIndex = planeIndex;
            }
        }

        private Stack<TreeEnumInfo> treeEnumStack = new Stack<>();

        private EnumPos() {
            treeEnumStack.push(new TreeEnumInfo(nodes, 0));
        }
    }

    private int rootNodesCount;

    //用List保存整棵树
    private List<TreeNode> nodes = new ArrayList<>();


    /**
     * @param parent      其父node，若添加root node，则父node传null即可
     * @param data        node中所包含的用户数据
     * @param layoutResId node的Item layout资源id
     * @return 刚添加的node
     */
    public TreeNode addNode(TreeNode parent, Object data, int layoutResId) {
        TreeNode node = new TreeNode(parent, data, layoutResId);
        if (parent == null) {
            //root node,append to end
            nodes.add(node);
            rootNodesCount++;
        }else {
            //插入非root node，有爹
            if (parent.isExpand()) {
                //如果parent当前状态是展开的
                int index = nodes.indexOf(parent);
                index += parent.expandDescendantCount;

                //插到最后一个子孙的后面
                nodes.add(index + 1, node);
                //需追溯它所有的长辈，为每个都更新其子孙数量
                TreeNode ancestor = parent;
                while (ancestor != null) {
                    ancestor.expandDescendantCount++;
                    ancestor = ancestor.parent;
                }
            } else {
                //如果parent当前状态是收起的
                if (parent.collapseDescendant == null) {
                    parent.collapseDescendant = new ArrayList<>();
                }
                parent.collapseDescendant.add(node);
            }

            //add children count
            parent.childrenCount++;
        }

        return node;
    }

//    /**
//     * Get a List witch conatins all items;
//     * @return List
//     */
//    public List<TreeNode> getNodeList(){
//        //如果有节点处于收起状态，底层是List中就不能包含所有的Item
//        //
//        return this.nodes;
//    }

    /**
     * 开始遍历
     * 如果返回不为null，则可以继续调用getNextNode()进行遍历。
     * 遍历顺序并不一定符合树的遍历顺序。
     *
     * @return 根上第一个node的位置，如果为null，则不能继续调用getNextNode()
     */
    @Deprecated
    public EnumPos startEnumNode() {
        if (nodes.isEmpty()) {
            return null;
        }

        return new EnumPos();
    }

    /**
     * 用于遍历，获取下一个节点
     * 注意！！返回的与参数其实是一个对象！
     *
     * @param pos 当前节点的位置，既是输入参数也是输出参数
     * @return 返回的其实是改变了内部属性的参数pos，当返回为null时，需停止遍历
     */
    @Deprecated
    public EnumPos enumNext(EnumPos pos) {
        EnumPos.TreeEnumInfo info = pos.treeEnumStack.peek();
        TreeNode curNode = info.nodeList.get(info.planeIndex);
        if (curNode.getChildrenCount() > 0 && !curNode.isExpand()) {
            //如果这个Node没展开且有儿子，则需要遍历它的儿子
            pos.treeEnumStack.push(pos.new TreeEnumInfo(curNode.collapseDescendant, 0));
            return pos;
        }
        //如果有儿子且展开了，planeIndex+1，或者没有孩子
        while (!pos.treeEnumStack.empty()) {
            pos.treeEnumStack.peek().planeIndex++;
            if (pos.treeEnumStack.peek().planeIndex == pos.treeEnumStack.peek().nodeList.size()) {
                //如果是当前List的最后一个了，则弹出
                pos.treeEnumStack.pop();
            } else {
                return pos;
            }
        }
        return null;
    }

    /**
     * 获取当前遍历到的节点
     *
     * @param pos 节点序号
     * @return 节点对象，必不为null
     */
    @Deprecated
    public TreeNode getNodeByEnumPos(EnumPos pos) {
        TreeNode node = pos.treeEnumStack.peek().nodeList.get(pos.treeEnumStack.peek().planeIndex);
        return node;
    }

    /**
     * @param parent      爸爸
     * @param position    属于爸爸的第几子
     * @param data        包含的用户数据
     * @param layoutResId layout资原id
     * @return 刚添加的node
     */
    public TreeNode insertNode(TreeNode parent, int position, Object data, int layoutResId) {
        TreeNode node = new TreeNode(parent, data, layoutResId);

        if (parent == null) {
            //insert root node
            if (position > rootNodesCount) {
                //插入位置超出范围
                return null;
            }
            insertRootNode(nodes, position,node);
            rootNodesCount++;
        }else {
            //插入非root node，有爹
            if (position > parent.childrenCount) {
                //插入位置超出范围
                return null;
            }
            insertNoRootNode(nodes,parent,position,node);
            //add children count
            parent.childrenCount++;
        }

        return node;
    }

    /**
     * @param position 排行第几，树角度的序号
     * @param node
     */
    private void insertRootNode(List<TreeNode> nodeList,int position, TreeNode node){
        //因为有的哥哥是展开的，所以需要遍历它们才能确定位置
        //TreeNode prevSibling =null;
        TreeNode nextSibling = nodeList.get(0);
        int i=0;
        while(i<position){
            //prevSibling = nextSibling;
            nextSibling = getNextSibling(nextSibling);
            i++;
        }

        if(nextSibling!=null) {
            //找到位置了，取得此位置的node在list中的序号，这才是插入的位置
            int index=nodeList.indexOf(nextSibling);
            nodeList.add(index,node);
        }else{
            //向最后添加
            nodeList.add(node);
        }
    }

    /**
     * @param position 排行第几，树角度的序号
     * @param node
     */
    private void insertNoRootNode(List<TreeNode> nodeList,TreeNode parent, int position, TreeNode node){
        if (parent.isExpand()) {
            //如果是展开的，往nodes中插入
            //因为有的哥哥是展开的，所以需要遍历它们才能确定位置
            TreeNode prevSibling = null;
            //获取第一个儿子
            TreeNode nextSibling = nodeList.get(nodeList.indexOf(parent)+1);
            int i=0;
            while(i<position){
                prevSibling = nextSibling;
                nextSibling = getNextSibling(nextSibling);
                i++;
            }

            //添到数组
            if(nextSibling!=null) {
                //找到位置了，取得此位置的node在list中的序号，这才是插入的位置
                int index=nodeList.indexOf(nextSibling);
                nodeList.add(index,node);
            }else{
                //应加到最后一个兄弟的后面，注意，可能此兄弟后面还有祖辈，所以还需要确定位置进行插入
                int index = nodeList.indexOf(prevSibling);
                nodeList.add(index+prevSibling.expandDescendantCount+1,node);
            }

            //需追溯它所有的长辈，为每个都更新其子孙数量
            TreeNode ancestor = parent;
            while (ancestor != null) {
                ancestor.expandDescendantCount++;
                ancestor = ancestor.parent;
            }
        } else {
            //如果非展开，向爸爸自己的数组中插入
            if (parent.collapseDescendant == null) {
                parent.collapseDescendant = new ArrayList<>();
            }

            insertRootNode(parent.collapseDescendant,position,node);
        }
    }

    /**
     * @param parent if null , return root node
     * @param index  它是从树的角度来讲的序号
     * @return plane index,<0 means exceed range
     *
     * 必须计算其哥哥展开子孙的情况
     */
    private int getNodePlaneIndexByIndex(TreeNode parent, int index) {
        if (!parent.isExpand()) {
            throw new IllegalStateException("Only invoke when parent is expand");
        }

        int range = 0;
        if (parent == null) {
            //get index of root node
            range = this.rootNodesCount;
        } else {
            range = parent.childrenCount;
        }

        //FIXME:有bug！
        int planeIndex = 0;

        for (int i = 0; i < range; i++) {
            if (i == index) {
                return planeIndex;
            } else {
                //指向下一个root node
                TreeNode node = nodes.get(planeIndex);
                planeIndex += node.isExpand() ? node.expandDescendantCount : 0;
                planeIndex++;
            }
        }

        return -1;
    }

    //返回被删除了Item的start plane index和count
    public Pair<Integer, Integer> clearDescendant(TreeNode treeNode) {
        if (treeNode.childrenCount == 0) {
            //如果没有儿子，无法收起
            return null;
        }

        //如果有儿子，把子孙们从List中取出来
        int nodePlaneIndex = nodes.indexOf(treeNode);
        Pair<Integer, Integer> ret = new Pair<Integer, Integer>(
                nodePlaneIndex + 1, treeNode.expandDescendantCount);
        List<TreeNode> descendant = nodes.subList(
                nodePlaneIndex + 1,
                nodePlaneIndex + 1 + treeNode.expandDescendantCount);
        descendant.clear();

        treeNode.childrenCount = 0;
        treeNode.expandDescendantCount = 0;
        treeNode.collapseDescendant = null;

        return ret;
    }

    /**
     * remove node and its descendant
     *
     * @param node node to be removed
     */
    public void removeNode(TreeNode node) {
        TreeNode parent = node.parent;
        if (parent == null || parent.isExpand()) {
            int expandDescendantCount = node.expandDescendantCount;
            int index = nodes.indexOf(node);
            List ret = nodes.subList(index, index + expandDescendantCount + 1);
            ret.clear();//remove nodes and its descendant

            if (parent != null) {
                parent.childrenCount++;

                //需追溯它所有的长辈，为每个都更新其子孙数量
                TreeNode ancestor = parent;
                while (ancestor != null) {
                    ancestor.expandDescendantCount -= expandDescendantCount;
                    ancestor = ancestor.parent;
                }
            } else {
                rootNodesCount--;
            }
        } else {
            if (parent == null) {
                rootNodesCount--;
            } else {
                parent.childrenCount++;
                parent.expandDescendantCount--;
            }
            nodes.remove(node);
        }
    }

    //获取节点在列表中的索引
    public TreeNode getNodeByPlaneIndex(int index) {
        return nodes.get(index);
    }

    /**
     * 获取在nodes中的绝对位置
     * @param node
     * @return 如果返回－1，说明node不在树中或处于被收起状态，此时它没有Plane Index
     */
    public int getNodePlaneIndex(TreeNode node) {
        return nodes.indexOf(node);
    }

    //获取一个节点在其爸爸里的位置，也就是它在兄弟中排行老几
    public int getNodeRank(TreeNode node){
        List<TreeNode> nodeList = getNodeContainer(node);
        TreeNode parent = node.getParent();

        //看其爸爸是否也在同一个List中
        if (parent!=null){
            int index = nodeList.indexOf(parent);
            if(index >= 0){
                //爸爸在这个list里面，爸爸后面就是第一个儿子
                TreeNode node1 = nodeList.get(index+1);
                int i=0;
                //比较各儿子是否与node相同
                while (node1 != null && node1!=node){
                    node1=getNextSibling(nodeList,node1);
                    i++;
                }
                if(node1!=null){
                    return i;
                }else {
                    //没有找到这个儿子，不可能！
                    throw new RuntimeException("Can`t find node in tree");
                }
            }else{
                //爸爸不在这个list里面，那这个list就是爸爸的（1）
            }
        }else{
            //说明是root Node，与（1）处处理方式相同
        }

        //node是nodeList中的root node
        TreeNode node1 = nodeList.get(0);
        int i=0;
        //比较各儿子是否与node相同
        while (node1 != null && node1!=node){
            node1=getNextSibling(nodeList,node1);
            i++;
        }

        if(node1==null){
            //没有找到这个儿子，不可能！
            throw new RuntimeException("Can`t find node in tree");
        }else{
            return i;
        }
    }

    //判断一个node在nodes中，还是在某个节点的collapseDescendant中
    private List<TreeNode> getNodeContainer(TreeNode node){
        TreeNode parent = node.getParent();
        while (parent!=null){
            if(parent.expandDescendantCount==0){
                //说明收起了，在collapseDescendant中
                return parent.collapseDescendant;
            }
        }

        //到这里说明在nodes中
        return nodes;
    }

    public TreeNode getNextSibling(TreeNode node){
        //判断这个node是在nodes中还是在某个Node的collapseDescendant中
        List<TreeNode> nodeList = getNodeContainer(node);

        int index = nodeList.indexOf(node)+1;
        if(nodeList.size()==index){
            return null;
        }else {
            TreeNode ret = nodeList.get(index + node.expandDescendantCount);
            //真的是node的弟弟吗？
            if (ret.getParent() == node.getParent()) {
                return ret;
            } else {
                //没有弟弟了
                return null;
            }
        }
    }

    //为了避免多次调用getNodeContainer而提供专用于内部的
    private TreeNode getNextSibling(List<TreeNode> nodeList, TreeNode node){
        int index = nodeList.indexOf(node)+1;
        TreeNode ret = nodeList.get(index+node.expandDescendantCount);
        //真的是node的弟弟吗？
        if(ret.getParent() == node.getParent()){
            return ret;
        }else{
            //没有弟弟了
            return null;
        }
    }

    public TreeNode getPrevSibling(TreeNode node){
        //判断这个node是在nodes中还是在某个Node的collapseDescendant中
        List<TreeNode> nodeList = getNodeContainer(node);
        //node在nodes中
        int index = nodeList.indexOf(node);

        //如果它是老大，则没哥了
        if(index==0){
            return null;
        }

        //如何找它哥哥？先找它左边的node，看是不是，如果不是，必定是它的堂后辈，
        //找这个后辈的爸爸，看它的爸爸与node是否相同
        TreeNode prevNode = nodeList.get(index-1);
        while(prevNode != null){
            if(prevNode.getParent()==node.getParent()){
                //找到了
                return prevNode;
            }
            prevNode = prevNode.getParent();
        }

        return null;
    }


    public int expandNode(TreeNode node) {
        int nodePlaneIndex = nodes.indexOf(node);

        if (node.isExpand()) {
            return 0;
        }

        node.setExpand(true);
        if (node.childrenCount == 0) {
            //如果没有儿子，无法展开
            return 0;
        }

        //如果有儿子，把儿子们移到List中
        List<TreeNode> descendant = node.extractDescendant();
        nodes.addAll(nodePlaneIndex + 1, descendant);
        node.expandDescendantCount = descendant.size();

        //需追溯它所有的长辈，为每个都更新其子孙数量
        TreeNode ancestor = node.parent;
        while (ancestor != null) {
            ancestor.expandDescendantCount += node.expandDescendantCount;
            ancestor = ancestor.parent;
        }

        return node.expandDescendantCount;
    }

    public int expandNode(int nodePlaneIndex) {
        TreeNode node = nodes.get(nodePlaneIndex);
        return expandNode(node);
    }

    //返回影响到的Node们的数量
    public int collapseNode(TreeNode node) {
        int nodePlaneIndex = nodes.indexOf(node);
        if (!node.isExpand()) {
            return 0;
        }
        node.setExpand(false);

        if (node.childrenCount == 0) {
            //如果没有儿子，无法收起
            return 0;
        }

        //如果有子孙，把子孙们从List中取出来自己保存
        List<TreeNode> descendant = nodes.subList(
                nodePlaneIndex + 1, nodePlaneIndex + 1 + node.expandDescendantCount);

        node.retractDescendant((List<TreeNode>) descendant);
        //在List中删掉这一段
        descendant.clear();

        //需追溯它所有的长辈，为每个都更新其子孙数量
        TreeNode ancestor = node.parent;
        while (ancestor != null) {
            ancestor.expandDescendantCount -= node.expandDescendantCount;
            ancestor = ancestor.parent;
        }

        int ret = node.expandDescendantCount;
        node.expandDescendantCount = 0;
        return ret;
    }

    public int collapseNode(int nodePlaneIndex) {
        TreeNode node = nodes.get(nodePlaneIndex);
        return collapseNode(node);
    }

    public int setDescendantChecked(int nodePlaneIndex, boolean b) {
        TreeNode node = nodes.get(nodePlaneIndex);
        if (node.isExpand()) {
            int start = nodePlaneIndex + 1;
            int count = node.expandDescendantCount;
            for (int i = start; i < start + count; i++) {
                nodes.get(i).setChecked(b);
            }
            return node.expandDescendantCount;
        } else {
            node.setDescendantChecked(b);
            return 0;
        }
    }

    public int size() {
        return nodes.size();
    }

    public int getNodeLayerLevel(TreeNode node) {
        int count = 0;
        TreeNode parent = node.parent;
        while (parent != null) {
            count++;
            parent = parent.parent;
        }
        return count;
    }

    public void removeCheckedNodes() {
        //从前往后遍历，先找到的是父辈，如果父辈被删，其子孙必然被删掉
        //所以只需删一个分支上的最外层被选中的就可以。
        ArrayList<TreeNode> nodeToDel = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            TreeNode node = nodes.get(i);
            if (node.isChecked()) {
                //注意不用判断一个Node是否为展开状态，直接这样删是没问题的，
                // 因为未展开时expandDescendantCount为0
                List<TreeNode> it_and_descendant = nodes.subList(i, i + 1 + node.expandDescendantCount);
                nodes.removeAll(it_and_descendant);
                //nodeToDel.addAll(it_and_descendant);

                //更新其爸爸的儿子数，更新其父辈的子孙数
                TreeNode ancestor = node.parent;
                if (ancestor != null) {
                    ancestor.childrenCount--;
                }

                while (ancestor != null) {
                    ancestor.expandDescendantCount -= node.expandDescendantCount + 1;
                    ancestor = ancestor.parent;
                }
                i--;
            } else {
                //如果这个Node没被选中，但是还要查看起孩子是否被选中
                node.removeCheckedChildren();
            }
        }
    }

    @FunctionalInterface
    public interface EnumOptionFunc {
        void option(TreeNode node);
    }

    @Deprecated
    //使用此方法，可以改变node对各种属性，但切不可
    public void enumCheckedNodes(EnumOptionFunc optFunc) {
        //如果是展开的，继续遍历
        //如果是收起的，应遍历它自己所保存的子孙们
        for (int i = 0; i < nodes.size(); i++) {
            TreeNode node = nodes.get(i);
            if (node.isChecked()) {
                optFunc.option(node);
                if (!node.isExpand()) {
                    node.enumCheckedNodes(optFunc);
                }
            }
        }
    }

    public void expandAllNodes() {
        for (int i = 0; i < nodes.size(); i++) {
            TreeNode node = nodes.get(i);
            expandNode(node);
        }
    }

    public void collapseAllNodes() {
        for (int i = 0; i < nodes.size(); i++) {
            TreeNode node = nodes.get(i);
            collapseNode(node);
        }
    }

    public void printList(){
        for(TreeNode node : nodes){
            //看看自己在哪一层
            int level = getNodeLayerLevel(node);

            StringBuilder sb = new StringBuilder();
            for(int i=0;i<level;i++){
                sb.append('\t');
            }

            System.out.println(sb.toString()+node.getData().toString());
        }
    }

    private class Couple {
        List<TreeNode> list;
        int curListPos;

        public Couple(List<TreeNode> list, int curListPos) {
            this.list = list;
            this.curListPos = curListPos;
        }
    }

    /**
     * 对整个树进行遍历，可以替代enumCheckedNodes方法，只需要对Node的isChecked判断即可
     * @param optFunc (node)->{options...}
     */
    public void forEach(EnumOptionFunc optFunc){
        //以树的形式顺序遍历所有节点

        //顶端是当前正在搞的List
        Stack<Couple> listStack = new Stack<>();
        listStack.push(new Couple(nodes,0));

        do {
            if(listStack.peek().list.size()>listStack.peek().curListPos) {
                TreeNode node = listStack.peek().list.get(listStack.peek().curListPos);
                optFunc.option(node);
                listStack.peek().curListPos++;

                if (node.collapseDescendant != null && !node.collapseDescendant.isEmpty()) {
                    //如果不为空，搞这list
                    listStack.push(new Couple(node.collapseDescendant, 0));
                }
            }else {
                listStack.pop();
            }
        }while(!listStack.empty());
    }

    /**
     * 获取第一个根Node
     * @return Node or Null
     */
    public TreeNode getFirstNode() {
        if(nodes.isEmpty()){
            return null;
        }else {
            return nodes.get(0);
        }
    }

    /**
     * 获取node的第一个孩子
     * @param node
     * @return Node or Null
     */
    public TreeNode getFirstChild(TreeNode node){
        if(node.childrenCount==0){
            return null;
        }
        List<TreeNode> nodeList = getNodeContainer(node);
        return getFirstChild(nodeList,node);
    }

    private TreeNode getFirstChild(List<TreeNode> nodeList,TreeNode node){
        if(node.childrenCount==0){
            return null;
        }

        if(node.isExpand()){
            int index=nodeList.indexOf(node);
            return nodeList.get(index+1);
        }else {
            return node.collapseDescendant.get(0);
        }
    }

    /**
     * 获取参数node的最后一个孩子
     * @param node
     * @return Node or Null
     */
    public TreeNode getLastChild(TreeNode node){
        if(node.childrenCount==0){
            return null;
        }

        List<TreeNode> nodeList = getNodeContainer(node);
        ListTree.TreeNode child = getFirstChild(nodeList,node);
        ListTree.TreeNode last = child;
        while(child!=null) {
            child = getNextSibling(nodeList, child);
            last=child;
        }
        return last;
    }

    /**
     * 跟据排行获取节点
     * @param parent
     * @param rank
     * @return Node or Null
     */
    public TreeNode getNodeByRank(TreeNode parent, int rank){
        if(rank >= parent.childrenCount){
            return null;
        }

        List<TreeNode> nodeList = getNodeContainer(parent);
        ListTree.TreeNode child = getFirstChild(nodeList,parent);
        int i=0;
        while(child!=null) {
            if(i==rank){
                return child;
            }
            child = getNextSibling(nodeList, child);
            i++;
        }
        return null;
    }
}

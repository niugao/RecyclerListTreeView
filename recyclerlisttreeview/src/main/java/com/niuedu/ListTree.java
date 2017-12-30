package com.niuedu;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by nkm on 27/12/2017.
 *
 * 以List的形式表式树。用起来感觉是树，但实际上是个List。这带来很多好处：
 * 1 首先就是没有递归算法。该用递归的地方全部变成了循环。
 * 2 其次是有序，插入节点时，可以指定它是它爸爸的第几个儿子。
 * 3 最后就是极其适合在RecyclerView中使用。使用此类做后台数据，
 *   跟List无异，无论根节点行还是子节点行都是RecyclerView中的一行。
 * 4 不需对RecyclerView做任何改动
 *
 * 有诗为证：
 * 远看像棵树
 * 近看不是树
 * 似树而非树
 * 是为牛逼树
 */
public class ListTree {

    public class TreeNode{
        //实际的数据
        private Object data;

        //本node所使用的layout id
        private int layoutResId;

        //儿子们的数量，儿子们一定是紧挨着爸爸放置的
        private int childrenCount=0;

        //其所有子孙们的数量
        private int descendantCount=0;

        private TreeNode parent=null;

        private boolean checked;

        //是否显示展开－收起图标
        private boolean showExpandIcon=true;

        //当此节点折叠时，其子孙们不能再位于List中，所以移到这里来保存。
        //当此节点展开时，再移到List中
        private List<TreeNode> collapseDescendant=new ArrayList<>();
        private boolean expand;
        private int layerLevel;

        private TreeNode(TreeNode parent, Object data,int layoutResId){
            this.parent=parent;
            this.data=data;
            this.layoutResId=layoutResId;
        }

        public int getChildrenCount() {
            return childrenCount;
        }

        public void setChildrenCount(int count) {
            this.childrenCount = count;
        }

        public int getDescendantCount() {
            return descendantCount;
        }

        public void setDescendantCount(int descendantCount) {
            this.descendantCount = descendantCount;
        }

        public void setParent(TreeNode node){
            this.parent=node;
        }

        public TreeNode getParent() {
            return parent;
        }

        //把子孙们收回
        public void retractDescendant(List<TreeNode> collapseChildren) {
            //Must clone a array
            this.collapseDescendant = new ArrayList<>();
            for(TreeNode node:collapseChildren){
                this.collapseDescendant.add(node);
            }
        }

        //把子孙们抽走
        public List<TreeNode> extractDescendant() {
            List<TreeNode> ret=this.collapseDescendant;
            this.collapseDescendant=null;
            return  ret;
        }

        public void setExpand(boolean expand) {
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

        /**
         * 添加一个儿子，只有处于收起状态时才能添加
         * @param node child to add
         */
        public void addNode(TreeNode node) {
            if(this.expand){
                throw new IllegalStateException("Only can invoke when node is collapsed");
            }

            this.collapseDescendant.add(node);
            this.childrenCount++;
            this.descendantCount++;
        }

        public void addNode(TreeNode node,int index) {
            if(this.expand){
                throw new IllegalStateException("Only can invoke when node is collapsed");
            }

            this.collapseDescendant.add(index,node);
            this.childrenCount++;
            this.descendantCount++;
        }

        public boolean isChecked() {
            return checked;
        }

        public boolean isShowExpandIcon() {
            return showExpandIcon;
        }

        public void setShowExpandIcon(boolean b){
            showExpandIcon=b;
        }
        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public void setDescendantChecked(boolean b) {
            if(this.expand){
                throw new IllegalStateException("Only can invoke when node is collapsed");
            }

            for(TreeNode node:this.collapseDescendant){
                node.setChecked(b);
            }
        }

    }

    private int rootNodesCount;

    //用List保存整棵树
    private List<TreeNode> nodes=new ArrayList<>();


    /**
     * @param parent 其父node，若添加root node，则父node传null即可
     * @param data node中所包含的用户数据
     * @param layoutResId node的行layout资源id
     * @return 刚添加的node
     */
    public TreeNode addNode(TreeNode parent,Object data,int layoutResId){
        TreeNode node=new TreeNode(parent,data,layoutResId);
        if(parent==null){
            //root node,append to end
            nodes.add(node);
            return node;
        }

        //插入非root node，有爹

        if(parent.isExpand()){
            //如果parent当前状态是展开的
            int index = nodes.indexOf(parent);
            index += parent.getDescendantCount();

            //插到最后一个子孙的后面
            nodes.add(index+1,node);
            //add children count
            if(parent!=null) {
                parent.setChildrenCount(parent.getChildrenCount() + 1);

                //需追溯它所有的长辈，为每个都更新其子孙数量
                TreeNode ancestor = parent;
                while(ancestor!=null){
                    ancestor.setDescendantCount(ancestor.getDescendantCount()+1);
                    ancestor=ancestor.getParent();
                }
            }else{
                rootNodesCount++;
            }
        }else{
            //如果parent当前状态是收起的
            parent.addNode(node);
        }

        return node;
    }

    /**
     *
     * @param parent 爸爸
     * @param position 属于爸爸的第几子
     * @param data 包含的用户数据
     * @param layoutResId layout资原id
     * @return 刚添加的node
     */
    public TreeNode insertNode(TreeNode parent,int position,Object data,int layoutResId){
        if(position > parent.getChildrenCount()){
            //插入位置超出范围
            return null;
        }

        TreeNode node = new TreeNode(parent, data, layoutResId);
        if(parent.isExpand()) {
            int planePosition = getNodePlaneIndexByIndex(parent, position);
            nodes.add(planePosition, node);

            //add children count
            if (parent != null) {
                parent.setChildrenCount(parent.getChildrenCount() + 1);

                //需追溯它所有的长辈，为每个都更新其子孙数量
                TreeNode ancestor = parent;
                while (ancestor != null) {
                    ancestor.setDescendantCount(ancestor.getDescendantCount() + 1);
                    ancestor = ancestor.getParent();
                }

            } else {
                rootNodesCount++;
            }
        }else{
            parent.addNode(node,position);
        }

        return node;
    }

    /**
     * @param parent if null , return root node
     * @param index 它是从树的角度来讲的序号
     * @return plane index,<0 means exceed range
     */
    private int getNodePlaneIndexByIndex(TreeNode parent, int index) {
        if(!parent.isExpand()){
            throw new IllegalStateException("Only invoke when parent is expand");
        }

        int range = 0;
        if(parent==null) {
            //get index of root node
            range = this.rootNodesCount;
        }else {
            range = parent.getChildrenCount();
        }

        int planeIndex = 0;

        for(int i=0;i<range;i++){
            if(i == index){
                return planeIndex;
            }else{
                //指向下一个root node
                TreeNode node=nodes.get(planeIndex);
                planeIndex += node.isExpand() ? node.getDescendantCount() : 0;
                planeIndex++;
            }
        }

        return -1;
    }

    /**
     * remove node and its descendant
     * @param node node to be removed
     */
    public void removeNode(TreeNode node){
        TreeNode parent=node.getParent();
        if(parent.isExpand()) {
            int descendantCount = node.getDescendantCount();
            int index = nodes.indexOf(node);
            List ret = nodes.subList(index, index + descendantCount + 1);
            ret.clear();//remove nodes and its descendant

            if (parent != null) {
                parent.setChildrenCount(parent.getChildrenCount() - 1);
                //需追溯它所有的长辈，为每个都更新其子孙数量
                TreeNode ancestor = parent;
                while (ancestor != null) {
                    ancestor.setDescendantCount(ancestor.getDescendantCount() - descendantCount);
                    ancestor = ancestor.parent;
                }
            } else {
                rootNodesCount--;
            }
        }else{
            if(parent==null){
                rootNodesCount--;
            }else {
                parent.setChildrenCount(parent.getChildrenCount() - 1);
                parent.setDescendantCount(parent.getDescendantCount()-1);
            }
            nodes.remove(node);
        }
    }

    //获取节点在列表中的索引
    public TreeNode getNodeByPlaneIndex(int index){
        return nodes.get(index);
    }

    public int getNodePlaneIndex(TreeNode node) {
        return nodes.indexOf(node);
    }

    public int expandNode(int nodePlaneIndex){
        TreeNode node=nodes.get(nodePlaneIndex);

        if(node.isExpand()){
            throw new IllegalStateException("Only invoke when parent is collesped");
        }

        node.setExpand(true);
        if(node.getChildrenCount()==0){
            //如果没有儿子，无法展开
            return 0;
        }

        //如果有儿子，把儿子们移到List中
        List<TreeNode> descendant = node.extractDescendant();
        nodes.addAll(nodePlaneIndex+1,descendant);

        //需追溯它所有的长辈，为每个都更新其子孙数量
        TreeNode ancestor = node.getParent();
        while(ancestor!=null){
            ancestor.setDescendantCount(ancestor.getDescendantCount()+node.getDescendantCount());
            ancestor=ancestor.getParent();
        }

        return node.getDescendantCount();
    }

    //返回影响到的Node们的数量
    public int collapseNode(int nodePlaneIndex) {
        TreeNode node=nodes.get(nodePlaneIndex);
        if(!node.isExpand()){
            throw new IllegalStateException("Only invoke when parent is expand");
        }
        node.setExpand(false);

        if (node.getChildrenCount() == 0) {
            //如果没有儿子，无法收起
            return 0;
        }


        //如果有儿子，把子孙们从List中取出自己保存
        List<TreeNode> descendant = nodes.subList(
                nodePlaneIndex + 1, nodePlaneIndex + 1 + node.getDescendantCount());

        node.retractDescendant((List<TreeNode>) descendant);
        //在List中删掉这一段
        descendant.clear();

        //需追溯它所有的长辈，为每个都更新其子孙数量
        TreeNode ancestor = node.getParent();
        while (ancestor != null) {
            ancestor.setDescendantCount(ancestor.getDescendantCount() - node.getDescendantCount());
            ancestor = ancestor.getParent();
        }

        return node.getDescendantCount();
    }

    public int setDescendantChecked(int nodePlaneIndex, boolean b) {
        TreeNode node=nodes.get(nodePlaneIndex);
        if (node.isExpand()) {
            int start = nodePlaneIndex + 1;
            int count = node.getDescendantCount();
            for (int i = start; i < start + count; i++) {
                nodes.get(i).setChecked(b);
            }
            return node.getDescendantCount();
        } else {
            node.setDescendantChecked(b);
            return 0;
        }
    }

    public int size() {
        return nodes.size();
    }

    public int getNodeLayerLevel(TreeNode node) {
        int count=0;
        TreeNode parent = node.getParent();
        while(parent!=null){
            count++;
            parent=parent.getParent();
        }
        return count;
    }
}

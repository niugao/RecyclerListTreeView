# RecyclerListTreeView

`The fastest android Tree View bases on RecyclerView in the world!!`

I am special because:<br/>
* The data store is looking like a tree but in fact it is a list.<br/>
* Not changed any thing on RecyclerView.<br/>
* No recursion,only cycle，so no worry about stack overflow.<br/>
* User will not feel any different from using way of origin Recycler.<br/>
* Less half amount of code than other's implemetion.<br/>

---
update:
### 0.1.5:
主要更新了Android SDK版本和Gradle版本.

增加了Kotlin版的测试App模块.
### 0.1.6:
修正的了一个bug：如果四五个顶级父类，当第一个顶级父类处于展开的情况下，删除最后一个顶级父类，程序会崩溃。

### 0.1.7:
- 改变了很多方法的访问性质，提高了封装性；
- 更新了Grable插件版本；
- 添加了新的方法：枚举所有的节点；
- 添加了新的方法：展开全部Node与收起全部Node；
- 修正bug：当一个node收起时，如果其子Node被选中，无法删除这个子node；

### 0.1.8 :
- 添加从获取节点的相邻哥哥和弟弟的方法：getNextSibling(),getPrevSibling()；
- 添加获取一个节在爸爸中的排行的方法；getNodeRankIndex();
- 修正插入节点时的bug；

---

`基于RecyclerView，自认为是当前最快的 Android tree view 的实现！！`

存储数据的结构并不是Tree，而是一个ArrayList。<br/>
与所有已知的网上的实现都不一样，看起来大家似乎都跳不出固定思维。
可以比较一下代码量，此实现比其它的少一半都不止.
<br/><br/>
核心是一个表示Tree的类，但它的本质是一个List。对RecyclerView没有任何改动，对Adapter只有少量封装，
使用者不会产生任何陌生感。也就是说你对原生RecyclerView能做的，现在依然能做。

<br/><br/>
注意一个Node有两个序号，一个是Plane Index，是它在List中的绝对位置；另一个是Rank Index，是它在兄弟中的排行，也就是爸爸的第几个儿子。
<br/><br/>

以List的形式表式树，带来很多好处：<br/>
* 大部分情况下，消除了递归。该用递归的地方基本都变成了循环。<br/>
* 节点有序，插入节点时，可以指定它是它爸爸的第几个儿子。<br/>
* 极其适合在RecyclerView中使用：与List无异，无论根节点还是子节点都对应RecyclerView中的一行，不需对RecyclerView做任何改动<br/>

有诗为证：<br/>
远看像棵树<br/>
近看不是树<br/>
似树而非树<br/>
是为牛逼树<br/>

---

## Useage

Gradle : implementation 'com.niuedu:recyclerlisttreeview:0.1.7'

## Reference
[CSDN:最快的Android TreeView出现了！](http://blog.csdn.net/nkmnkm/article/details/78985540)

---

## License

   Copyright 2017 niugao.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

---

![](https://github.com/niugao/RecyclerListTreeView/blob/master/snapshots/1.png)
![](https://github.com/niugao/RecyclerListTreeView/blob/master/snapshots/2.png)
![](https://github.com/niugao/RecyclerListTreeView/blob/master/snapshots/3.png)
![](https://github.com/niugao/RecyclerListTreeView/blob/master/snapshots/4.png)

---

## Useage:<br/>
### 创建ListTree实例，向其中添加每行的数据（一行是一个节点）。每个节点是一个ListTree.TreeNode实例。要在行中显示不同的数据，需要自定义不同的Model类，通过TreeNode.getData()获取Model实例，并进行类型转换转成实际的类型。
### 从ListTreeAdapter派生自己的Adpater,重写其onCreateNodeView()和onBindNodeViewHolder()方法，实现方式与RecyclerView.Adpater中相应的方法无异。注意viewType被我定死了，即那一行的layout资源ID。
### 在ListTree中已提供了Check一行的支持，如果要实现，需要你自己在行layout中添加对应的控件（比如Switch或ToggleButton）并与TreeNode的Check状态关联。
### 推荐Android入门好书：《Android9编程通俗演义》－清华大学出版社，京东淘宝及各大书店有售！




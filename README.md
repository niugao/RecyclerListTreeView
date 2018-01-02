# RecyclerListTreeView

`The fastest android Tree View based on RecyclerView in the world!!`

I am special because:
*The data store is looking like a tree but in fact it is a list.
*Not changed any thing on RecyclerView.
*No recursion,only cycle，so no worry about stack overflow.
*User will not feel any different from origin Recycler using way.
*Less half amount of code to other`s implemetion.


基于RecyclerView，自认为是当前最快的 Android tree view 的实现了！！

存储数据的结构并不是Tree，而是一个ArrayList。与所有已知的网上的实现都不一样，大家似乎都跳不出固定思维。
可以比较一下代码量，此实现比其它的少一半都不止。

核心是一个表示Tree的类，但它的本质是一个List。对RecyclerView没有任何改动，对Adapter只有少量封装，
使用者不会产生任何陌生感。也就是说你对RecyclerView能做的，现在依然能做。

以List的形式表式树，带来很多好处：
1 没有了递归。该用递归的地方全部变成了循环（Tree不论有多少层都没有栈溢出）。
2 其次是有序，插入节点时，可以指定它是它爸爸的第几个儿子。
3 极其适合在RecyclerView中使用，
4 跟List无异，无论根节点还是子节点都对应RecyclerView中的一行。
5 不需对RecyclerView做任何改动

有诗为证：
远看像棵树
近看不是树
似树而非树
是为牛逼树


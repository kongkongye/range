## 介绍
适合minecraft，以及未来会出的hytale等有支持插件的游戏。

此库作为范围util，一般适合用来实现`领地`功能，作为核心检测层。

## 为什么选择此库(why)
这是范围前置util,最重要的目的: 高效!!!

因为一般情况下,如果存在上万范围时,检测指定位置的范围列表或获取与指定范围冲突的范围列表,效率就会很低,此前置就是为了解决这个而设计的.

## 如何实现高效检测(how)
1. 缓存,即缓存最近检测过的结果
2. 分维度保存,类似分区算法

主要相关类：

1. DivSpace
2. LevelMultiSpace
3. RangeMultiSpace

## 支持哪些功能(what)
1. 多种维度(dim)
2. 维度空间(space)
3. 范围管理器
   1. 获取包含指定位置的所有范围对象
   2. 获取与指定范围冲突的所有范围对象列表
   3. 自动优化

## 使用方法
1. 新建一个`RangeManager`,在世界加载时调用`addWorld`方法,世界卸载时调用`delWorld`方法
2. 需要增加对象时调用`RangeManager`的`add`方法

## todo
1. 缓冲仍然有一些优化空间
2. 冲突也可以与位置一样进行类似的自动优化,后面再搞吧
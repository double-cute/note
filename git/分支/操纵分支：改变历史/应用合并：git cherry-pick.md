# 应用合并：git cherry-pick
> 和git merge完全一样！！不同时的是没有连线

<br><br>

## 目录
1. [用法和原理]()
2. [cherry-pick的含义]()

<br><br>

### 一、用法和原理：

- 用法和git merge完全一样：git cherry-pick 节点引用
- 效果：假设是git cherry-pick NODE
  - 先做git merge NODE，然后**将NODE和合并后新的HEAD之间的连线去掉**.
  - 也就是说merge和cherry-pick都是合并，只不过前者在git log中可以看到连线，后者没有连线而已.
    - 简单地说**看不出合并的历史记录**.
- 原理：
  1. 先做一个NODE的副本NODE_
  2. 然后git merge NODE_
  3. 最后将临时的NODE_删去.
    - 所以在git log中找不到merge的记录（被删除了）.
  4. 因此git cherry-pick也会发生合并冲突问题，实质是git merge的合并冲突问题，解决方法也和git merge完全一样.
  5. **合并提交的说明默认为NODE的提交说明！！**

<br><br>

### 二、cherry-pick的含义：
> 译为分拣.

- merge强调的是合并前后的关联关系：
  1. NODE和新节点之间有连线（git log）.
  2. 直观上表现为NODE是新节点的父节点，强调没有NODE就没有新节点（爸爸和儿子的关系）.
- cherry-pick只强调应用合并：
  1. 仅仅将NODE和HEAD合并为新节点.
  2. 合并后NODE和新节点没有任何关系，仅仅就是“利用”了一下NODE而已，用完就不管它了.
    - 形象地说就是借种，生出来了以后NODE不对新节点负责（不是它的父结点）.
  3. 因此cherry-pick的含义就是：从版本库中所有已存在的节点中**分拣**出想要的，并**应用**在当前HEAD上，合并出新节点.
    - 由于是应用**分拣点**的提交，因此合并提交的说明也理应和分拣点的提交说明一样，所以：
      1. git cherry-pick没有-m选项，如果能自动合并，则默认提交说明和NODE一样.
      2. 即使需要手动解决冲突，那最后提交合并的时候也应该让提交说明和NODE的提交说明一样：
        - 如果你忘记掉了NODE的提交说明也无需查看后再写到-m中.
        - 直接git commit不加-m选项进入vi后会看到已经帮你自动写好了NODE的提交说明，保存退出即可.
          - git cherry-pick非常智能，会自动帮你准备好合并提交的提交说明.

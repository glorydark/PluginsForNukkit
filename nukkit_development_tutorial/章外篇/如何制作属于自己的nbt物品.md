# 如何制作属于自己的nbt物品？

发布: 2022年7月31日  更新: 2022年7月31日

相信很多开发者都看见过RPG物品的使用，比如我们经常使用的RsWeapon、SuperWeapon、GRPG等插件。在这节，我将会大致说明一下nbt物品的部分基础功能实现。

## 如果要给一个物品插入tag怎么办呢？

```java
Item nbtitem = new Item(269,0,1);
CompoundTag tag = new CompoundTag();tag.putBoolean("Unbreakable", true));
nbtitem.setCompoundTag(tag);
```

如果你看到了“Unbreakable”的字眼，那么你应该能猜到，这部分代码是实现 **物品无限耐久** 的方式。而这个例子，也是很好地向你展示了如何插入一个tag。

比如小游戏加入的时候，给予玩家“退出房间”、“选择队伍”的物品也一般需要应用到此方法，从而通过插入的tag来判断使用时的物品是否为“退出房间”等的特殊物品。（此处指路name的GunWar：[GunWar](https://github.com/MemoriesOfTime/GunWar)，有兴趣可以去了解一下）

## 如果我插入了tag，那么我将如何获取呢？

```java
@EventHandler
public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
    if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
         Item item =((Player) event.getDamager()).getInventory().getItemInHand();
         //先获取攻击者手上的物品
         if(!item.hasCompoundTag()){return;}
         //如果没有CompoundTag。返回
         Compoundtag tag = item.getNamedTag();
         if(tag.contains("damage")){
               //如果有名为damage的标签。标签需要提前插入
               //举例: tag.putString("damage", "2.0");
               event.setDamage(Float.parseFloat(tag.getString("damage"));
               //获取damage并应用于伤害事件
         }
    }
}
```

如代码所示，这个是RPG物品造成自定义伤害的局部代码。在玩家攻击玩家的时候，本身玩家也属于Entity，会触发EntityDamageByEntityEvent。也就在这个事件中，我们可以更改玩家造成的伤害以及造成的击退效果。这个例子向我们讲述了如何让RPG物品攻击带有自定义伤害。

## 我该如何更新武器数据呢？有没有现成的RPG物品例子供学习呢？

此处可以借鉴RsWeapon，RsWeapon对物品配置的更新应用部分做的是非常的典型、优秀。如果你已经看懂了上面的内容，快快点击下面的链接文本，去探索其中的奥秘吧！

[RsWeapon](https://github.com/SmallasWater/RsWeapon)

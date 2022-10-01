# Nukkit中如何使用物品组件

发布: 2022年6月24日  更新: 2022年6月24日

相信很多开发者估计都很疑惑bds中的一些物品组件，比如死亡不掉落、锁定物品等等。其实，在nk中，我们可以通过修改物品CompoundTag可以实现这些。比如我刚刚发的一个名为FastenItem的插件，也是对物品组件的运用。

## 参考资料及工具

player.dat格式：[player.dat格式 - Minecraft Wiki，最详细的我的世界百科](https://minecraft.fandom.com/zh/wiki/Player.dat%E6%A0%BC%E5%BC%8F#%E5%8E%86%E5%8F%B2)

物品组件介绍：[物品组件 - Minecraft Wiki，最详细的我的世界百科](https://minecraft.fandom.com/zh/wiki/%E7%89%A9%E5%93%81%E7%BB%84%E4%BB%B6#lock_in_inventory)

MCC Tool Chest PE: https://kirimasharo.com/d/MCCToolChestPE_Setup.zip

在nk中，实现物品组件的简单用法其实和设置物品无限耐久的方法相同（Unbreakable）。发现的过程需要我们用心去搜集&查阅资料、大胆尝试，从而找到突破点，直击要害。

## 如何实现

```java
CompoundTag tag;
if(item.hasCompoundTag()){
   tag = item.getNamedTag();
}else{
   tag = new CompoundTag();
}
//物品红色锁 lock_in_inventory
tag.putByte("minecraft:item_lock", 1);

//物品黄色锁 lock_in_slot
tag.putByte("minecraft:item_lock", 2);

//物品死亡不掉落 keep_on_death
tag.putByte("minecraft:keep_on_death", 1);

//物品可破坏方块 can_destroy（详情见wiki的通用标签）
ListTag listTag = new ListTag("CanDestroy");
StringTag stringtag = new StringTag("CanDestroy");
stringtag.data = "minecraft:grass";
listTag.add(stringtag);
tag.putList(listTag);

//可放置在XX方块上 can_place_on（详情见wiki的方块标签）
ListTag listTag = new ListTag("CanPlaceOn");
StringTag stringtag = new StringTag("CanPlaceOn");
stringtag.data = "minecraft:grass";
listTag.add(stringtag);
tag.putList(listTag);
```

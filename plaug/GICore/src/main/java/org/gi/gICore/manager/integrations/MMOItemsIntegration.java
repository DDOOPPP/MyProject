package org.gi.gICore.manager.integrations;

import io.lumine.mythic.bukkit.utils.nbt.NBT;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.itemtype.MMOItemType;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.manager.TypeManager;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.N;
import org.gi.gICore.GICore;

import java.util.Optional;

public class MMOItemsIntegration implements PluginIntegration{

    @Override
    public String getPluginName() {
        return "MMOItems";
    }

    @Override
    public boolean isEnabled() {
        return GICore.getInstance().getServer().getPluginManager().isPluginEnabled("MMOItems");
    }

    @Override
    public void disable() {

    }

    public Optional<MMOItem> getMMOItem(ItemStack item) {
        if (item == null) return Optional.empty();
        String id = MMOItems.getID(item);
        if (id == null) return Optional.empty();
        Type type = MMOItems.getType(item);
        if (type == null) return Optional.empty();

        MMOItem mmoItem = MMOItems.plugin.getMMOItem(type,id);
        return Optional.ofNullable(mmoItem);
    }

    public Optional<ItemStack> getItem(String id, String type) {
        try{
            TypeManager manager = new TypeManager();
            Type mmoType = manager.get(type.toUpperCase());
            if (mmoType == null) return Optional.empty();

            MMOItem mmoItem = MMOItems.plugin.getMMOItem(mmoType,id);
            if (mmoItem == null) return Optional.empty();

            return Optional.ofNullable(mmoItem.newBuilder().build());
        }catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean isMMOItem(ItemStack item) {
        return getMMOItem(item).isPresent();
    }
}

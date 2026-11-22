private void dropAll(Player p, ItemStack stack) {
        p.getWorld().dropItem(p.getEyeLocation(), stack, it -> {
            it.setVelocity(p.getLocation().getDirection().multiply(0.3));
            it.setPickupDelay(40);
            it.setThrower(p.getUniqueId());
        });
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        int slot = p.getInventory().getHeldItemSlot();
        if (holdReached(p, slot)) {
            Bukkit.getScheduler().runTask(this, () -> {
                ItemStack rest = p.getInventory().getItem(slot);
                if (rest == null || rest.getType().isAir()) return;
                p.getInventory().setItem(slot, null);
                dropAll(p, rest);
            });
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getClick() == ClickType.DROP) {
            ItemStack clicked = e.getCurrentItem();
            if (clicked != null && !clicked.getType().isAir() && holdReached(p, 1000 + e.getRawSlot())) {
                e.setCancelled(true);
                e.setCurrentItem(null);
                dropAll(p, clicked);
            }
        }
        Bukkit.getScheduler().runTask(this, () -> fixPlayer(p));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTask(this, () -> fixPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        tracks.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        fixInv(e.getInventory());
    }

    @EventHandler
    public void onSpawn(ItemSpawnEvent e) {
        ItemStack s = e.getEntity().getItemStack();
        if (fix(s)) e.getEntity().setItemStack(s);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        ItemStack s = e.getItem().getItemStack();
        if (fix(s)) e.getItem().setItemStack(s);
    }
}

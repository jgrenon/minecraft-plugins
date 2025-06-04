package avx.money;

import org.bukkit.plugin.java.JavaPlugin;

import avx.money.utils.BalanceManager;
import avx.money.commands.*;

public class MoneyPlugin extends JavaPlugin {

    public static MoneyPlugin instance;
    public BalanceManager balanceManager;

    @Override
    public void onEnable() {
        instance = this;
        balanceManager = new BalanceManager(this);

        getCommand("money").setExecutor(new MoneyCommand());
        getCommand("pay").setExecutor(new PayCommand());
        getCommand("cash").setExecutor(new CashCommand());
        getCommand("money").setTabCompleter(new MoneyCommand()); // pour auto-complétion
        getLogger().info("MoneyPlugin activé !");
    }

    @Override
    public void onDisable() {
        balanceManager.save();
        getLogger().info("MoneyPlugin désactivé !");
    }

    public static MoneyPlugin getInstance() {
        return instance;
    }

}
package io.ucoin.app.config;


import java.io.File;

import io.ucoin.app.model.Identity;
import io.ucoin.app.model.Wallet;
import io.ucoin.app.technical.crypto.CryptoUtils;
import io.ucoin.app.technical.crypto.TestFixtures;

/**
 * Access to configuration options
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 */
public class Configuration  {
    /** Logger. */
    private static final String TAG = "Configuration";

    private static Configuration instance;

    public static Configuration instance() {
        return instance;
    }

    public static void setInstance(Configuration instance) {
        Configuration.instance = instance;
    }

    protected File configFile;

    public Configuration() {
        super();
    }

    public String getVersion() {
        return ConfigurationOption.VERSION.getDefaultValue();
    }

    public String getNodeHost() {
        return ConfigurationOption.NODE_HOST.getDefaultValue();
    }

    public int getNodePort() {
        return Integer.parseInt(ConfigurationOption.NODE_PORT.getDefaultValue());
    }

    public int getNodeTimeout() {
        return Integer.parseInt(ConfigurationOption.NODE_TIMEOUT.getDefaultValue());
    }

    public String getForumUrl() {
        return ConfigurationOption.FORUM_URL.getDefaultValue();
    }

    private Wallet currentWallet;

    public Wallet getCurrentWallet() {
        if (currentWallet != null) {
            return currentWallet;
        }

        // TODO : replace from a database ?
        TestFixtures fixtures = new TestFixtures();

        Identity identity = new Identity();
        identity.setUid(fixtures.getUid());
        identity.setPubkey(fixtures.getUserPublicKey());
        identity.setTimestamp(fixtures.getSelfTimestamp());
        identity.setSignature(fixtures.getSelfSignature());
        currentWallet = new Wallet(
                fixtures.getCurrency(),
                CryptoUtils.decodeBase58(fixtures.getUserPrivateKey()),
                identity);

        return currentWallet;
    }

    public void setCurrentWallet(Wallet currentWallet) {
        this.currentWallet = currentWallet;
    }
    
}

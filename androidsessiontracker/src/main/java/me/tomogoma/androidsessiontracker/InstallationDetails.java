package me.tomogoma.androidsessiontracker;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.UUID;

import me.tomogoma.androidcache.Cache;
import me.tomogoma.androidcache.Storable;
import me.tomogoma.androidcache.leveldb.LevelDBCache;

/**
 * Created by tomogoma on 30/08/16.
 */
public class InstallationDetails implements Storable {

    private static final String LOG_TAG = InstallationDetails.class.getName();
    private static final String INSTALLATION_DETAILS_KEY = "logconduit.app_installation_details";
    private static final String INSTALLATION_DETAILS_FILE = "logconduit.app_installation_details";

    public static String getInstallationUUID(Context c) {
        Cache cache = new LevelDBCache(c, INSTALLATION_DETAILS_FILE);
        String idStr;
        try {
            idStr = cache.get(INSTALLATION_DETAILS_KEY);
        } catch (IOException | Cache.InvalidKeyException e) {
            Log.e(LOG_TAG, "Failed to get installation details");
            throw new RuntimeException(e);
        }
        InstallationDetails id = new Gson().fromJson(idStr, InstallationDetails.class);
        boolean updateStore = false;
        if (id == null) {
            String uuid = UUID.randomUUID().toString();
            id = new InstallationDetails(uuid);
            updateStore = true;
        }
        if (updateStore) {
            try {
                cache.insert(id);
            } catch (IOException | Cache.InvalidKeyException e) {
                Log.e(LOG_TAG, "Failed to get installation details");
                throw new RuntimeException(e);
            }
        }
        return id.installationID;
    }

    private transient String key;
    private String installationID;

    public InstallationDetails(String installationID) {
        if (installationID == null || installationID.isEmpty()) {
            throw new IllegalArgumentException("installation UUID was null or empty");
        }
        this.key = INSTALLATION_DETAILS_KEY;
        this.installationID = installationID;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return toJson();
    }

    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }
}

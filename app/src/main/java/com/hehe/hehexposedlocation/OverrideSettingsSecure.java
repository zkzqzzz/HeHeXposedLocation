package com.hehe.hehexposedlocation;

import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;

import com.hehe.hehexposedlocation.BuildConfig;

import java.util.HashSet;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class OverrideSettingsSecure implements IXposedHookLoadPackage {
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        final XSharedPreferences sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_PREFERENCES_FILE);
        sharedPreferences.makeWorldReadable();

        if (sharedPreferences.getBoolean(Common.PREF_KEY_WHITELIST_ALL, true) ||
                sharedPreferences.getStringSet(Common.PREF_KEY_WHITELIST_APP_LIST, new HashSet<String>(0)).contains(lpparam.packageName)) {

            findAndHookMethod("android.provider.Settings.Secure", lpparam.classLoader, "getString",
                    ContentResolver.class, String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            String requested = (String) param.args[1];
                            if (requested.equals(Settings.Secure.ALLOW_MOCK_LOCATION)) {
                                param.setResult("0");
                            }
                        }
                    });
            if (Build.VERSION.SDK_INT >= 17) {
                findAndHookMethod("android.provider.Settings.Secure", lpparam.classLoader, "getStringForUser",
                        ContentResolver.class, String.class, Integer.TYPE, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                String requested = (String) param.args[1];
                                if (requested.equals(Settings.Secure.ALLOW_MOCK_LOCATION)) {
                                    param.setResult("0");
                                }
                            }
                        });
            }
            //https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/location/java/android/location/Location.java
            //https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/location/java/android/location/LocationManager.java
            // at API level 18, the function Location.isFromMockProvider is added
            if (Build.VERSION.SDK_INT >= 18) {
                final int haha = (R.id.txtNoise);
                final float hoho = (mod (haha, 2))/((float)2.11111);
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "isFromMockProvider",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                //param.setResult(false);
                                param.setResult(true);
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getLatitude",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.setResult((double)haha);
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getLongitude",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.setResult((double)haha);
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getAccuracy",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.setResult((float)hoho);
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "hasAltitude",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.setResult(false);
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "hasAccuracy",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.setResult(false);
                            }
                        });
                        //For location listener
                findAndHookMethod(Common.SYSTEM_LOCATION_LISTENER, lpparam.classLoader, "onLocationChanged",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                double latitude = haha;
                                double longitude = haha;
                                float accuracy = (float)hoho;
                            }
                        });
            }
        }
    }
    private int mod(int x, int y)
    {
        int result = x % y;
        if (result < 0)
            result += y;
        return result;
    }
}
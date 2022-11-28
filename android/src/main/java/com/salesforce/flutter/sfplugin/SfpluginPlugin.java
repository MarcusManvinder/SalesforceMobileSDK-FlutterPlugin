/*
 Copyright (c) 2018-present, salesforce.com, inc. All rights reserved.

 Redistribution and use of this software in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 conditions and the following disclaimer in the documentation and/or other materials provided
 with the distribution.
 * Neither the name of salesforce.com, inc. nor the names of its contributors may be used to
 endorse or promote products derived from this software without specific prior written
 permission of salesforce.com, inc.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.flutter.sfplugin;

import androidx.annotation.NonNull;

import com.salesforce.flutter.sfplugin.bridge.SalesforceFlutterBridge;
import com.salesforce.flutter.sfplugin.bridge.SalesforceNetFlutterBridge;
import com.salesforce.flutter.sfplugin.bridge.SalesforceOauthFlutterBridge;
import com.salesforce.flutter.sfplugin.bridge.SmartStoreFlutterBridge;
import com.salesforce.flutter.sfplugin.bridge.SmartSyncFlutterBridge;
import com.salesforce.flutter.sfplugin.ui.SalesforceFlutterActivity;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * Salesforce flutter plugin
 */
public class SfpluginPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {

    private SalesforceOauthFlutterBridge oauthBridge;
    private SalesforceNetFlutterBridge networkBridge;
    private SmartStoreFlutterBridge smartStoreFlutterBridge;
    private SmartSyncFlutterBridge smartSyncFlutterBridge;

    private SalesforceFlutterActivity _activity;

    /**
     * Plugin registration.
     */
    public static void registerWith(PluginRegistry.Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "sfplugin");
        channel.setMethodCallHandler(new SfpluginPlugin((SalesforceFlutterActivity) registrar.activity()));
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
       final MethodChannel channel = new MethodChannel(binding.getBinaryMessenger(), "sfplugin");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {

    }

    public SfpluginPlugin() {}

    private SfpluginPlugin(SalesforceFlutterActivity currentActivity) {
        initSFBridge(currentActivity);
    }

    private initSFBridge(SalesforceFlutterActivity currentActivity) {
        this.networkBridge = new SalesforceNetFlutterBridge(currentActivity);
        this.oauthBridge = new SalesforceOauthFlutterBridge(currentActivity);
        this.smartStoreFlutterBridge = new SmartStoreFlutterBridge(currentActivity);
        this.smartSyncFlutterBridge = new SmartSyncFlutterBridge(currentActivity);
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (_activity != null) {
            String prefix = call.method.substring(0, call.method.indexOf("#"));

            for (SalesforceFlutterBridge bridge : new SalesforceFlutterBridge[] { oauthBridge, networkBridge, smartStoreFlutterBridge, smartSyncFlutterBridge}) {
                if (call.method.startsWith(bridge.getPrefix() + "#")) {
                    bridge.onMethodCall(call, result);
                    return;
                }
            }
            result.notImplemented();
        }
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        _activity = (SalesforceFlutterActivity) binding.getActivity();

        initSFBridge(_activity);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        _activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        _activity = (SalesforceFlutterActivity) binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        _activity = null;
    }
}

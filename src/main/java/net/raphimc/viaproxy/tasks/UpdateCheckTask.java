/*
 * This file is part of ViaProxy - https://github.com/RaphiMC/ViaProxy
 * Copyright (C) 2021-2026 RK_01/RaphiMC and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.raphimc.viaproxy.tasks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vdurmont.semver4j.Semver;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.ui.I18n;
import net.raphimc.viaproxy.ui.ViaProxyWindow;
import net.raphimc.viaproxy.ui.popups.DownloadPopup;
import net.raphimc.viaproxy.util.JarUtil;
import net.raphimc.viaproxy.util.logging.Logger;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static net.raphimc.viaproxy.ViaProxy.VERSION;

public class UpdateCheckTask implements Runnable {

    private final boolean hasUI;

    public UpdateCheckTask(final boolean hasUI) {
        this.hasUI = hasUI;
    }

    @Override
    @SuppressWarnings("UnreachableCode")
    public void run() {
	    return;
    }

    private void showUpdateWarning(final String latestVersion) {
        JOptionPane.showMessageDialog(ViaProxy.getForegroundWindow(), I18n.get("popup.update.info", VERSION, latestVersion), "ViaProxy", JOptionPane.WARNING_MESSAGE);
    }

    private void showUpdateQuestion(final String name, final String downloadUrl, final String latestVersion) {
        int chosen = JOptionPane.showConfirmDialog(ViaProxy.getForegroundWindow(), I18n.get("popup.update.info", VERSION, latestVersion) + "\n\n" + I18n.get("popup.update.question"), "ViaProxy", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (chosen == JOptionPane.YES_OPTION) {
            final File f = new File(JarUtil.getJarFile().map(File::getParentFile).orElseThrow(), name);
            new DownloadPopup(ViaProxy.getForegroundWindow(), downloadUrl, f, () -> SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(ViaProxy.getForegroundWindow(), I18n.get("popup.update.success"), "ViaProxy", JOptionPane.INFORMATION_MESSAGE);
                try {
                    JarUtil.launch(f);
                    System.exit(0);
                } catch (Throwable e) {
                    Logger.LOGGER.error("Could not start the new ViaProxy jar", e);
                    ViaProxyWindow.showException(e);
                }
            }), t -> {
                if (t != null) {
                    Logger.LOGGER.error("Could not download the latest version of ViaProxy", t);
                    ViaProxyWindow.showException(t);
                }
            });
        }
    }

    private boolean isMainViaProxyJar(final JsonObject root, final JsonObject assetObject) {
        return assetObject.get("name").getAsString().equals(root.get("name").getAsString() + ".jar");
    }

    private boolean isJava8ViaProxyJar(final JsonObject root, final JsonObject assetObject) {
        return assetObject.get("name").getAsString().equals(root.get("name").getAsString() + "+java8.jar");
    }

}

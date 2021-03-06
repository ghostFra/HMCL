/*
 * Hello Minecraft! Launcher.
 * Copyright (C) 2018  huangyuhui <huanghongxun2008@126.com>
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
 * along with this program.  If not, see {http://www.gnu.org/licenses/}.
 */
package org.jackhuang.hmcl.ui.versions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXTabPane;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import org.jackhuang.hmcl.download.game.GameAssetIndexDownloadTask;
import org.jackhuang.hmcl.setting.Profile;
import org.jackhuang.hmcl.ui.FXUtils;
import org.jackhuang.hmcl.ui.decorator.DecoratorPage;
import org.jackhuang.hmcl.util.FileUtils;

import java.io.File;

import static org.jackhuang.hmcl.util.i18n.I18n.i18n;

public final class VersionPage extends StackPane implements DecoratorPage {
    private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper(this, "title", null);

    @FXML
    private VersionSettingsPage versionSettings;
    @FXML
    private Tab modTab;
    @FXML
    private ModListPage mod;
    @FXML
    private InstallerListPage installer;
    @FXML
    private WorldListPage world;
    @FXML
    private JFXListView<?> browseList;
    @FXML
    private JFXListView<?> managementList;
    @FXML
    private JFXButton btnBrowseMenu;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXButton btnManagementMenu;
    @FXML
    private JFXButton btnExport;
    @FXML
    private StackPane rootPane;
    @FXML
    private StackPane contentPane;
    @FXML
    private JFXTabPane tabPane;

    private final JFXPopup browsePopup;
    private final JFXPopup managementPopup;

    private Profile profile;
    private String version;

    {
        FXUtils.loadFXML(this, "/assets/fxml/version/version.fxml");

        getChildren().removeAll(browseList, managementList);

        browsePopup = new JFXPopup(browseList);
        managementPopup = new JFXPopup(managementList);

        FXUtils.installTooltip(btnDelete, i18n("version.manage.remove"));
        FXUtils.installTooltip(btnBrowseMenu, i18n("settings.game.exploration"));
        FXUtils.installTooltip(btnManagementMenu, i18n("settings.game.management"));
        FXUtils.installTooltip(btnExport, i18n("modpack.export"));
    }

    public void load(String id, Profile profile) {
        this.version = id;
        this.profile = profile;

        title.set(i18n("version.manage.manage") + " - " + id);

        versionSettings.loadVersionSetting(profile, id);
        mod.setParentTab(tabPane);
        modTab.setUserData(mod);
        mod.loadMods(profile.getModManager(), id);
        installer.loadVersion(profile, id);
        world.loadVersion(profile, id);
    }

    @FXML
    private void onBrowseMenu() {
        browseList.getSelectionModel().select(-1);
        browsePopup.show(btnBrowseMenu, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, -12, 15);
    }

    @FXML
    private void onManagementMenu() {
        managementList.getSelectionModel().select(-1);
        managementPopup.show(btnManagementMenu, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, -12, 15);
    }

    @FXML
    private void onBrowse() {
        String sub;
        switch (browseList.getSelectionModel().getSelectedIndex()) {
            case 0:
                sub = "";
                break;
            case 1:
                sub = "mods";
                break;
            case 2:
                sub = "coremods";
                break;
            case 3:
                sub = "config";
                break;
            case 4:
                sub = "resourcepacks";
                break;
            case 5:
                sub = "screenshots";
                break;
            case 6:
                sub = "saves";
                break;
            default:
                return;
        }
        FXUtils.openFolder(new File(profile.getRepository().getRunDirectory(version), sub));
    }

    @FXML
    private void onManagement() {
        switch (managementList.getSelectionModel().getSelectedIndex()) {
            case 0: // rename a version
                Versions.renameVersion(profile, version);
                break;
            case 1: // remove a version
                Versions.deleteVersion(profile, version);
                break;
            case 2: // redownload asset index
                new GameAssetIndexDownloadTask(profile.getDependency(), profile.getRepository().getResolvedVersion(version)).start();
                break;
            case 3: // delete libraries
                FileUtils.deleteDirectoryQuietly(new File(profile.getRepository().getBaseDirectory(), "libraries"));
                break;
            case 4:
                throw new Error();
        }
    }

    public String getTitle() {
        return title.get();
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return title.getReadOnlyProperty();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }
}

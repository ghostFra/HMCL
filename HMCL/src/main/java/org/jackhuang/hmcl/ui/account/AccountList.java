/*
 * Hello Minecraft! Launcher.
 * Copyright (C) 2017  huangyuhui <huanghongxun2008@126.com>
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
package org.jackhuang.hmcl.ui.account;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ToggleGroup;
import org.jackhuang.hmcl.auth.Account;
import org.jackhuang.hmcl.ui.Controllers;
import org.jackhuang.hmcl.ui.ListPage;
import org.jackhuang.hmcl.ui.decorator.DecoratorPage;
import org.jackhuang.hmcl.util.MappedObservableList;

import static org.jackhuang.hmcl.ui.FXUtils.onInvalidating;
import static org.jackhuang.hmcl.util.i18n.I18n.i18n;

public class AccountList extends ListPage<AccountListItem> implements DecoratorPage {
    private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper(i18n("account.manage"));
    private ObjectProperty<Account> selectedAccount = new SimpleObjectProperty<Account>() {
        {
            itemsProperty().addListener(onInvalidating(this::invalidated));
        }

        @Override
        protected void invalidated() {
            Account selected = get();
            itemsProperty().forEach(item -> item.selectedProperty().set(item.getAccount() == selected));
        }
    };
    private final ListProperty<Account> accounts = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ToggleGroup toggleGroup;
    private final ObservableList<AccountListItem> accountItems;

    public AccountList() {
        toggleGroup = new ToggleGroup();

        accountItems = MappedObservableList.create(
                accountsProperty(),
                account -> new AccountListItem(toggleGroup, account));

        itemsProperty().bindContent(accountItems);

        toggleGroup.selectedToggleProperty().addListener((o, a, toggle) -> {
            if (toggle == null || toggle.getUserData() == null) return;
            selectedAccount.set(((AccountListItem) toggle.getUserData()).getAccount());
        });
    }

    public ObjectProperty<Account> selectedAccountProperty() {
        return selectedAccount;
    }

    public ListProperty<Account> accountsProperty() {
        return accounts;
    }

    @Override
    public void add() {
        Controllers.dialog(new AddAccountPane());
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return title.getReadOnlyProperty();
    }
}

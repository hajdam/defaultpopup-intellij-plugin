/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.utils.guipopup;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Module installer.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultPopupInstaller implements StartupActivity.DumbAware, AppLifecycleListener { /* ApplicationInitializedListener */

    private boolean installed = false;

    @Override
    public void runActivity(Project project) {
        install();
    }

    private void install() {
        if (!installed) {
            IntelliJDefaultPopupMenu.register();
            installed = true;
        }
    }

    @Override
    public void appStarted() {
        install();
    }

//    public void componentsInitialized() {
//        install();
//    }
}

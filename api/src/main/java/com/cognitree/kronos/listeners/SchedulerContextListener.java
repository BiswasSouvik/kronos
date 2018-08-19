/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cognitree.kronos.listeners;

import com.cognitree.kronos.scheduler.SchedulerApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SchedulerContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerContextListener.class);
    private SchedulerApp schedulerApp;

    public SchedulerContextListener() {
        schedulerApp = new SchedulerApp();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            schedulerApp.start();
        } catch (Exception e) {
            logger.error("Error starting scheduler", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        schedulerApp.stop();
    }
}

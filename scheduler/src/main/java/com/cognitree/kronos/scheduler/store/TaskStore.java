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

package com.cognitree.kronos.scheduler.store;

import com.cognitree.kronos.model.Task;
import com.cognitree.kronos.model.Task.Status;
import com.cognitree.kronos.model.TaskId;

import java.util.List;
import java.util.Map;

/**
 * An interface exposing API's to provide {@link Task} persistence.
 */
public interface TaskStore extends Store<Task, TaskId> {

    List<Task> load(String namespace) throws StoreException;

    List<Task> loadByJobIdAndWorkflowName(String namespace, String jobId, String workflowName) throws StoreException;

    List<Task> loadByStatus(String namespace, List<Status> statuses) throws StoreException;

    Map<Status, Integer> countByStatus(String namespace, long createdAfter, long createdBefore) throws StoreException;

    Map<Status, Integer> countByStatusForWorkflowName(String namespace, String workflowName,
                                                      long createdAfter, long createdBefore) throws StoreException;
}
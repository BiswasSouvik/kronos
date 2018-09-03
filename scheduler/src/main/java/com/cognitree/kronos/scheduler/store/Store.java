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

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Store<E, T> {

    /**
     * called during initialization phase to initialize the store.
     *
     * @param storeConfig configuration used to initialize the store.
     * @throws StoreException
     */
    void init(ObjectNode storeConfig) throws Exception;

    void store(E entity) throws StoreException;

    E load(T identity) throws StoreException;

    void update(E entity) throws StoreException;

    void delete(T identity) throws StoreException;

    void stop();
}

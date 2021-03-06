/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.camel.filesplit;

import org.apache.camel.component.file.GenericFile;

public class FileBlock {
    private final GenericFile<?> file;
    private final long size;
    private final int index;

    public FileBlock(GenericFile<?> file, long size, int index) {
        this.file = file;
        this.size = size;
        this.index = index;
    }

    public GenericFile<?> getFile() {
        return file;
    }

    public long getChunk() {
        return size;
    }

    public int getIndex() {
        return index;
    }
}

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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChunkInputStream extends BufferedInputStream {
    private static final Logger LOG = LoggerFactory.getLogger(ChunkInputStream.class);
	private final long chunk;
	private final int index;
	private int bytesRead = 0;
	private boolean advance = true;

	public ChunkInputStream(InputStream in, long chunk, int index) {
		super(in);
		this.chunk = chunk;
		this.index = index;
	}

	public ChunkInputStream(InputStream in, int size, long chunk, int index) {
		super(in, size);
		this.chunk = chunk;
		this.index = index;
	}

	@Override
	public synchronized int read() throws IOException {
		step();
		int result = bytesRead < chunk ? super.read() : -1;
	    bytesRead += result == -1 ? 0 : 1; 
		return bytesRead < chunk ? result : -1;
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		step();
		int result = bytesRead < chunk ? super.read(b, off, len) : -1;
	    bytesRead += result == -1 ? 0 : result;
		return bytesRead > chunk ? (int)(chunk + result - bytesRead) : result;
	}

	@Override
	public int read(byte[] b) throws IOException {
		step();
		int result = bytesRead < chunk ? super.read(b) : -1;
	    bytesRead += result == -1 ? 0 : result;
		return bytesRead > chunk ? (int)(chunk + result - bytesRead) : result;
	}

	private void step() throws IOException {
		if (advance) {
			this.skip(chunk * index);
			advance = false;
		}
	}
}

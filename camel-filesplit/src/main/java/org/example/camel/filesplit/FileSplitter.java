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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileConverter;

@Converter
public final class FileSplitter {

	public static Expression chunks(final long chunkSize) {
		return new Expression()  {
			@Override
			@SuppressWarnings("unchecked")
			public <T> T evaluate(Exchange exchange, Class<T> type) {
				if (type.isAssignableFrom(List.class) && exchange.getIn().getBody() instanceof GenericFile) {
					GenericFile<?> input = (GenericFile<?>)exchange.getIn().getBody();
					long size = input.getFileLength();
					int count = (int)((size + chunkSize - 1) / chunkSize);
					List<InputChunk> answer = new ArrayList<InputChunk>(count);
					for (int i = 0; i < count; i++) {
						answer.add(new InputChunk(input, chunkSize, i));
					}
				    return (T)answer;
				}
				return null;
		    }
		};
	}
	
	@Converter
	public static InputStream toInputStream(InputChunk chunk, Exchange exchange) throws IOException {
        return (chunk.getFile().getFile() instanceof java.io.File)
            ? new ChunkInputStream(new FileInputStream((File)chunk.getFile().getFile()), chunk.getChunk(), chunk.getIndex())
            : GenericFileConverter.genericFileToInputStream(chunk.getFile(), exchange);
	}
}

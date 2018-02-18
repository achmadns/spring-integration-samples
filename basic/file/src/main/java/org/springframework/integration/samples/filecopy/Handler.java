/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.samples.filecopy;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * A class providing several handling methods for different types of payloads.
 *
 * @author Mark Fisher
 * @author Marius Bogoevici
 */
public class Handler {
    private final ConnectionFactory connectionFactory = new ConnectionFactory();
    private final Connection connection = connectionFactory.newConnection();
    private final Channel channel = connection.createChannel();
    private final Logger log = Logger.getLogger(Handler.class);

    public Handler() throws IOException, TimeoutException {
    }

    public String handleString(String input) {
        System.out.println("Copying text: " + input);
        return input.toUpperCase();
    }

    public File handleFile(File input) {
        log.info("Copying file: " + input.getAbsolutePath());
        return input;
    }

    public void handle(File input) throws IOException {
        final String path = input.getAbsolutePath();
        channel.basicPublish("", "oss.download", null , path.getBytes());
        log.info("Published message: " + path);
    }

    public byte[] handleBytes(byte[] input) {
        System.out.println("Copying " + input.length + " bytes ...");
        return new String(input).toUpperCase().getBytes();
    }

}

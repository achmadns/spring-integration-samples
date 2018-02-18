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

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.CountDownLatch;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Demonstrating the file copy scenario using file-based source and target.
 * See the 'fileCopyDemo-file.xml' configuration file for details.
 *
 * @author Marius Bogoevici
 */
public class FileBasedFileCopyTest {
    private final Logger log = Logger.getLogger(FileBasedFileCopyTest.class);

    @Test
    public void testFileBasedCopy() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/spring/integration/fileCopyDemo-file.xml", FileBasedFileCopyTest.class);
        FileCopyDemoCommon.displayDirectories(context);
        new CountDownLatch(1).await();
    }

    @Test
    public void watch_folder() throws IOException {
        final WatchService watcher = FileSystems.getDefault().newWatchService();
        final WatchKey key = Paths.get("/tmp/spring-integration-samples/output/").register(watcher,
                new WatchEvent.Kind[]{ENTRY_CREATE});
        log.info("Ready");
        while (true) {
            key.pollEvents().forEach(event -> {
                final WatchEvent.Kind<?> kind = event.kind();
                if (OVERFLOW != kind) {
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    final Path path = ev.context();
                    log.info("Got " + path.toString());
                }
            });
            final boolean valid = key.reset();
            if (!valid) break;
//            log.info("Checked");
        }
        log.info("Finished");
    }
}

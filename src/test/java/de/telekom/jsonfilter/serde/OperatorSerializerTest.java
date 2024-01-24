// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package de.telekom.jsonfilter.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.telekom.jsonfilter.operator.Operator;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OperatorSerializerTest {

    static ObjectMapper om;

    @BeforeAll
    static void initTest() {
        SimpleModule m = new SimpleModule();
        m.addDeserializer(Operator.class, new OperatorDeserializer());
        m.addSerializer(Operator.class, new OperatorSerializer());

        om = new ObjectMapper(new YAMLFactory());
        om.registerModule(m);
    }

    @Test
    void shouldSerialize() throws IOException, URISyntaxException {
        var operator = om.readValue(new File(getClass().getClassLoader().getResource("serdeTest.yaml").toURI()), Operator.class);

        var output = Paths.get("src/test/resources/output/serdeTest_OUTPUT.yaml").toFile();
        output.getParentFile().mkdirs();
        output.createNewFile();

        om.writeValue(output, operator);

        Reader originReader = new FileReader(new File(getClass().getClassLoader().getResource("serdeTest.yaml").toURI()));
        Reader resultReader = new FileReader(Paths.get("src/test/resources/output/serdeTest_OUTPUT.yaml").toFile());

        assertTrue(IOUtils.contentEqualsIgnoreEOL(originReader, resultReader));
    }
}
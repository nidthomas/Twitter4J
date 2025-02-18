/*
 * Copyright (C) 2007 Yusuke Yamamoto
 * Copyright (C) 2011 Twitter, Inc.
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

package twitter4j.conf;

import org.junit.jupiter.api.Test;
import twitter4j.auth.RequestToken;

import java.io.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class ConfigurationTest {


    @Test
    void testGetInstance() throws Exception {
        Configuration conf = ConfigurationContext.getInstance();
        assertNotNull(conf);
    }

    @Test
    void testFixURL() throws Exception {
        assertEquals("http://www.bea.com", ConfigurationBase.fixURL(false, "http://www.bea.com"));
        assertEquals("http://www.bea.com", ConfigurationBase.fixURL(false, "https://www.bea.com"));
        assertEquals("https://www.bea.com", ConfigurationBase.fixURL(true, "http://www.bea.com"));
        assertEquals("https://www.bea.com", ConfigurationBase.fixURL(true, "https://www.bea.com"));
        assertNull(ConfigurationBase.fixURL(false, null));
        assertNull(ConfigurationBase.fixURL(true, null));
    }

    @Test
    void testConfiguration() throws Exception {
        ConfigurationBase conf = new PropertyConfiguration();

        String test = "t4j";
        String override = "system property";


        System.getProperties().remove("twitter4j.user");
        conf = new PropertyConfiguration();
        assertNull(conf.getUser());

        conf.setUser(test);
        assertEquals(test, conf.getUser());
        System.setProperty("twitter4j.user", override);
        conf = new PropertyConfiguration();
        assertEquals(override, conf.getUser());
        conf.setUser(test);
        assertEquals(test, conf.getUser());
        System.getProperties().remove("twitter4j.user");

        System.getProperties().remove("twitter4j.password");
        conf = new PropertyConfiguration();
        assertNull(conf.getPassword());

        conf.setPassword(test);
        assertEquals(test, conf.getPassword());
        System.setProperty("twitter4j.password", override);
        conf = new PropertyConfiguration();
        assertEquals(override, conf.getPassword());
        conf.setPassword(test);
        assertEquals(test, conf.getPassword());
        System.getProperties().remove("twitter4j.password");

        System.getProperties().remove("twitter4j.http.proxyHost");
        conf = new PropertyConfiguration();
        assertNull(conf.getHttpClientConfiguration().getHttpProxyHost());

        System.setProperty("twitter4j.http.proxyHost", override);
        conf = new PropertyConfiguration();
        assertEquals(override, conf.getHttpClientConfiguration().getHttpProxyHost());
        System.getProperties().remove("twitter4j.http.proxyHost");

        System.getProperties().remove("twitter4j.http.proxyPort");
        conf = new PropertyConfiguration();
        assertEquals(-1, conf.getHttpClientConfiguration().getHttpProxyPort());

        System.setProperty("twitter4j.http.proxyPort", "100");
        conf = new PropertyConfiguration();
        assertEquals(100, conf.getHttpClientConfiguration().getHttpProxyPort());
        System.getProperties().remove("twitter4j.http.proxyPort");


        System.getProperties().remove("twitter4j.http.proxyUser");
        conf = new PropertyConfiguration();
        assertNull(conf.getHttpClientConfiguration().getHttpProxyUser());

        System.setProperty("twitter4j.http.proxyUser", override);
        conf = new PropertyConfiguration();
        assertEquals(override, conf.getHttpClientConfiguration().getHttpProxyUser());
        System.getProperties().remove("twitter4j.http.proxyUser");


        System.getProperties().remove("twitter4j.http.proxyPassword");
        conf = new PropertyConfiguration();
        assertNull(conf.getHttpClientConfiguration().getHttpProxyPassword());

        System.setProperty("twitter4j.http.proxyPassword", override);
        conf = new PropertyConfiguration();
        assertEquals(override, conf.getHttpClientConfiguration().getHttpProxyPassword());
        System.getProperties().remove("twitter4j.http.proxyPassword");


        System.getProperties().remove("twitter4j.http.connectionTimeout");
        conf = new PropertyConfiguration();
        assertEquals(20000, conf.getHttpClientConfiguration().getHttpConnectionTimeout());

        conf.setHttpConnectionTimeout(10);
        assertEquals(10, conf.getHttpClientConfiguration().getHttpConnectionTimeout());
        System.setProperty("twitter4j.http.connectionTimeout", "100");
        conf = new PropertyConfiguration();
        assertEquals(100, conf.getHttpClientConfiguration().getHttpConnectionTimeout());
        conf.setHttpConnectionTimeout(10);
        assertEquals(10, conf.getHttpClientConfiguration().getHttpConnectionTimeout());
        System.getProperties().remove("twitter4j.http.connectionTimeout");


        System.getProperties().remove("twitter4j.http.readTimeout");
        conf = new PropertyConfiguration();
        assertEquals(120000, conf.getHttpClientConfiguration().getHttpReadTimeout());

        conf.setHttpReadTimeout(10);
        assertEquals(10, conf.getHttpClientConfiguration().getHttpReadTimeout());
        System.setProperty("twitter4j.http.readTimeout", "100");
        conf = new PropertyConfiguration();
        assertEquals(100, conf.getHttpClientConfiguration().getHttpReadTimeout());
        conf.setHttpReadTimeout(10);
        assertEquals(10, conf.getHttpClientConfiguration().getHttpReadTimeout());
        System.getProperties().remove("twitter4j.http.readTimeout");

        writeFile("./twitter4j.properties", "twitter4j.http.readTimeout=1234");
        conf = new PropertyConfiguration();
        assertEquals(1234, conf.getHttpClientConfiguration().getHttpReadTimeout());
        writeFile("./twitter4j.properties", "twitter4j.http.readTimeout=4321");
        conf = new PropertyConfiguration();
        assertEquals(4321, conf.getHttpClientConfiguration().getHttpReadTimeout());
        deleteFile("./twitter4j.properties");
    }

    @Test
    void testSSL() throws Exception {
        Configuration conf;

        // disable SSL
        writeFile("./twitter4j.properties", "twitter4j.restBaseURL=http://somewhere.com/"
                + "\n" + "twitter4j.http.useSSL=false");
        conf = new PropertyConfiguration("/");
        assertEquals("http://somewhere.com/", conf.getRestBaseURL());

        // explicitly enabling SSL
        writeFile("./twitter4j.properties", "twitter4j.restBaseURL=http://somewhere.com/"
                + "\n" + "twitter4j.http.useSSL=true");
        conf = new PropertyConfiguration("/");
        // useSSL doesn't take effect if restBaseURL is explicitly specified.
        assertEquals("http://somewhere.com/", conf.getRestBaseURL());
        deleteFile("./twitter4j.properties");
        conf = new PropertyConfiguration();

        // uses SSL by default
        System.getProperties().remove("twitter4j.http.useSSL");

        writeFile("./twitter4j.properties", "restBaseURL=http://somewhere.com/");
        conf = new PropertyConfiguration("/");
        assertEquals("http://somewhere.com/", conf.getRestBaseURL());
        deleteFile("./twitter4j.properties");
    }

    @Test
    void testTwitter4jPrefixOmittable() throws Exception {
        System.getProperties().remove("http.useSSL");
        System.getProperties().remove("twitter4j.http.useSSL");
        Configuration conf;
        writeFile("./twitter4j.properties", "twitter4j.restBaseURL=http://somewhere.com/");
        conf = new PropertyConfiguration("/");
        assertEquals("http://somewhere.com/", conf.getRestBaseURL());
        writeFile("./twitter4j.properties", "restBaseURL=http://somewhere2.com/");

        conf = new PropertyConfiguration("/");
        assertEquals("http://somewhere2.com/", conf.getRestBaseURL());
    }

    @Test
    void testTreeConfiguration() throws Exception {
        Configuration conf;
        writeFile("./twitter4j.properties", "twitter4j.restBaseURL=http://somewhere.com/"
                + "\n" + "twitter4j.http.useSSL=false");
        conf = new PropertyConfiguration("/");
        assertEquals("http://somewhere.com/", conf.getRestBaseURL());
        writeFile("./twitter4j.properties", "twitter4j.restBaseURL=http://somewhere.com/"
                + "\n" + "twitter4j.http.useSSL=false"
                + "\n" + "china.twitter4j.restBaseURL=http://somewhere.cn/");

        conf = new PropertyConfiguration("/china");
        assertEquals("http://somewhere.cn/", conf.getRestBaseURL());

        conf = new PropertyConfiguration("/china/");
        assertEquals("http://somewhere.cn/", conf.getRestBaseURL());
        deleteFile("./twitter4j.properties");

        // configuration for two different countries and default
        writeFile("./twitter4j.properties", "restBaseURL=http://somewhere.com/"
                + "\n" + "http.useSSL=false"
                + "\n" + "user=one"
                + "\n" + "china.restBaseURL=http://somewhere.cn/"
                + "\n" + "china.user=two"
                + "\n" + "japan.restBaseURL=http://yusuke.homeip.net/"
                + "\n" + "japan.user=three"
        );
        conf = new PropertyConfiguration();
        assertEquals("one", conf.getUser());
        conf = new PropertyConfiguration("/china");
        assertEquals("two", conf.getUser());
        conf = new PropertyConfiguration("/japan");
        assertEquals("three", conf.getUser());


        writeFile("./twitter4j.properties", "restBaseURL=http://somewhere.com/"
                + "\n" + "http.useSSL=false"
                + "\n" + "user=one"
                + "\n" + "password=pasword-one"
                + "\n" + "china.restBaseURL=http://somewhere.cn/"
                + "\n" + "china.user1.user=two"
                + "\n" + "china.user1.password=pasword-two"
                + "\n" + "china.user2.user=three"
                + "\n" + "china.user2.password=pasword-three"
        );
        conf = new PropertyConfiguration();
        assertEquals("one", conf.getUser());
        conf = new PropertyConfiguration("/china/user1");
        assertEquals("two", conf.getUser());
        assertEquals("pasword-two", conf.getPassword());
        conf = new PropertyConfiguration("/china/user2");
        assertEquals("three", conf.getUser());
        assertEquals("pasword-three", conf.getPassword());

        deleteFile("./twitter4j.properties");
    }

    @Test
    void testConfigurationBuilder() throws Exception {
        deleteFile("./twitter4j.properties");
        ConfigurationBuilder builder;
        Configuration conf;
        builder = new ConfigurationBuilder();
        conf = builder.build();

        Configuration t = (Configuration) serializeDeserialize(conf);

        assertEquals(conf, serializeDeserialize(conf));

        assertEquals(0, conf.getRestBaseURL().indexOf("https://"));
        assertEquals(0, conf.getOAuthAuthenticationURL().indexOf("https://"));
        assertEquals(0, conf.getOAuthAuthorizationURL().indexOf("https://"));
        assertEquals(0, conf.getOAuthAccessTokenURL().indexOf("https://"));
        assertEquals(0, conf.getOAuthRequestTokenURL().indexOf("https://"));

        builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey("key");
        builder.setOAuthConsumerSecret("secret");
        conf = builder.build();
        assertEquals(0, conf.getRestBaseURL().indexOf("https://"));
        assertEquals(0, conf.getOAuthAuthenticationURL().indexOf("https://"));
        assertEquals(0, conf.getOAuthAuthorizationURL().indexOf("https://"));
        assertEquals(0, conf.getOAuthAccessTokenURL().indexOf("https://"));
        assertEquals(0, conf.getOAuthRequestTokenURL().indexOf("https://"));

        RequestToken rt = new RequestToken("key", "secret");

        // TFJ-328 RequestToken.getAuthenticationURL()/getAuthorizationURL() should return URLs starting with https:// for security reasons
        assertEquals(0, rt.getAuthenticationURL().indexOf("https://"));
        assertEquals(0, rt.getAuthorizationURL().indexOf("https://"));
        assertEquals(0, conf.getOAuthAccessTokenURL().indexOf("https://"));
        assertEquals(0, conf.getOAuthRequestTokenURL().indexOf("https://"));

        // disable SSL
        writeFile("./twitter4j.properties", "twitter4j.restBaseURL=http://somewhere.com/"
                + "\n" + "twitter4j.debug=true"
                + "\n" + "media.providerParameters=debug=true&foo=bar");
        conf = new ConfigurationBuilder().build();
        assertEquals("http://somewhere.com/", conf.getRestBaseURL());
        assertTrue(conf.isDebugEnabled());
        Properties mediaProps = conf.getMediaProviderParameters();
        assertNotNull(mediaProps);
        assertNull(mediaProps.getProperty("hoge"));
        assertEquals("true", mediaProps.getProperty("debug"));
        assertEquals("bar", mediaProps.getProperty("foo"));

        deleteFile("./twitter4j.properties");
    }


    private static Object serializeDeserialize(Object obj) throws Exception {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(byteOutputStream);
        oos.writeObject(obj);
        byteOutputStream.close();
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(byteInputStream);
        Object that = ois.readObject();
        byteInputStream.close();
        ois.close();
        return that;
    }

    @Test
    void testEnvironmentVariableBasedConfiguration() throws Exception {
        Configuration conf = ConfigurationContext.getInstance();
        // perquisite: export twitter4j.debug=true
//        assertTrue(conf.isDebugEnabled());
        // perquisite: export twitter4j.debug=false
//        assertFalse(conf.isDebugEnabled());
    }

    private void writeFile(String path, String content) throws IOException {
        File file = new File(path);
        file.delete();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(content);
        bw.close();
    }

    private void deleteFile(String path) throws IOException {
        File file = new File(path);
        file.delete();
    }
}

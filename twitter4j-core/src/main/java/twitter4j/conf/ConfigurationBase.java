/*
 * Copyright 2007 Yusuke Yamamoto
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

import twitter4j.HttpClientConfiguration;
import twitter4j.Logger;

import java.io.ObjectStreamException;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Configuration base class with default settings.
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
class ConfigurationBase implements Configuration, java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 6175546394599249696L;
    private boolean debug = false;
    private String user = null;
    private String password = null;
    private HttpClientConfiguration httpConf;

    private int httpStreamingReadTimeout = 40 * 1000;
    private int httpRetryCount = 0;
    private int httpRetryIntervalSeconds = 5;

    private String oAuthConsumerKey = null;
    private String oAuthConsumerSecret = null;
    private String oAuthAccessToken = null;
    private String oAuthAccessTokenSecret = null;
    private String oAuth2TokenType;
    private String oAuth2AccessToken;
    private String oAuth2Scope;
    private String oAuthRequestTokenURL = "https://api.twitter.com/oauth/request_token";
    private String oAuthAuthorizationURL = "https://api.twitter.com/oauth/authorize";
    private String oAuthAccessTokenURL = "https://api.twitter.com/oauth/access_token";
    private String oAuthAuthenticationURL = "https://api.twitter.com/oauth/authenticate";
    private String oAuth2TokenURL = "https://api.twitter.com/oauth2/token";
    private String oAuth2InvalidateTokenURL = "https://api.twitter.com/oauth2/invalidate_token";

    private String restBaseURL = "https://api.twitter.com/1.1/";
    private String streamBaseURL = "https://stream.twitter.com/1.1/";
    private String userStreamBaseURL = "https://userstream.twitter.com/1.1/";
    private String siteStreamBaseURL = "https://sitestream.twitter.com/1.1/";
    private String uploadBaseURL = "https://upload.twitter.com/1.1/";

    private String dispatcherImpl = "twitter4j.DispatcherImpl";
    private int asyncNumThreads = 1;

    private String loggerFactory = null;

    private long contributingTo = -1L;

    private boolean includeMyRetweetEnabled = true;
    private boolean includeEntitiesEnabled = true;
    private boolean trimUserEnabled = false;
    private boolean includeExtAltTextEnabled = true;
    private boolean tweetModeExtended = true;
    private boolean includeEmailEnabled = false;

    private boolean jsonStoreEnabled = false;

    private boolean mbeanEnabled = false;

    private boolean userStreamRepliesAllEnabled = false;
    private boolean userStreamWithFollowingsEnabled = true;
    private boolean stallWarningsEnabled = true;

    private boolean applicationOnlyAuthEnabled = false;

    private String mediaProvider = "TWITTER";
    private String mediaProviderAPIKey = null;
    private Properties mediaProviderParameters = null;
    private boolean daemonEnabled = true;

    private String streamThreadName = "";

    protected ConfigurationBase() {
        httpConf = new MyHttpClientConfiguration(null // proxy host
                , null // proxy user
                , null // proxy password
                , -1 // proxy port
                , false // proxy socks
                , 20000 // connection timeout
                , 120000 // read timeout
                , false // pretty debug
                , true // gzip enabled
        );
    }

    class MyHttpClientConfiguration implements HttpClientConfiguration, Serializable {
        @Serial
        private static final long serialVersionUID = 8226866124868861058L;
        private String httpProxyHost = null;
        private String httpProxyUser = null;
        private String httpProxyPassword = null;
        private boolean httpProxySocks = false;
        private int httpProxyPort = -1;
        private int httpConnectionTimeout = 20000;
        private int httpReadTimeout = 120000;
        private boolean prettyDebug = false;
        private boolean gzipEnabled = true;

        MyHttpClientConfiguration(String httpProxyHost, String httpProxyUser, String httpProxyPassword, int httpProxyPort, boolean httpProxySocks, int httpConnectionTimeout, int httpReadTimeout, boolean prettyDebug, boolean gzipEnabled) {
            this.httpProxyHost = httpProxyHost;
            this.httpProxyUser = httpProxyUser;
            this.httpProxyPassword = httpProxyPassword;
            this.httpProxyPort = httpProxyPort;
            this.httpProxySocks = httpProxySocks;
            this.httpConnectionTimeout = httpConnectionTimeout;
            this.httpReadTimeout = httpReadTimeout;
            this.prettyDebug = prettyDebug;
            this.gzipEnabled = gzipEnabled;
        }

        @Override
        public String getHttpProxyHost() {
            return httpProxyHost;
        }

        @Override
        public int getHttpProxyPort() {
            return httpProxyPort;
        }

        @Override
        public String getHttpProxyUser() {
            return httpProxyUser;
        }

        @Override
        public String getHttpProxyPassword() {
            return httpProxyPassword;
        }

        @Override
        public boolean isHttpProxySocks() {
            return httpProxySocks;
        }

        @Override
        public int getHttpConnectionTimeout() {
            return httpConnectionTimeout;
        }

        @Override
        public int getHttpReadTimeout() {
            return httpReadTimeout;
        }

        @Override
        public int getHttpRetryCount() {
            return httpRetryCount;
        }

        @Override
        public int getHttpRetryIntervalSeconds() {
            return httpRetryIntervalSeconds;
        }

        @Override
        public boolean isPrettyDebugEnabled() {
            return prettyDebug;
        }

        @Override
        public boolean isGZIPEnabled() {
            return gzipEnabled;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyHttpClientConfiguration that = (MyHttpClientConfiguration) o;

            if (gzipEnabled != that.gzipEnabled) return false;
            if (httpProxySocks != that.httpProxySocks) return false;
            if (httpConnectionTimeout != that.httpConnectionTimeout) return false;
            if (httpProxyPort != that.httpProxyPort) return false;
            if (httpProxySocks != that.httpProxySocks) return false;
            if (httpReadTimeout != that.httpReadTimeout) return false;
            if (prettyDebug != that.prettyDebug) return false;
            if (!Objects.equals(httpProxyHost, that.httpProxyHost))
                return false;
            if (!Objects.equals(httpProxyPassword, that.httpProxyPassword))
                return false;
            return Objects.equals(httpProxyUser, that.httpProxyUser);
        }

        @Override
        public int hashCode() {
            int result = httpProxyHost != null ? httpProxyHost.hashCode() : 0;
            result = 31 * result + (httpProxyUser != null ? httpProxyUser.hashCode() : 0);
            result = 31 * result + (httpProxyPassword != null ? httpProxyPassword.hashCode() : 0);
            result = 31 * result + httpProxyPort;
            result = 31 * result + (httpProxySocks ? 1 : 0);
            result = 31 * result + httpConnectionTimeout;
            result = 31 * result + httpReadTimeout;
            result = 31 * result + (prettyDebug ? 1 : 0);
            result = 31 * result + (gzipEnabled ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "MyHttpClientConfiguration{" +
                    "httpProxyHost='" + httpProxyHost + '\'' +
                    ", httpProxyUser='" + httpProxyUser + '\'' +
                    ", httpProxyPassword='" + httpProxyPassword + '\'' +
                    ", httpProxyPort=" + httpProxyPort +
                    ", proxyType=" + (httpProxySocks ? Proxy.Type.SOCKS : Proxy.Type.HTTP) +
                    ", httpConnectionTimeout=" + httpConnectionTimeout +
                    ", httpReadTimeout=" + httpReadTimeout +
                    ", prettyDebug=" + prettyDebug +
                    ", gzipEnabled=" + gzipEnabled +
                    '}';
        }
    }


    public void dumpConfiguration() {
        Logger log = Logger.getLogger(ConfigurationBase.class);
        if (debug) {
            Field[] fields = ConfigurationBase.class.getDeclaredFields();
            for (Field field : fields) {
                try {
                    Object value = field.get(this);
                    String strValue = String.valueOf(value);
                    if (value != null && field.getName().matches("oAuthConsumerSecret|oAuthAccessTokenSecret|password")) {
                        strValue = String.valueOf(value).replaceAll(".", "*");
                    }
                    log.debug(field.getName() + ": " + strValue);
                } catch (IllegalAccessException ignore) {
                }
            }
        }
    }

    @Override
    public final boolean isDebugEnabled() {
        return debug;
    }

    protected final void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public final String getUser() {
        return user;
    }

    protected final void setUser(String user) {
        this.user = user;
    }

    @Override
    public final String getPassword() {
        return password;
    }

    @Override
    public HttpClientConfiguration getHttpClientConfiguration() {
        return httpConf;
    }

    protected final void setPassword(String password) {
        this.password = password;
    }

    protected final void setPrettyDebugEnabled(boolean prettyDebug) {
        httpConf = new MyHttpClientConfiguration(httpConf.getHttpProxyHost()
                , httpConf.getHttpProxyUser()
                , httpConf.getHttpProxyPassword()
                , httpConf.getHttpProxyPort()
                , httpConf.isHttpProxySocks()
                , httpConf.getHttpConnectionTimeout()
                , httpConf.getHttpReadTimeout()
                , prettyDebug, httpConf.isGZIPEnabled()
        );
    }

    protected final void setGZIPEnabled(boolean gzipEnabled) {
        httpConf = new MyHttpClientConfiguration(httpConf.getHttpProxyHost()
                , httpConf.getHttpProxyUser()
                , httpConf.getHttpProxyPassword()
                , httpConf.getHttpProxyPort()
                , httpConf.isHttpProxySocks()
                , httpConf.getHttpConnectionTimeout()
                , httpConf.getHttpReadTimeout()
                , httpConf.isPrettyDebugEnabled(), gzipEnabled
        );
    }

    // methods for HttpClientConfiguration

    protected final void setHttpProxyHost(String proxyHost) {
        httpConf = new MyHttpClientConfiguration(proxyHost
                , httpConf.getHttpProxyUser()
                , httpConf.getHttpProxyPassword()
                , httpConf.getHttpProxyPort()
                , httpConf.isHttpProxySocks()
                , httpConf.getHttpConnectionTimeout()
                , httpConf.getHttpReadTimeout()
                , httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        );
    }

    protected final void setHttpProxyUser(String proxyUser) {
        httpConf = new MyHttpClientConfiguration(httpConf.getHttpProxyHost()
                , proxyUser
                , httpConf.getHttpProxyPassword()
                , httpConf.getHttpProxyPort()
                , httpConf.isHttpProxySocks()
                , httpConf.getHttpConnectionTimeout()
                , httpConf.getHttpReadTimeout()
                , httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        );
    }

    protected final void setHttpProxyPassword(String proxyPassword) {
        httpConf = new MyHttpClientConfiguration(httpConf.getHttpProxyHost()
                , httpConf.getHttpProxyUser()
                , proxyPassword
                , httpConf.getHttpProxyPort()
                , httpConf.isHttpProxySocks()
                , httpConf.getHttpConnectionTimeout()
                , httpConf.getHttpReadTimeout()
                , httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        );
    }

    protected final void setHttpProxyPort(int proxyPort) {
        httpConf = new MyHttpClientConfiguration(httpConf.getHttpProxyHost()
                , httpConf.getHttpProxyUser()
                , httpConf.getHttpProxyPassword()
                , proxyPort
                , httpConf.isHttpProxySocks()
                , httpConf.getHttpConnectionTimeout()
                , httpConf.getHttpReadTimeout()
                , httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        );
    }

    protected final void setHttpProxySocks(boolean isSocksProxy) {
        httpConf = new MyHttpClientConfiguration(httpConf.getHttpProxyHost()
                , httpConf.getHttpProxyUser()
                , httpConf.getHttpProxyPassword()
                , httpConf.getHttpProxyPort()
                , isSocksProxy
                , httpConf.getHttpConnectionTimeout()
                , httpConf.getHttpReadTimeout()
                , httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        );
    }

    protected final void setHttpConnectionTimeout(int connectionTimeout) {
        httpConf = new MyHttpClientConfiguration(httpConf.getHttpProxyHost()
                , httpConf.getHttpProxyUser()
                , httpConf.getHttpProxyPassword()
                , httpConf.getHttpProxyPort()
                , httpConf.isHttpProxySocks()
                , connectionTimeout
                , httpConf.getHttpReadTimeout()
                , httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        );
    }

    protected final void setHttpReadTimeout(int readTimeout) {
        httpConf = new MyHttpClientConfiguration(httpConf.getHttpProxyHost()
                , httpConf.getHttpProxyUser()
                , httpConf.getHttpProxyPassword()
                , httpConf.getHttpProxyPort()
                , httpConf.isHttpProxySocks()
                , httpConf.getHttpConnectionTimeout()
                , readTimeout
                , httpConf.isPrettyDebugEnabled(), httpConf.isGZIPEnabled()
        );
    }

    @Override
    public int getHttpStreamingReadTimeout() {
        return httpStreamingReadTimeout;
    }

    protected final void setHttpStreamingReadTimeout(int httpStreamingReadTimeout) {
        this.httpStreamingReadTimeout = httpStreamingReadTimeout;
    }

    protected final void setHttpRetryCount(int retryCount) {
        this.httpRetryCount = retryCount;
    }

    protected final void setHttpRetryIntervalSeconds(int retryIntervalSeconds) {
        this.httpRetryIntervalSeconds = retryIntervalSeconds;
    }

    // oauth related setter/getters

    @Override
    public final String getOAuthConsumerKey() {
        return oAuthConsumerKey;
    }

    protected final void setOAuthConsumerKey(String oAuthConsumerKey) {
        this.oAuthConsumerKey = oAuthConsumerKey;
    }

    @Override
    public final String getOAuthConsumerSecret() {
        return oAuthConsumerSecret;
    }

    protected final void setOAuthConsumerSecret(String oAuthConsumerSecret) {
        this.oAuthConsumerSecret = oAuthConsumerSecret;
    }

    @Override
    public String getOAuthAccessToken() {
        return oAuthAccessToken;
    }

    protected final void setOAuthAccessToken(String oAuthAccessToken) {
        this.oAuthAccessToken = oAuthAccessToken;
    }

    @Override
    public String getOAuthAccessTokenSecret() {
        return oAuthAccessTokenSecret;
    }

    protected final void setOAuthAccessTokenSecret(String oAuthAccessTokenSecret) {
        this.oAuthAccessTokenSecret = oAuthAccessTokenSecret;
    }

    @Override
    public String getOAuth2TokenType() {
        return oAuth2TokenType;
    }

    protected final void setOAuth2TokenType(String oAuth2TokenType) {
        this.oAuth2TokenType = oAuth2TokenType;
    }

    @Override
    public String getOAuth2AccessToken() {
        return oAuth2AccessToken;
    }

    @Override
    public String getOAuth2Scope() {
        return oAuth2Scope;
    }

    protected final void setOAuth2AccessToken(String oAuth2AccessToken) {
        this.oAuth2AccessToken = oAuth2AccessToken;
    }

    protected final void setOAuth2Scope(String oAuth2Scope) {
        this.oAuth2Scope = oAuth2Scope;
    }

    @Override
    public final int getAsyncNumThreads() {
        return asyncNumThreads;
    }

    protected final void setAsyncNumThreads(int asyncNumThreads) {
        this.asyncNumThreads = asyncNumThreads;
    }

    @Override
    public final long getContributingTo() {
        return contributingTo;
    }

    protected final void setContributingTo(long contributingTo) {
        this.contributingTo = contributingTo;
    }

    @Override
    public String getRestBaseURL() {
        return restBaseURL;
    }

    protected final void setRestBaseURL(String restBaseURL) {
        this.restBaseURL = restBaseURL;
    }

    @Override
    public String getUploadBaseURL() {
        return uploadBaseURL;
    }

    protected final void setUploadBaseURL(String uploadBaseURL) {
        this.uploadBaseURL = uploadBaseURL;
    }

    @Override
    public String getStreamBaseURL() {
        return streamBaseURL;
    }

    protected final void setStreamBaseURL(String streamBaseURL) {
        this.streamBaseURL = streamBaseURL;
    }

    @Override
    public String getUserStreamBaseURL() {
        return userStreamBaseURL;
    }

    protected final void setUserStreamBaseURL(String siteStreamBaseURL) {
        this.userStreamBaseURL = siteStreamBaseURL;
    }

    @Override
    public String getSiteStreamBaseURL() {
        return siteStreamBaseURL;
    }

    protected final void setSiteStreamBaseURL(String siteStreamBaseURL) {
        this.siteStreamBaseURL = siteStreamBaseURL;
    }

    @Override
    public String getOAuthRequestTokenURL() {
        return oAuthRequestTokenURL;
    }

    protected final void setOAuthRequestTokenURL(String oAuthRequestTokenURL) {
        this.oAuthRequestTokenURL = oAuthRequestTokenURL;
    }

    @Override
    public String getOAuthAuthorizationURL() {
        return oAuthAuthorizationURL;
    }

    protected final void setOAuthAuthorizationURL(String oAuthAuthorizationURL) {
        this.oAuthAuthorizationURL = oAuthAuthorizationURL;
    }

    @Override
    public String getOAuthAccessTokenURL() {
        return oAuthAccessTokenURL;
    }

    protected final void setOAuthAccessTokenURL(String oAuthAccessTokenURL) {
        this.oAuthAccessTokenURL = oAuthAccessTokenURL;
    }

    @Override
    public String getOAuthAuthenticationURL() {
        return oAuthAuthenticationURL;
    }

    protected final void setOAuthAuthenticationURL(String oAuthAuthenticationURL) {
        this.oAuthAuthenticationURL = oAuthAuthenticationURL;
    }

    @Override
    public String getOAuth2TokenURL() {
        return oAuth2TokenURL;
    }

    protected final void setOAuth2TokenURL(String oAuth2TokenURL) {
        this.oAuth2TokenURL = oAuth2TokenURL;
    }

    @Override
    public String getOAuth2InvalidateTokenURL() {
        return oAuth2InvalidateTokenURL;
    }

    protected final void setOAuth2InvalidateTokenURL(String oAuth2InvalidateTokenURL) {
        this.oAuth2InvalidateTokenURL = oAuth2InvalidateTokenURL;
    }

    @Override
    public String getDispatcherImpl() {
        return dispatcherImpl;
    }

    protected final void setDispatcherImpl(String dispatcherImpl) {
        this.dispatcherImpl = dispatcherImpl;
    }

    @Override
    public String getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public boolean isIncludeEntitiesEnabled() {
        return includeEntitiesEnabled;
    }

    protected void setIncludeEntitiesEnabled(boolean includeEntitiesEnabled) {
        this.includeEntitiesEnabled = includeEntitiesEnabled;
    }

    protected final void setLoggerFactory(String loggerImpl) {
        this.loggerFactory = loggerImpl;
    }

    @Override
    public boolean isIncludeMyRetweetEnabled() {
        return this.includeMyRetweetEnabled;
    }

    public void setIncludeMyRetweetEnabled(boolean enabled) {
        this.includeMyRetweetEnabled = enabled;
    }

    @Override
    public boolean isTrimUserEnabled() {
        return this.trimUserEnabled;
    }

    @Override
    public boolean isIncludeExtAltTextEnabled() {
        return this.includeExtAltTextEnabled;
    }

    @Override
    public boolean isTweetModeExtended() {
        return this.tweetModeExtended;
    }

    @Override
    public boolean isDaemonEnabled() {
        return daemonEnabled;
    }

    protected void setDaemonEnabled(boolean daemonEnabled) {
        this.daemonEnabled = daemonEnabled;
    }

    @Override
    public boolean isIncludeEmailEnabled() {
        return includeEmailEnabled;
    }

    protected void setIncludeEmailEnabled(boolean includeEmailEnabled) {
        this.includeEmailEnabled = includeEmailEnabled;
    }

    public void setTrimUserEnabled(boolean enabled) {
        this.trimUserEnabled = enabled;
    }

    public void setIncludeExtAltTextEnabled(boolean enabled) {
        this.includeExtAltTextEnabled = enabled;
    }

    public void setTweetModeExtended(boolean enabled) {
        this.tweetModeExtended = enabled;
    }

    @Override
    public boolean isJSONStoreEnabled() {
        return this.jsonStoreEnabled;
    }

    protected final void setJSONStoreEnabled(boolean enabled) {
        this.jsonStoreEnabled = enabled;
    }

    @Override
    public boolean isMBeanEnabled() {
        return this.mbeanEnabled;
    }

    protected final void setMBeanEnabled(boolean enabled) {
        this.mbeanEnabled = enabled;
    }

    @Override
    public boolean isUserStreamRepliesAllEnabled() {
        return this.userStreamRepliesAllEnabled;
    }

    @Override
    public boolean isUserStreamWithFollowingsEnabled() {
        return this.userStreamWithFollowingsEnabled;
    }

    protected final void setUserStreamRepliesAllEnabled(boolean enabled) {
        this.userStreamRepliesAllEnabled = enabled;
    }

    protected final void setUserStreamWithFollowingsEnabled(boolean enabled) {
        this.userStreamWithFollowingsEnabled = enabled;
    }

    @Override
    public boolean isStallWarningsEnabled() {
        return stallWarningsEnabled;
    }

    protected final void setStallWarningsEnabled(boolean stallWarningsEnabled) {
        this.stallWarningsEnabled = stallWarningsEnabled;
    }

    @Override
    public boolean isApplicationOnlyAuthEnabled() {
        return applicationOnlyAuthEnabled;
    }

    protected final void setApplicationOnlyAuthEnabled(boolean applicationOnlyAuthEnabled) {
        this.applicationOnlyAuthEnabled = applicationOnlyAuthEnabled;
    }

    @Override
    public String getMediaProvider() {
        return this.mediaProvider;
    }

    protected final void setMediaProvider(String mediaProvider) {
        this.mediaProvider = mediaProvider;
    }

    @Override
    public String getMediaProviderAPIKey() {
        return this.mediaProviderAPIKey;
    }

    protected final void setMediaProviderAPIKey(String mediaProviderAPIKey) {
        this.mediaProviderAPIKey = mediaProviderAPIKey;
    }

    @Override
    public Properties getMediaProviderParameters() {
        return this.mediaProviderParameters;
    }

    protected final void setMediaProviderParameters(Properties props) {
        this.mediaProviderParameters = props;
    }

    @Override
    public String getStreamThreadName() {
        return this.streamThreadName;
    }

    protected final void setStreamThreadName(String streamThreadName) {
        this.streamThreadName = streamThreadName;
    }

    static String fixURL(boolean useSSL, String url) {
        if (null == url) {
            return null;
        }
        int index = url.indexOf("://");
        if (-1 == index) {
            throw new IllegalArgumentException("url should contain '://'");
        }
        String hostAndLater = url.substring(index + 3);
        if (useSSL) {
            return "https://" + hostAndLater;
        } else {
            return "http://" + hostAndLater;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationBase that = (ConfigurationBase) o;

        if (debug != that.debug) return false;
        if (httpStreamingReadTimeout != that.httpStreamingReadTimeout) return false;
        if (httpRetryCount != that.httpRetryCount) return false;
        if (httpRetryIntervalSeconds != that.httpRetryIntervalSeconds) return false;
        if (asyncNumThreads != that.asyncNumThreads) return false;
        if (contributingTo != that.contributingTo) return false;
        if (includeMyRetweetEnabled != that.includeMyRetweetEnabled) return false;
        if (includeEntitiesEnabled != that.includeEntitiesEnabled) return false;
        if (trimUserEnabled != that.trimUserEnabled) return false;
        if (includeExtAltTextEnabled != that.includeExtAltTextEnabled) return false;
        if (tweetModeExtended != that.tweetModeExtended) return false;
        if (includeEmailEnabled != that.includeEmailEnabled) return false;
        if (jsonStoreEnabled != that.jsonStoreEnabled) return false;
        if (mbeanEnabled != that.mbeanEnabled) return false;
        if (userStreamRepliesAllEnabled != that.userStreamRepliesAllEnabled) return false;
        if (userStreamWithFollowingsEnabled != that.userStreamWithFollowingsEnabled) return false;
        if (stallWarningsEnabled != that.stallWarningsEnabled) return false;
        if (applicationOnlyAuthEnabled != that.applicationOnlyAuthEnabled) return false;
        if (daemonEnabled != that.daemonEnabled) return false;
        if (!Objects.equals(user, that.user)) return false;
        if (!Objects.equals(password, that.password)) return false;
        if (!Objects.equals(httpConf, that.httpConf)) return false;
        if (!Objects.equals(oAuthConsumerKey, that.oAuthConsumerKey))
            return false;
        if (!Objects.equals(oAuthConsumerSecret, that.oAuthConsumerSecret))
            return false;
        if (!Objects.equals(oAuthAccessToken, that.oAuthAccessToken))
            return false;
        if (!Objects.equals(oAuthAccessTokenSecret, that.oAuthAccessTokenSecret))
            return false;
        if (!Objects.equals(oAuth2TokenType, that.oAuth2TokenType))
            return false;
        if (!Objects.equals(oAuth2AccessToken, that.oAuth2AccessToken))
            return false;
        if (!Objects.equals(oAuth2Scope, that.oAuth2Scope)) return false;
        if (!Objects.equals(oAuthRequestTokenURL, that.oAuthRequestTokenURL))
            return false;
        if (!Objects.equals(oAuthAuthorizationURL, that.oAuthAuthorizationURL))
            return false;
        if (!Objects.equals(oAuthAccessTokenURL, that.oAuthAccessTokenURL))
            return false;
        if (!Objects.equals(oAuthAuthenticationURL, that.oAuthAuthenticationURL))
            return false;
        if (!Objects.equals(oAuth2TokenURL, that.oAuth2TokenURL))
            return false;
        if (!Objects.equals(oAuth2InvalidateTokenURL, that.oAuth2InvalidateTokenURL))
            return false;
        if (!Objects.equals(restBaseURL, that.restBaseURL)) return false;
        if (!Objects.equals(streamBaseURL, that.streamBaseURL))
            return false;
        if (!Objects.equals(userStreamBaseURL, that.userStreamBaseURL))
            return false;
        if (!Objects.equals(siteStreamBaseURL, that.siteStreamBaseURL))
            return false;
        if (!Objects.equals(uploadBaseURL, that.uploadBaseURL))
            return false;
        if (!Objects.equals(dispatcherImpl, that.dispatcherImpl))
            return false;
        if (!Objects.equals(loggerFactory, that.loggerFactory))
            return false;
        if (!Objects.equals(mediaProvider, that.mediaProvider))
            return false;
        if (!Objects.equals(mediaProviderAPIKey, that.mediaProviderAPIKey))
            return false;
        if (!Objects.equals(mediaProviderParameters, that.mediaProviderParameters))
            return false;
        return Objects.equals(streamThreadName, that.streamThreadName);

    }

    @Override
    public int hashCode() {
        int result = (debug ? 1 : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (httpConf != null ? httpConf.hashCode() : 0);
        result = 31 * result + httpStreamingReadTimeout;
        result = 31 * result + httpRetryCount;
        result = 31 * result + httpRetryIntervalSeconds;
        result = 31 * result + (oAuthConsumerKey != null ? oAuthConsumerKey.hashCode() : 0);
        result = 31 * result + (oAuthConsumerSecret != null ? oAuthConsumerSecret.hashCode() : 0);
        result = 31 * result + (oAuthAccessToken != null ? oAuthAccessToken.hashCode() : 0);
        result = 31 * result + (oAuthAccessTokenSecret != null ? oAuthAccessTokenSecret.hashCode() : 0);
        result = 31 * result + (oAuth2TokenType != null ? oAuth2TokenType.hashCode() : 0);
        result = 31 * result + (oAuth2AccessToken != null ? oAuth2AccessToken.hashCode() : 0);
        result = 31 * result + (oAuth2Scope != null ? oAuth2Scope.hashCode() : 0);
        result = 31 * result + (oAuthRequestTokenURL != null ? oAuthRequestTokenURL.hashCode() : 0);
        result = 31 * result + (oAuthAuthorizationURL != null ? oAuthAuthorizationURL.hashCode() : 0);
        result = 31 * result + (oAuthAccessTokenURL != null ? oAuthAccessTokenURL.hashCode() : 0);
        result = 31 * result + (oAuthAuthenticationURL != null ? oAuthAuthenticationURL.hashCode() : 0);
        result = 31 * result + (oAuth2TokenURL != null ? oAuth2TokenURL.hashCode() : 0);
        result = 31 * result + (oAuth2InvalidateTokenURL != null ? oAuth2InvalidateTokenURL.hashCode() : 0);
        result = 31 * result + (restBaseURL != null ? restBaseURL.hashCode() : 0);
        result = 31 * result + (streamBaseURL != null ? streamBaseURL.hashCode() : 0);
        result = 31 * result + (userStreamBaseURL != null ? userStreamBaseURL.hashCode() : 0);
        result = 31 * result + (siteStreamBaseURL != null ? siteStreamBaseURL.hashCode() : 0);
        result = 31 * result + (uploadBaseURL != null ? uploadBaseURL.hashCode() : 0);
        result = 31 * result + (dispatcherImpl != null ? dispatcherImpl.hashCode() : 0);
        result = 31 * result + asyncNumThreads;
        result = 31 * result + (loggerFactory != null ? loggerFactory.hashCode() : 0);
        result = 31 * result + (int) (contributingTo ^ (contributingTo >>> 32));
        result = 31 * result + (includeMyRetweetEnabled ? 1 : 0);
        result = 31 * result + (includeEntitiesEnabled ? 1 : 0);
        result = 31 * result + (trimUserEnabled ? 1 : 0);
        result = 31 * result + (includeExtAltTextEnabled ? 1 : 0);
        result = 31 * result + (tweetModeExtended ? 1 : 0);
        result = 31 * result + (includeEmailEnabled ? 1 : 0);
        result = 31 * result + (jsonStoreEnabled ? 1 : 0);
        result = 31 * result + (mbeanEnabled ? 1 : 0);
        result = 31 * result + (userStreamRepliesAllEnabled ? 1 : 0);
        result = 31 * result + (userStreamWithFollowingsEnabled ? 1 : 0);
        result = 31 * result + (stallWarningsEnabled ? 1 : 0);
        result = 31 * result + (applicationOnlyAuthEnabled ? 1 : 0);
        result = 31 * result + (mediaProvider != null ? mediaProvider.hashCode() : 0);
        result = 31 * result + (mediaProviderAPIKey != null ? mediaProviderAPIKey.hashCode() : 0);
        result = 31 * result + (mediaProviderParameters != null ? mediaProviderParameters.hashCode() : 0);
        result = 31 * result + (daemonEnabled ? 1 : 0);
        result = 31 * result + (streamThreadName != null ? streamThreadName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ConfigurationBase{" +
                "debug=" + debug +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", httpConf=" + httpConf +
                ", httpStreamingReadTimeout=" + httpStreamingReadTimeout +
                ", httpRetryCount=" + httpRetryCount +
                ", httpRetryIntervalSeconds=" + httpRetryIntervalSeconds +
                ", oAuthConsumerKey='" + oAuthConsumerKey + '\'' +
                ", oAuthConsumerSecret='" + oAuthConsumerSecret + '\'' +
                ", oAuthAccessToken='" + oAuthAccessToken + '\'' +
                ", oAuthAccessTokenSecret='" + oAuthAccessTokenSecret + '\'' +
                ", oAuth2TokenType='" + oAuth2TokenType + '\'' +
                ", oAuth2AccessToken='" + oAuth2AccessToken + '\'' +
                ", oAuth2Scope='" + oAuth2Scope + '\'' +
                ", oAuthRequestTokenURL='" + oAuthRequestTokenURL + '\'' +
                ", oAuthAuthorizationURL='" + oAuthAuthorizationURL + '\'' +
                ", oAuthAccessTokenURL='" + oAuthAccessTokenURL + '\'' +
                ", oAuthAuthenticationURL='" + oAuthAuthenticationURL + '\'' +
                ", oAuth2TokenURL='" + oAuth2TokenURL + '\'' +
                ", oAuth2InvalidateTokenURL='" + oAuth2InvalidateTokenURL + '\'' +
                ", restBaseURL='" + restBaseURL + '\'' +
                ", streamBaseURL='" + streamBaseURL + '\'' +
                ", userStreamBaseURL='" + userStreamBaseURL + '\'' +
                ", siteStreamBaseURL='" + siteStreamBaseURL + '\'' +
                ", uploadBaseURL='" + uploadBaseURL + '\'' +
                ", dispatcherImpl='" + dispatcherImpl + '\'' +
                ", asyncNumThreads=" + asyncNumThreads +
                ", loggerFactory='" + loggerFactory + '\'' +
                ", contributingTo=" + contributingTo +
                ", includeMyRetweetEnabled=" + includeMyRetweetEnabled +
                ", includeEntitiesEnabled=" + includeEntitiesEnabled +
                ", trimUserEnabled=" + trimUserEnabled +
                ", includeExtAltTextEnabled=" + includeExtAltTextEnabled +
                ", tweetModeExtended=" + tweetModeExtended +
                ", includeEmailEnabled=" + includeEmailEnabled +
                ", jsonStoreEnabled=" + jsonStoreEnabled +
                ", mbeanEnabled=" + mbeanEnabled +
                ", userStreamRepliesAllEnabled=" + userStreamRepliesAllEnabled +
                ", userStreamWithFollowingsEnabled=" + userStreamWithFollowingsEnabled +
                ", stallWarningsEnabled=" + stallWarningsEnabled +
                ", applicationOnlyAuthEnabled=" + applicationOnlyAuthEnabled +
                ", mediaProvider='" + mediaProvider + '\'' +
                ", mediaProviderAPIKey='" + mediaProviderAPIKey + '\'' +
                ", mediaProviderParameters=" + mediaProviderParameters +
                ", daemonEnabled=" + daemonEnabled +
                ", streamThreadName='" + streamThreadName + '\'' +
                '}';
    }

    private static final List<ConfigurationBase> instances = new ArrayList<>();

    private static void cacheInstance(ConfigurationBase conf) {
        if (!instances.contains(conf)) {
            instances.add(conf);
        }
    }

    protected void cacheInstance() {
        cacheInstance(this);
    }

    private static ConfigurationBase getInstance(ConfigurationBase configurationBase) {
        int index;
        if ((index = instances.indexOf(configurationBase)) == -1) {
            instances.add(configurationBase);
            return configurationBase;
        } else {
            return instances.get(index);
        }
    }

    // assures equality after deserializedation
    @Serial
    protected Object readResolve() throws ObjectStreamException {
        return getInstance(this);
    }
}

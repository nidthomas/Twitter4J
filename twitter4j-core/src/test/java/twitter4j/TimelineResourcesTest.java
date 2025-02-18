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
package twitter4j;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.2.4
 */
class TimelineResourcesTest extends TwitterTestBase {

    @Test
    void testGetHomeTimeline() throws Exception {
        List<Status> statuses = twitter1.getHomeTimeline();
        assertTrue(0 < statuses.size());
        assertTrue(twitter1.getConfiguration().isJSONStoreEnabled());
        assertNotNull(TwitterObjectFactory.getRawJSON(statuses));
        assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
    }

    @Test
    void testUserTimeline() throws Exception {
        List<Status> statuses;
        statuses = twitter1.getUserTimeline();
        assertTrue(0 < statuses.size(), "size");
        try {
            statuses = twitter1.getUserTimeline("1000");
            assertNotNull(TwitterObjectFactory.getRawJSON(statuses));
            assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
            assertTrue(0 < statuses.size(), "size");
            assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
            assertEquals(9737332, statuses.get(0).getUser().getId());
            try {
                twitter1.getUserTimeline(1000);
            }catch(TwitterException te){
                // id 1000 / @percep2al is now protected
                assertEquals(401, te.getStatusCode());
            }
            statuses = twitter1.getUserTimeline(id1.screenName, new Paging().count(10));
            assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
            assertNotNull(TwitterObjectFactory.getRawJSON(statuses));
            assertTrue(0 < statuses.size(), "size");
            statuses = twitter1.getUserTimeline(id1.screenName, new Paging(999383469L));
            assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
            assertNotNull(TwitterObjectFactory.getRawJSON(statuses));
            assertTrue(0 < statuses.size(), "size");
        } catch (TwitterException te) {
            // is being rate limited
            assertEquals(400, te.getStatusCode());
        }

        statuses = twitter1.getUserTimeline(new Paging(999383469L));
        assertNotNull(TwitterObjectFactory.getRawJSON(statuses));
        assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
        assertTrue(0 < statuses.size(), "size");
        statuses = twitter1.getUserTimeline(new Paging(999383469L).count(15));
        assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
        assertNotNull(TwitterObjectFactory.getRawJSON(statuses));
        assertTrue(0 < statuses.size(), "size");


        statuses = twitter1.getUserTimeline(new Paging(1).count(30));
        assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
        assertNotNull(TwitterObjectFactory.getRawJSON(statuses));
        List<Status> statuses2 = twitter1.getUserTimeline(new Paging(2).count(15));
        assertEquals(statuses2.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses2.get(0))));
        assertEquals(statuses.get(statuses.size() - 1), statuses2.get(statuses2.size() - 1));
    }

    @Test
    void testGetMentions() throws Exception {
        Status status = twitter2.updateStatus("@" + id1.screenName + " reply to id1 " + new java.util.Date());
        assertNotNull(TwitterObjectFactory.getRawJSON(status));
        List<Status> statuses = twitter1.getMentionsTimeline();
        // mention can be invisible due to Twitter's spam isolation mechanism
        if (statuses.size() != 0) {
            assertNotNull(TwitterObjectFactory.getRawJSON(statuses));
            assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
            assertTrue(statuses.size() > 0);

            statuses = twitter1.getMentionsTimeline(new Paging(1));
            assertNotNull(TwitterObjectFactory.getRawJSON(statuses));
            assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
            assertTrue(statuses.size() > 0);
            statuses = twitter1.getMentionsTimeline(new Paging(1));
            assertNotNull(TwitterObjectFactory.getRawJSON(statuses));
            assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
            assertTrue(statuses.size() > 0);
            statuses = twitter1.getMentionsTimeline(new Paging(1, 1L));
            assertNotNull(TwitterObjectFactory.getRawJSON(statuses));
            assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
            assertTrue(statuses.size() > 0);
            statuses = twitter1.getMentionsTimeline(new Paging(1L));
            assertNotNull(TwitterObjectFactory.getRawJSON(statuses));
            assertEquals(statuses.get(0), TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(statuses.get(0))));
            assertTrue(statuses.size() > 0);
        }
    }

    @Test
    void testRetweetsOfMe() throws Exception {
        List<Status> statuses = twitter2.getRetweetsOfMe();
    }
}

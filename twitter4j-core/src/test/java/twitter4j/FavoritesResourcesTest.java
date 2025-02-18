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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.2.4
 */
class FavoritesResourcesTest extends TwitterTestBase {

    @Test
    void testFavoriteMethods() throws Exception {
        Status status = twitter1.getHomeTimeline().get(0);
        try {
            twitter2.destroyFavorite(status.getId());
        } catch (TwitterException te) {
        }
        assertNotNull(TwitterObjectFactory.getRawJSON(status));
        assertEquals(status, TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(status)));
        status = twitter2.createFavorite(status.getId());
        assertNotNull(TwitterObjectFactory.getRawJSON(status));
        assertEquals(status, TwitterObjectFactory.createStatus(TwitterObjectFactory.getRawJSON(status)));
        assertTrue(twitter2.getFavorites().size() > 0);
        assertTrue(twitter2.getFavorites("t4j_news").size() > 0);
        assertEquals(1, twitter2.getFavorites("t4j_news", new Paging().count(1)).size());
        long t4j_news_user_id = 72297675;
        assertTrue(twitter2.getFavorites(t4j_news_user_id).size() > 0);
        assertEquals(1, twitter2.getFavorites(t4j_news_user_id, new Paging().count(1)).size());
        try {
            twitter2.destroyFavorite(status.getId());
        } catch (TwitterException te) {
            // sometimes destroying favorite fails with 404
            assertEquals(404, te.getStatusCode());
        }
    }

}

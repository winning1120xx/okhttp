/*
 * Copyright (C) 2014 Square, Inc.
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
package okhttp3.internal.http2

import okhttp3.SimpleProvider
import okhttp3.internal.http2.hpackjson.HpackJsonUtil
import okhttp3.internal.http2.hpackjson.Story
import org.junit.jupiter.api.Assumptions.assumeFalse
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

class HpackDecodeInteropTest : HpackDecodeTestBase() {
  @ParameterizedTest
  @ArgumentsSource(StoriesTestProvider::class)
  fun testGoodDecoderInterop(story: Story) {
    assumeFalse(
      story === Story.MISSING,
      "Test stories missing, checkout git submodule"
    )
    testDecoder(story)
  }

  internal class StoriesTestProvider : SimpleProvider() {
    override fun arguments(): List<Any> = createStories(HpackJsonUtil.storiesForCurrentDraft())
  }
}

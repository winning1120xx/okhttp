/*
 * Copyright (C) 2018 Square, Inc.
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
package okhttp3

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import okhttp3.TestUtil.assumeNetwork
import okhttp3.internal.platform.ConscryptPlatform
import okhttp3.internal.platform.Platform
import okhttp3.testing.PlatformRule
import org.conscrypt.Conscrypt
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ConscryptTest {
  @JvmField @RegisterExtension val platform = PlatformRule()
  @JvmField @RegisterExtension val clientTestRule = OkHttpClientTestRule()

  private val client = clientTestRule.newClient()

  @BeforeEach fun setUp() {
    platform.assumeConscrypt()
  }

  @Test
  fun testTrustManager() {
    assertThat(Conscrypt.isConscrypt(Platform.get().platformTrustManager())).isTrue()
  }

  @Test
  @Disabled
  fun testMozilla() {
    assumeNetwork()

    val request = Request.Builder().url("https://mozilla.org/robots.txt").build()

    client.newCall(request).execute().use {
      assertThat(it.protocol).isEqualTo(Protocol.HTTP_2)
      assertThat(it.handshake!!.tlsVersion).isEqualTo(TlsVersion.TLS_1_3)
    }
  }

  @Test
  @Disabled
  fun testGoogle() {
    assumeNetwork()

    val request = Request.Builder().url("https://google.com/robots.txt").build()

    client.newCall(request).execute().use {
      assertThat(it.protocol).isEqualTo(Protocol.HTTP_2)
      if (it.handshake!!.tlsVersion != TlsVersion.TLS_1_3) {
        System.err.println("Flaky TLSv1.3 with google")
//    assertThat(it.handshake()!!.tlsVersion).isEqualTo(TlsVersion.TLS_1_3)
      }
    }
  }

  @Test
  fun testBuildIfSupported() {
    val actual = ConscryptPlatform.buildIfSupported()
    assertThat(actual).isNotNull()
  }

  @Test
  fun testVersion() {
    val version = Conscrypt.version()

    assertTrue(ConscryptPlatform.atLeastVersion(1, 4, 9))
    assertTrue(ConscryptPlatform.atLeastVersion(version.major()))
    assertTrue(ConscryptPlatform.atLeastVersion(version.major(), version.minor()))
    assertTrue(ConscryptPlatform.atLeastVersion(version.major(), version.minor(), version.patch()))
    assertFalse(ConscryptPlatform.atLeastVersion(version.major(), version.minor(), version.patch() + 1))
    assertFalse(ConscryptPlatform.atLeastVersion(version.major(), version.minor() + 1))
    assertFalse(ConscryptPlatform.atLeastVersion(version.major() + 1))
  }
}

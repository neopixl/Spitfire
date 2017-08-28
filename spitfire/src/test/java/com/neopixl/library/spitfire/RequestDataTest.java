/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.neopixl.library.spitfire;

import com.neopixl.library.spitfire.model.RequestData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class RequestDataTest {

    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        assertNotNull(RequestData.class.getMethod("getFileName"));
        assertNotNull(RequestData.class.getMethod("getContent"));
        assertNotNull(RequestData.class.getMethod("getType"));

        assertNotNull(RequestData.class.getMethod("clear"));

        assertNotNull(RequestData.class.getMethod("setFileName", String.class));
        assertNotNull(RequestData.class.getMethod("setContent", byte[].class));
        assertNotNull(RequestData.class.getMethod("setType", String.class));

        assertNotNull(RequestData.class.getConstructor(String.class, byte[].class));

        assertNotNull(RequestData.class.getConstructor(String.class, byte[].class, String.class));
    }
}

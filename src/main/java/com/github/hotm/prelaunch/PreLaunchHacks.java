/*
 * MIT License
 *
 * Copyright (c) 2020 i509VCB
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/*
 * Note: This class has been moved into the com.github.hotm.prelaunch package.
 */
package com.github.hotm.prelaunch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * So you want to Mixin into Authlib/Brigadier/DataFixerUpper, on Fabric you'll need this guy.
 *
 * <p>YOU SHOULD ONLY USE THIS CLASS DURING "preLaunch" and ONLY TARGET A CLASS WHICH IS NOT ANY CLASS YOU MIXIN TO.
 *
 * This will likely not work on Gson because FabricLoader has some special logic related to Gson.
 */
public final class PreLaunchHacks {
    private PreLaunchHacks() {}

    private static final ClassLoader KNOT_CLASSLOADER = Thread.currentThread().getContextClassLoader();
    private static final Method ADD_URL_METHOD;

    static {
        Method tempAddUrlMethod = null;
        try {
            tempAddUrlMethod = KNOT_CLASSLOADER.getClass().getMethod("addURL", URL.class);
            tempAddUrlMethod.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to load Classloader fields", e);
        }

        ADD_URL_METHOD = tempAddUrlMethod;
    }

    /**
     * Hackily load the package which a mixin may exist within.
     *
     * YOU SHOULD NOT TARGET A CLASS WHICH YOU MIXIN TO.
     *
     * @param pathOfAClass The path of any class within the package.
     */
    public static void hackilyLoadForMixin(String pathOfAClass) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        URL url = Class.forName(pathOfAClass).getProtectionDomain().getCodeSource().getLocation();
        ADD_URL_METHOD.invoke(KNOT_CLASSLOADER, url);
    }
}

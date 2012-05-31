/*
 * Copyright 2012 J.Boz and the original author or authors.
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
package com.javaboz.commons.test;

import junit.framework.Assert;

import org.junit.Test;

import com.javaboz.commons.test.NumberUtils;


/**
 * Test de la classe {@link NumberUtils}.
 * 
 * @author jboz
 */
public class NumberUtilsTest {

  @Test
  public void testToString() {
    Assert.assertEquals("1000015687", NumberUtils.toString(1000015687L));
    Assert.assertEquals("148977687.1", NumberUtils.toString(148977687.1));
    Assert.assertEquals("0.11", NumberUtils.toString(0.110));
    Assert.assertEquals("1.11", NumberUtils.toString(1.111111d));
    Assert.assertEquals("1.15", NumberUtils.toString(1.151111d));
    Assert.assertEquals("1.16", NumberUtils.toString(1.156111d));
  }
}

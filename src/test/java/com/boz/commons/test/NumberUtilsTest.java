package com.boz.commons.test;

import junit.framework.Assert;

import org.junit.Test;

import com.boz.commons.test.NumberUtils;


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

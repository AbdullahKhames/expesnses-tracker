package name.expenses.utils;

import junit.framework.TestCase;

public class PageUtilTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    public void testCreatePage() {
        assertEquals(2, PageUtil.createPage(1L, 10L, null, 12).getTotalPages());
    }
}
/*
 * Copyright 2016 Key Bridge.
 *
 * All rights reserved. Use is subject to license terms.
 * This software is protected by copyright.
 *
 * See the License for specific language governing permissions and
 * limitations under the License.
 */
package ch.keybridge.lib.faces.sso.client;

import java.security.GeneralSecurityException;
import junit.framework.TestCase;

/**
 *
 * @author Key Bridge LLC
 */
public class SSOSessionTest extends TestCase {

  public SSOSessionTest(String testName) {
    super(testName);
  }

  public void testCrypt() throws GeneralSecurityException {
    String key = "the key";
    String value = "the secret value";

    String crypt = SSOSession.encrypt(key, value);
    System.out.println("crypt " + crypt);
    String sh = "shortkey";

    System.out.println(String.format("%1$-" + 10 + "s", sh));
    sh = String.format("%16s", "foo").replace(' ', '*');
    System.out.println(sh);
    System.out.println(sh.length());
  }

}

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import java.util.Map;

/**
 * @author Jason van Zyl
 * @version $Id$
 */
public interface Contextualizable {
	public void contextualize(Map<String, Object> context);
}

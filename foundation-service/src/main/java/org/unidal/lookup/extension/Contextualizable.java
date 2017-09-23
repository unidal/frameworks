package org.unidal.lookup.extension;

import java.util.Map;

/**
 * @author Jason van Zyl
 * @version $Id$
 */
public interface Contextualizable {
	public void contextualize(Map<String, Object> context);
}

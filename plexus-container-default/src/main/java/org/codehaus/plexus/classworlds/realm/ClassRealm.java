package org.codehaus.plexus.classworlds.realm;

/*
 * Copyright 2001-2006 Codehaus Foundation.
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

import java.net.URL;
import java.net.URLClassLoader;

/**
 * The class loading gateway. Each class realm has access to a base class loader, imports form zero or more other class loaders, an
 * optional parent class loader and of course its own class path. When queried for a class/resource, a class realm will always query
 * its base class loader first before it delegates to a pluggable strategy. The strategy in turn controls the order in which
 * imported class loaders, the parent class loader and the realm itself are searched. The base class loader is assumed to be capable
 * of loading of the bootstrap classes.
 *
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author Jason van Zyl
 */
public class ClassRealm extends URLClassLoader {
   public ClassRealm(URL[] urls) {
      super(urls);
   }
}

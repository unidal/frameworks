package org.unidal.web.admin.config;

import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.AbstractModule;
import org.unidal.web.mvc.Module;
import org.unidal.web.mvc.annotation.ModuleMeta;
import org.unidal.web.mvc.annotation.ModulePagesMeta;

@ModuleMeta(name = "config", defaultInboundAction = "home", defaultTransition = "default", defaultErrorAction = "default")
@ModulePagesMeta({

org.unidal.web.admin.config.home.Handler.class,

org.unidal.web.admin.config.refresh.Handler.class,

})
@Named(type = Module.class, value = "org.unidal.web.admin.config.ConfigModule")
public class ConfigModule extends AbstractModule {

}

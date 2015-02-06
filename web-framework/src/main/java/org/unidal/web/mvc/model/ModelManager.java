package org.unidal.web.mvc.model;

import static org.unidal.lookup.util.StringUtils.isEmpty;
import static org.unidal.lookup.util.StringUtils.isNotEmpty;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.util.ReflectUtils;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.Module;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.ErrorActionMeta;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.ModuleMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;
import org.unidal.web.mvc.annotation.PreInboundActionMeta;
import org.unidal.web.mvc.annotation.TransitionMeta;
import org.unidal.web.mvc.annotation.ValidationMeta;
import org.unidal.web.mvc.model.entity.ErrorModel;
import org.unidal.web.mvc.model.entity.InboundActionModel;
import org.unidal.web.mvc.model.entity.ModuleModel;
import org.unidal.web.mvc.model.entity.OutboundActionModel;
import org.unidal.web.mvc.model.entity.TransitionModel;

public class ModelManager extends ContainerHolder implements Initializable {
	@Inject
	private ModuleRegistry m_registry;

	private Map<String, ModuleModel> m_modules = new HashMap<String, ModuleModel>();

	private void assertErrorExists(ModuleModel module, String errorActionName) {
		if (!module.getErrors().containsKey(errorActionName)) {
			throw new IllegalArgumentException("No method is annotated by @" + ErrorActionMeta.class.getSimpleName()
			      + "(name = \"" + errorActionName + "\") defined in " + module.getModuleClass());
		}
	}

	private void assertParameter(Method method) {
		Class<?>[] parameters = method.getParameterTypes();

		if (parameters.length != 1) {
			throw new IllegalArgumentException("Only one parameter is allowed by " + method);
		} else if (!ActionContext.class.isAssignableFrom(parameters[0])) {
			throw new IllegalArgumentException("Parameter(" + parameters[0] + ") of " + method + " msut be subclass of "
			      + ActionContext.class);
		}
	}

	private void assertTransitionExists(ModuleModel module, String transitionName) {
		if (!module.getTransitions().containsKey(transitionName)) {
			throw new IllegalArgumentException("No method is annotated by @" + TransitionMeta.class.getSimpleName()
			      + "(name = \"" + transitionName + "\") in " + module.getModuleClass());
		}
	}

	ModuleModel build(Module module) {
		Class<?> moduleClass = module.getClass();
		ModuleMeta moduleMeta = moduleClass.getAnnotation(ModuleMeta.class);

		if (moduleMeta == null) {
			throw new RuntimeException(moduleClass + " must be annotated by " + ModuleMeta.class);
		}

		ModuleModel model = buildModule(module, moduleMeta);

		validateModule(model);
		return model;
	}

	private ErrorModel buildError(Method method, ErrorActionMeta errorMeta) {
		assertParameter(method);

		ErrorModel error = new ErrorModel();

		error.setActionName(errorMeta.name());
		error.setMethod(method);
		return error;
	}

	private InboundActionModel buildInbound(ModuleModel module, Method method, InboundActionMeta inMeta,
	      PreInboundActionMeta preInMeta) {
		if (preInMeta != null && inMeta == null) {
			throw new RuntimeException(PreInboundActionMeta.class + " can only be used with " + InboundActionMeta.class
			      + " for " + method);
		}

		InboundActionModel existed = module.getInbounds().get(inMeta.name());

		if (existed != null) {
			throw new RuntimeException("Duplicated name(" + inMeta.name() + ") found between " + method.getName()
			      + "() and " + existed.getActionMethod().getName() + "() of " + module.getModuleClass());
		}

		assertParameter(method);

		InboundActionModel inbound = new InboundActionModel();

		inbound.setActionName(inMeta.name());
		inbound.setTransitionName(isEmpty(inMeta.transition()) ? module.getDefaultTransitionName() : inMeta.transition());
		inbound.setErrorActionName(isEmpty(inMeta.errorAction()) ? module.getDefaultErrorActionName() : inMeta
		      .errorAction());
		inbound.setActionMethod(method);
		inbound.setContextClass(method.getParameterTypes()[0]);

		if (preInMeta != null) {
			inbound.setPreActionNames(preInMeta.value());
		}

		PayloadMeta payloadMeta = method.getAnnotation(PayloadMeta.class);
		if (payloadMeta != null) {
			inbound.setPayloadClass(payloadMeta.value());
		}

		ValidationMeta validationMeta = method.getAnnotation(ValidationMeta.class);
		if (validationMeta != null) {
			for (Class<?> validationClass : validationMeta.value()) {
				inbound.addValidationClass(validationClass);
			}
		}

		return inbound;
	}

	private ModuleModel buildModule(Module module, ModuleMeta moduleMeta) {
		ModuleModel model = new ModuleModel();
		Class<?> moduleClass = module.getClass();

		model.setModuleName(moduleMeta.name());
		model.setModuleClass(moduleClass);
		model.setDefaultInboundActionName(moduleMeta.defaultInboundAction());
		model.setDefaultTransitionName(moduleMeta.defaultTransition());
		model.setDefaultErrorActionName(moduleMeta.defaultErrorAction());
		model.setActionResolverInstance(lookupOrNewInstance(moduleMeta.actionResolver()));
		model.setModuleInstance(module);

		buildModuleFromMethods(model, moduleClass.getMethods(), model.getModuleInstance());

		Class<? extends PageHandler<?>>[] pageHandlers = module.getPageHandlers();

		if (pageHandlers != null) {
			buildModuleFromHandlers(model, pageHandlers);
		}

		return model;
	}

	private void buildModuleFromHandlers(ModuleModel module, Class<? extends PageHandler<?>>[] handlerClasses) {
		for (Class<? extends PageHandler<?>> handlerClass : handlerClasses) {
			PageHandler<?> handler = lookup(handlerClass);

			buildModuleFromMethods(module, handlerClass.getMethods(), handler);
		}
	}

	private void buildModuleFromMethods(ModuleModel module, Method[] methods, Object instance) {
		for (Method method : methods) {
			int modifier = method.getModifiers();

			// ignore static and abstract methods
			if (Modifier.isStatic(modifier) || Modifier.isAbstract(modifier)) {
				continue;
			}

			InboundActionMeta inMeta = method.getAnnotation(InboundActionMeta.class);
			PreInboundActionMeta preInMeta = method.getAnnotation(PreInboundActionMeta.class);
			OutboundActionMeta outMeta = method.getAnnotation(OutboundActionMeta.class);
			TransitionMeta transitionMeta = method.getAnnotation(TransitionMeta.class);
			ErrorActionMeta errorMeta = method.getAnnotation(ErrorActionMeta.class);
			int num = (inMeta == null ? 0 : 1) + (outMeta == null ? 0 : 1) + (transitionMeta == null ? 0 : 1)
			      + (errorMeta == null ? 0 : 1);

			if (num == 0) {
				// Not annotated, ignored
				continue;
			} else if (num > 1) {
				throw new RuntimeException(method + " can only be annotated by one of " + InboundActionMeta.class + ", "
				      + OutboundActionMeta.class + ", " + TransitionMeta.class + " or " + ErrorActionMeta.class);
			}

			if (inMeta != null) {
				InboundActionModel inbound = buildInbound(module, method, inMeta, preInMeta);

				inbound.setModuleInstance(instance);
				module.addInbound(inbound);
			} else if (outMeta != null) {
				OutboundActionModel outbound = buildOutbound(module, method, outMeta);

				outbound.setModuleInstance(instance);
				module.addOutbound(outbound);
			} else if (transitionMeta != null) {
				TransitionModel transition = buildTransition(method, transitionMeta);

				transition.setModuleInstance(instance);

				if (!module.getTransitions().containsKey(transition.getTransitionName())) {
					module.addTransition(transition);
				}
			} else if (errorMeta != null) {
				ErrorModel error = buildError(method, errorMeta);

				error.setModuleInstance(instance);

				if (!module.getErrors().containsKey(error.getActionName())) {
					module.addError(error);
				}
			} else {
				throw new RuntimeException("Internal error!");
			}
		}
	}

	private OutboundActionModel buildOutbound(ModuleModel module, Method method, OutboundActionMeta outMeta) {
		assertParameter(method);

		OutboundActionModel outbound = new OutboundActionModel();

		outbound.setActionName(outMeta.name());
		outbound.setMethod(method);

		return outbound;
	}

	private TransitionModel buildTransition(Method method, TransitionMeta transitionMeta) {
		assertParameter(method);

		TransitionModel transition = new TransitionModel();

		transition.setTransitionName(transitionMeta.name());
		transition.setMethod(method);
		return transition;
	}

	public ModuleModel getModule(String name) {
		return m_modules.get(name);
	}

	public void initialize() throws InitializationException {
		Module defaultModule = m_registry.getDefaultModule();
		List<Module> modules = m_registry.getModules();

		for (Module module : modules) {
			register(module, defaultModule == module);
		}
	}

	protected <T> T lookupOrNewInstance(Class<T> clazz) {
		if (hasComponent(clazz)) {
			return lookup(clazz);
		} else {
			return ReflectUtils.createInstance(clazz);
		}
	}

	void register(Class<? extends Module> moduleClass) throws Exception {
		register(moduleClass.newInstance(), false);
	}

	void register(Module module, boolean defaultModule) {
		Class<? extends Module> moduleClass = module.getClass();
		ModuleModel model = build(module);
		ModuleModel oldModel = m_modules.put(model.getModuleName(), model);

		if (oldModel != null && oldModel.getModuleClass() != moduleClass) {
			throw new RuntimeException("Two modules(" + oldModel.getModuleClass() + " and " + moduleClass
			      + ") can't have same module name(" + model.getModuleName() + ").");
		}

		if (defaultModule) {
			m_modules.put(null, model);
		}
	}

	private void validateModule(ModuleModel module) {
		if (isNotEmpty(module.getDefaultTransitionName())) {
			assertTransitionExists(module, module.getDefaultTransitionName());
		}

		if (isNotEmpty(module.getDefaultErrorActionName())) {
			assertErrorExists(module, module.getDefaultErrorActionName());
		}

		for (InboundActionModel inbound : module.getInbounds().values()) {
			if (isEmpty(inbound.getTransitionName())) {
				throw new IllegalArgumentException("Please specify transition() of @"
				      + InboundActionMeta.class.getSimpleName() + " of " + inbound.getActionMethod());
			} else {
				assertTransitionExists(module, inbound.getTransitionName());
			}

			// error action is optional
			if (!isEmpty(inbound.getErrorActionName())) {
				assertErrorExists(module, inbound.getErrorActionName());
			}
		}
	}

}

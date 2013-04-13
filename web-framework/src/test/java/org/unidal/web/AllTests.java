package org.unidal.web;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.web.jsp.function.FormFunctionTest;
import org.unidal.web.jsp.function.FormatFunctionTest;
import org.unidal.web.jsp.function.MappingFunctionTest;
import org.unidal.web.jsp.function.ObjectFunctionTest;
import org.unidal.web.jsp.tag.ErrorTagTest;
import org.unidal.web.lifecycle.ActionResolverTest;
import org.unidal.web.mvc.model.AnnotationMatrixTest;
import org.unidal.web.mvc.model.ModuleManagerTest;
import org.unidal.web.mvc.payload.PayloadProviderTest;
import org.unidal.web.mvc.view.model.ModelHandlerTest;

@RunWith(Suite.class)
@SuiteClasses({

MvcTest.class,

FormatFunctionTest.class,

FormFunctionTest.class,

ObjectFunctionTest.class,

MappingFunctionTest.class,

ErrorTagTest.class,

ActionResolverTest.class,

AnnotationMatrixTest.class,

ModuleManagerTest.class,

PayloadProviderTest.class,

ModelHandlerTest.class

})
public class AllTests {

}

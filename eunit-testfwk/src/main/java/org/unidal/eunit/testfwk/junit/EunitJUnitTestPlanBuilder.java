package org.unidal.eunit.testfwk.junit;

import org.unidal.eunit.model.entity.EunitClass;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.model.transform.BaseVisitor;
import org.unidal.eunit.testfwk.EunitTaskType;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.ITestCase;
import org.unidal.eunit.testfwk.spi.ITestCaseBuilder;
import org.unidal.eunit.testfwk.spi.ITestPlan;
import org.unidal.eunit.testfwk.spi.ITestPlanBuilder;
import org.unidal.eunit.testfwk.spi.Registry;
import org.unidal.eunit.testfwk.spi.task.Task;

public class EunitJUnitTestPlanBuilder extends BaseVisitor implements ITestPlanBuilder<JUnitCallback> {
   private IClassContext m_ctx;

   private ITestPlan<JUnitCallback> m_plan;

   @Override
   @SuppressWarnings("unchecked")
   public void build(IClassContext ctx) {
      m_plan = (ITestPlan<JUnitCallback>) ctx.getTestPlan();
      m_ctx = ctx;

      ctx.forEunit().getEunitClass().accept(this);
      m_plan.executeDeferredActions();
   }

   @Override
   public void visitEunitClass(EunitClass eunitClass) {
      m_plan.addBeforeClass(new Task<EunitTaskType>(EunitTaskType.BEFORE_CLASS, null));

      super.visitEunitClass(eunitClass);

      m_plan.addAfterClass(new Task<EunitTaskType>(EunitTaskType.AFTER_CLASS, null));
   }

   @Override
   public void visitEunitMethod(EunitMethod eunitMethod) {
      if (eunitMethod.getBeforeAfter() != null) {
         if (!eunitMethod.isStatic()) {
            if (eunitMethod.isBeforeAfter()) { // @Before
               m_plan.addBefore(new Task<EunitTaskType>(EunitTaskType.METHOD, eunitMethod));
            } else { // @After
               m_plan.addAfter(new Task<EunitTaskType>(EunitTaskType.METHOD, eunitMethod));
            }
         } else {
            if (eunitMethod.isBeforeAfter()) { // @BeforeClass
               m_plan.addBeforeClass(new Task<EunitTaskType>(EunitTaskType.METHOD, eunitMethod));
            } else { // @AfterClass
               m_plan.addAfterClass(new Task<EunitTaskType>(EunitTaskType.METHOD, eunitMethod));
            }
         }
      } else if (eunitMethod.isTest()) { // @Test
         super.visitEunitMethod(eunitMethod);

         Registry registry = m_ctx.getRegistry();
         ITestCase<JUnitCallback> testCase;

//         if (!eunitMethod.isIgnored()) {
        @SuppressWarnings("unchecked")
        ITestCaseBuilder<JUnitCallback> builder = (ITestCaseBuilder<JUnitCallback>) registry.getTestCaseBuilder();

        testCase = builder.build(m_ctx, eunitMethod);
//         }

         m_plan.addTestCase(eunitMethod, testCase);
      }
   }
}

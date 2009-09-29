/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */

package org.amplafi.flow.web.resolvers;


import org.amplafi.flow.Flow;
import org.amplafi.flow.impl.FlowActivityImpl;
import org.amplafi.flow.FlowActivityImplementor;
import org.amplafi.flow.FlowDefinitionsManager;
import org.amplafi.flow.impl.FlowImpl;
import org.amplafi.flow.flowproperty.FlowPropertyDefinitionImpl;
import org.amplafi.flow.web.components.FlowBorder;
import org.amplafi.flow.web.components.FullFlowComponent;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.asset.ExternalResource;
import org.apache.tapestry.parse.ComponentTemplate;
import org.apache.tapestry.parse.ITemplateParser;
import org.apache.tapestry.parse.TemplateToken;
import org.apache.tapestry.parse.ITemplateParserDelegate;
import org.apache.tapestry.parse.TemplateParseException;
import org.apache.tapestry.resolver.ComponentSpecificationResolver;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.easymock.EasyMock;

import static org.amplafi.flow.web.resolvers.FlowAwareTemplateSourceDelegate.*;
import static org.easymock.classextension.EasyMock.*;
import java.util.List;
import java.util.Locale;

/**
 * Test {@link FlowAwareTemplateSourceDelegate}.
 */
public class TestFlowAwareTemplateSourceDelegate extends Assert {

    private static final String VALIDATORS = "<span jwcid=\"@flow:AttachFlowValidators\" validators=\"validators:flow\"/>";
    private static final String TEMPLATE_FORM =
        "<span jwcid=\"inF@If\" renderTag=\"false\" condition=\"ognl:insideForm\">" +
        "<span jwcid=\"@RenderBlock\" block=\"component:flowBlock\"/></span>" +
        "<span jwcid=\"orF@Else\" renderTag=\"false\">" +
        "<form jwcid=\"comp#0FlowForm@Form\" async=\"ognl:async\" clientValidationEnabled=\"true\" class=\"ognl:className\" " +
        "delegate=\"ognl:delegate\" cancel=\"listener:doCancelForm\" refresh=\"listener:doRefreshForm\" stateful=\"ognl:stateful\">" +
        "<span jwcid=\"@RenderBlock\" block=\"component:flowBlock\"/>" + VALIDATORS + "</form></span>";
    private static final String TEMPLATE_SUFFIX =
        "<span jwcid=\"@RenderBody\"/><div jwcid=\"@RenderBlock\" block=\"ognl:currentBlock\"/></div></div>\n</span>";
    private static final String TEMPLATE_PREFIX = TEMPLATE_FORM +
    "<span jwcid=\"flowBlock@Block\">" + "<div jwcid=\"" + VISIBLE_FLOW_IF
            + "@If\" condition=\"ognl:visibleFlow\" renderTag=\"false\">";

    @DataProvider(name="FlowAwareTemplateSourceDelegate")
    protected Object[][] getFlowAwareTemplateSourceDelegate() {
        FlowAwareTemplateSourceDelegate delegate =
            new FlowAwareTemplateSourceDelegate();
        delegate.setLog(LogFactory.getLog(this.getClass()));
        return new Object[][] { new Object[] {delegate}};
    }
    /**
     * Test a missing flow definition.
     * @param delegate
     *
     */
    @Test(dataProvider="FlowAwareTemplateSourceDelegate")
    public void testMissingFlow(FlowAwareTemplateSourceDelegate delegate) {
        String componentName = "NoSuch";
        IComponentSpecification compSpec = createSimpleCompSpec(componentName, FullFlowComponent.class);

        trainUsingMemoryLocation(compSpec);

        IComponent component = createMock(IComponent.class);
        expect(component.getSpecification()).andReturn(compSpec);
        trainGetFlowForComponent(componentName, null, delegate);
        trainParser(delegate);
        Locale locale = null;
        IRequestCycle cycle = createMock(IRequestCycle.class);

        replay(component, compSpec);
        ComponentTemplate bad = delegate.findTemplate(cycle, component, locale);
        assertEqualsExcludingWhitespace(new String(bad.getTemplateData()),
                "<div>[Flow "+componentName+" not found]</div>");
    }

    /**
     * Test a flow definition that has no activites.
     *
     */
    @Test(dataProvider="FlowAwareTemplateSourceDelegate")
    public void testEmptyFlow(FlowAwareTemplateSourceDelegate delegate) {
        String type = "Good";
        IComponentSpecification compSpec = createSimpleCompSpec(type, FullFlowComponent.class);
        IComponent component = createMock(IComponent.class);
        expect(component.getSpecification()).andReturn(compSpec);

        trainUsingMemoryLocation(compSpec);

        Flow flow = createSimpleFlow(type);
        trainGetFlowForComponent(type, flow, delegate);
        trainParser(delegate);
        Locale locale = null;
        IRequestCycle cycle = createMock(IRequestCycle.class);

        replay(component, compSpec);
        ComponentTemplate bad = delegate.findTemplate(cycle, component, locale);
        assertEqualsExcludingWhitespace(new String(bad.getTemplateData()),
                "<div>[Flow " + type + " has no activites]</div>");
    }

    /**
     * This flow has some activities.
     *
     */
    @Test(dataProvider="FlowAwareTemplateSourceDelegate")
    public void testSimple2Flow(FlowAwareTemplateSourceDelegate delegate) {
        String componentName = "comp#0";
        ComponentSpecificationResolver csr = createMock(ComponentSpecificationResolver.class);
        delegate.setComponentSpecificationResolver(csr);
        IComponentSpecification compSpec = createSimpleCompSpec(componentName, FullFlowComponent.class);

        trainUsingMemoryLocation(compSpec);

        IComponent component = createMock(IComponent.class);
        expect(component.getSpecification()).andReturn(compSpec);
        expect(component.getNamespace()).andReturn(null).anyTimes();
        expect(component.getLocation()).andReturn(null);
        Flow flow = createFlow2(componentName, 1);
        trainGetFlowForComponent(componentName, flow, delegate);
        trainParser(delegate);
        IRequestCycle cycle = createMock(IRequestCycle.class);
        csr.resolve(cycle, null, componentName, null);
        expect(csr.getSpecification()).andReturn(compSpec);
        expect(compSpec.getComponentClassName()).andReturn(FakeComponent.class.getName());

        programFlowBorder(csr, cycle);

        replay(component, csr, compSpec);
        Locale locale = null;
        ComponentTemplate good = delegate.findTemplate(cycle, component, locale);
        assertEqualsExcludingWhitespace(new String(good.getTemplateData()),
                TEMPLATE_PREFIX +
                "<div jwcid=\"fc0@Block\"><div jwcid=\"fic0@comp#0\" " +
                //ATTACH_OGNL+" " +
                "categorySelection=\"fprop:categorySelection\" " +
                "fooMessage=\"fprop:fooMessage=fic0@message:foo-message\"literalFling=\"fprop:literalFling=fic0@literal:fling\"" +
                "/></div>\n" + getFullFlowBorderTemplate() + TEMPLATE_SUFFIX);
    }
    /**
     * @param csr
     * @param cycle
     */
    private void programFlowBorder(ComponentSpecificationResolver csr, IRequestCycle cycle) {
        IComponentSpecification flowBorderComponentSpec = createSimpleCompSpec(FLOW_BORDER_COMPONENT, FlowBorder.class);
        csr.resolve(cycle, null, FLOW_BORDER_COMPONENT, null);
        expect(csr.getSpecification()).andReturn(flowBorderComponentSpec);
        expect(flowBorderComponentSpec.getComponentClassName()).andReturn(FlowBorder.class.getName());
        replay(flowBorderComponentSpec);
    }

    /**
     * This flow has some activities and properties.
     *
     */
    @Test(dataProvider="FlowAwareTemplateSourceDelegate")
    public void testFlowWithProperties(FlowAwareTemplateSourceDelegate delegate) {
        String componentName = "comp#0";
        ComponentSpecificationResolver csr = createMock(ComponentSpecificationResolver.class);
        delegate.setComponentSpecificationResolver(csr);
        IComponentSpecification compSpec = createSimpleCompSpec(componentName, FullFlowComponent.class);

        trainUsingMemoryLocation(compSpec);

        IComponent component = createMock(IComponent.class);
        expect(component.getSpecification()).andReturn(compSpec);
        expect(component.getNamespace()).andReturn(null).anyTimes();
        expect(component.getLocation()).andReturn(null);
        Flow flow = createFlowWithProperties(componentName);
        trainGetFlowForComponent(componentName, flow, delegate);
        trainParser(delegate);
        IRequestCycle cycle = createMock(IRequestCycle.class);
        csr.resolve(cycle, null, componentName, null);
        expect(csr.getSpecification()).andReturn(compSpec);
        expect(compSpec.getComponentClassName()).andReturn(FakeComponent.class.getName()).anyTimes();

        programFlowBorder(csr, cycle);

        replay(component, csr, compSpec, cycle);
        Locale locale = null;
        ComponentTemplate good = delegate.findTemplate(cycle, component, locale);
        assertEqualsExcludingWhitespace(new String(good.getTemplateData()),
                TEMPLATE_PREFIX +
                "<div jwcid=\"fc0@Block\"><div jwcid=\"fic0@comp#0\"  "+
                //"componentGlobaldef1=\"fprop:globaldef1\"  " +
                //"componentOverlapParameter=\"fprop:overlap\" " +
                //ATTACH_OGNL + " " +
                "categorySelection=\"fprop:categorySelection\" " +
                "fooMessage=\"fprop:fooMessage=fic0@message:foo-message\"literalFling=\"fprop:literalFling=fic0@literal:fling\"" +
                "/></div>\n" +
                getFullFlowBorderTemplate() +
                TEMPLATE_SUFFIX);
    }

    private void trainUsingMemoryLocation(IComponentSpecification compSpec) {
        Location location = createMock(Location.class);
        expect(compSpec.getLocation()).andReturn(location);
        replay(location);
        compSpec.setLocation(isA(Location.class));
    }

    private void trainGetFlowForComponent(String componentName, Flow flow, FlowAwareTemplateSourceDelegate delegate) {
        FlowDefinitionsManager flowDefinitionsManager = createMock(FlowDefinitionsManager.class);
        expect(flowDefinitionsManager.getFlowDefinition(componentName)).andReturn(flow);
        replay(flowDefinitionsManager);
        delegate.setFlowDefinitionsManager(flowDefinitionsManager);
    }

    private void trainParser(FlowAwareTemplateSourceDelegate delegate) {
        // we dont want to mock the world, so we just mock this
        ITemplateParser templateParser = createMock(ITemplateParser.class);
        delegate.setParser(templateParser);
        try {
            expect(templateParser.parse(EasyMock.isA(char[].class),
                    EasyMock.isA(ITemplateParserDelegate.class),
                    EasyMock.isA(Resource.class)
            )).andReturn(new TemplateToken[0]);
        } catch (TemplateParseException e) {
            fail();
        }
        replay(templateParser);
    }

    private Flow createFlowWithProperties(String componentName) {
        Flow flow = createFlow2(componentName, 1);
        FlowPropertyDefinitionImpl globalDef = new FlowPropertyDefinitionImpl("globaldef1");
        globalDef.setUiComponentParameterName("componentGlobaldef1");
        flow.addPropertyDefinitions(globalDef);
        FlowPropertyDefinitionImpl globalOverlap = new FlowPropertyDefinitionImpl("overlap");
        globalOverlap.setUiComponentParameterName("globalOverlapParameter");
        flow.addPropertyDefinitions(globalOverlap);
        FlowPropertyDefinitionImpl overlap  = new FlowPropertyDefinitionImpl("overlap");
        overlap.setUiComponentParameterName("componentOverlapParameter");
        ((FlowActivityImplementor)flow.getActivity(0)).addPropertyDefinitions(overlap);
        return flow;
    }

    private Flow createSimpleFlow(String flowTypeName) {
        Flow simple = new FlowImpl(flowTypeName);
        return simple;
    }

    private Flow createFlow2(String flowTypeName, int size) {
        Flow simple = new FlowImpl(flowTypeName);

        for (int i = 0; i < size; i++) {
            FlowActivityImpl activity = new FlowActivityImpl();
            activity.setComponentName("comp#"+i);
            simple.addActivity(activity);
        }
        return simple;
    }

    private IComponentSpecification createSimpleCompSpec(String componentName, Class<?> componentClass) throws NoSuchMethodError {
        IComponentSpecification compSpec = createMock(IComponentSpecification.class);
        expect(compSpec.getPublicId()).andReturn(null);
        expect(compSpec.getComponentClassName()).andReturn(componentClass.getName());
        expect(compSpec.getDescription()).andReturn(componentName);
        Resource res = new ExternalResource("dummy", null);
        expect(compSpec.getSpecificationLocation()).andReturn(res).anyTimes();
        return compSpec;
    }

    private String getFullFlowBorderTemplate() {
        return "<div jwcid=\"flowBorder@flow:FlowBorder\" hideFlowControl=\"ognl:hideFlowControl\" updateComponents=\"ognl:updateComponents\" "
        + "endListener=\"ognl:endListener\" cancelListener=\"ognl:cancelListener\" finishListener=\"ognl:finishListener\" "
        + "async=\"ognl:async\" nextListener=\"ognl:nextListener\" previousListener=\"ognl:previousListener\""
        + " fsFlowTransitions=\"fprop:fsFlowTransitions\""
        + " attachedFlowState=\"ognl:flowToUse\" updateListener=\"fprop:updateListener\""
        +">\n";
    }
    /**
     * Asserts that two strings are equal not taking into account whitespace differences.
     * @param one
     * @param two
     */
    protected void assertEqualsExcludingWhitespace(String one, String two) {
        assertEquals(StringUtils.join(one.split("\\s")),
                StringUtils.join(two.split("\\s")));
    }

    public abstract static class FakeComponent implements IComponent {
        @Parameter(required=true)
        public abstract List<Object> getCategorySelection();
        @Parameter(defaultValue="message:foo-message")
        public abstract String getFooMessage();
        @Parameter(defaultValue="literal:fling")
        public abstract String getLiteralFling();
    }
}

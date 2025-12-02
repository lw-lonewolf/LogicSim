package com.logisim;

import static org.junit.jupiter.api.Assertions.*;

import com.logisim.domain.components.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComponentTest {

    private static class ConcreteComponent extends Component {

        public ConcreteComponent() {
            super();
            this.inputs = new boolean[2];
            this.outputs = new boolean[2];
        }

        public ConcreteComponent(String name) {
            super(name);
            this.inputs = new boolean[2];
            this.outputs = new boolean[2];
        }

        @Override
        public void execute() {
            outputs[0] = inputs[0];
        }
    }

    private ConcreteComponent component;

    @BeforeEach
    void setUp() {
        component = new ConcreteComponent();
    }

    @Test
    void testDefaultConstructorGeneratesUuid() {
        assertNotNull(component.getUuid());
        assertFalse(component.getUuid().isEmpty());
    }

    @Test
    void testNamedConstructor() {
        ConcreteComponent namedComponent = new ConcreteComponent("TestName");
        assertEquals("TestName", namedComponent.getName());
        assertNotNull(namedComponent.getUuid());
    }

    @Test
    void testSetInput() {
        component.setInput(0, true);
        assertTrue(component.getInputs()[0]);
        component.setInput(1, false);
        assertFalse(component.getInputs()[1]);
    }

    @Test
    void testGetOutput() {
        boolean[] outputs = { true, false };
        component.setOutputs(outputs);
        assertTrue(component.getOutput(0));
        assertFalse(component.getOutput(1));
    }

    @Test
    void testSetName() {
        component.setName("UpdatedName");
        assertEquals("UpdatedName", component.getName());
    }

    @Test
    void testSetInputsArray() {
        boolean[] newInputs = { true, false, true };
        component.setInputs(newInputs);
        assertArrayEquals(newInputs, component.getInputs());
    }

    @Test
    void testSetOutputsArray() {
        boolean[] newOutputs = { false, true, false };
        component.setOutputs(newOutputs);
        assertArrayEquals(newOutputs, component.getOutputs());
    }

    @Test
    void testPositionX() {
        component.setPositionX(123.45);
        assertEquals(123.45, component.getPositionX());
    }

    @Test
    void testPositionY() {
        component.setPositionY(678.90);
        assertEquals(678.90, component.getPositionY());
    }

    @Test
    void testUuid() {
        String customUuid = "test-uuid-123";
        component.setUuid(customUuid);
        assertEquals(customUuid, component.getUuid());
    }
}

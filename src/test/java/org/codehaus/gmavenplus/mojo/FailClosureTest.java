package org.codehaus.gmavenplus.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;

public class FailClosureTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private FailClosure failClosure = FailClosure.INSTANCE;

    @Test
    public void callWithNullArguments() throws Exception {
        exceptionRule.expect(MojoExecutionException.class);
        exceptionRule.expectMessage("Failed");

        failClosure.call(null);
    }

    @Test
    public void callWithNoArguments() throws Exception {
        exceptionRule.expect(MojoExecutionException.class);
        exceptionRule.expectMessage("Failed");

        failClosure.call(new Object[0]);
    }

    @Test
    public void callWithCause() throws Exception {
        IllegalStateException cause = new IllegalStateException("Cause exception");
        exceptionRule.expect(MojoExecutionException.class);
        exceptionRule.expectCause(equalTo(cause));
        exceptionRule.expectMessage("Failed");

        failClosure.call(new Object[]{cause});
    }

    @Test
    public void callWithMessage() throws Exception {
        String message = "Error message";
        exceptionRule.expect(MojoExecutionException.class);
        exceptionRule.expectMessage(message);

        failClosure.call(new Object[]{message});
    }

    @Test
    public void callWithMessageObject() throws Exception {
        Message message = new Message("Error");
        exceptionRule.expect(MojoExecutionException.class);
        exceptionRule.expectMessage(message.toString());

        failClosure.call(new Object[]{message});
    }

    @Test
    public void callWithCauseAndMessage() throws Exception {
        IllegalStateException cause = new IllegalStateException("Cause exception");
        String message = "Error message";
        exceptionRule.expect(MojoExecutionException.class);
        exceptionRule.expectCause(equalTo(cause));
        exceptionRule.expectMessage(message);

        failClosure.call(new Object[]{message, cause});
    }

    @Test
    public void callWithCauseAndMessageObject() throws Exception {
        IllegalStateException cause = new IllegalStateException("Cause exception");
        Message message = new Message("Error");
        exceptionRule.expect(MojoExecutionException.class);
        exceptionRule.expectCause(equalTo(cause));
        exceptionRule.expectMessage(message.toString());

        failClosure.call(new Object[]{message, cause});
    }

    @Test
    public void callWithInvalidArguments() throws Exception {
        exceptionRule.expect(MojoFailureException.class);

        failClosure.call(new Object[]{new IllegalArgumentException("Cause"), singletonList("Something else")});
    }

    private static class Message {
        private final String body;

        Message(String messageBody) {
            this.body = messageBody;
        }

        @Override
        public String toString() {
            return "Message: " + body;
        }
    }

}
